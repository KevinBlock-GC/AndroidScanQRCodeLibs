package com.example.scanqrcodeexamples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.scanqrcodeexamples.decoders.AndroidZxingEmbedded
import com.example.scanqrcodeexamples.decoders.MLKitQRScanner
import com.example.scanqrcodeexamples.decoders.YuriyBudiyevCodeScanner
import com.example.scanqrcodeexamples.ui.theme.ScanQRCodeExamplesTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

sealed class CameraProvider {
    abstract val name: String
    @Composable abstract fun Render(onQRCodeScanned: (String) -> Unit)

    data object AndroidZxingEmbedded: CameraProvider() {
         override val name: String = "Android Zxing Embedded"
         @Composable
         override fun Render(onQRCodeScanned: (String) -> Unit) {
             AndroidZxingEmbedded(onQRCodeScanned)
         }
     }

    data object YuriyBudiyevCodeScanner: CameraProvider() {
        override val name: String = "Yuriy Budiyev Code Scanner"
        @Composable
        override fun Render(onQRCodeScanned: (String) -> Unit) {
            YuriyBudiyevCodeScanner(onQRCodeScanned)
        }
    }

    data object MLKitQRScanner: CameraProvider() {
        override val name: String = "ML Kit QR Scanner"
        @Composable
        override fun Render(onQRCodeScanned: (String) -> Unit) {
            MLKitQRScanner(onQRCodeScanned)
        }
    }
}

private val Providers = listOf(
    CameraProvider.AndroidZxingEmbedded,
    CameraProvider.YuriyBudiyevCodeScanner,
    CameraProvider.MLKitQRScanner
)

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val defaultButtonColors = ButtonDefaults.buttonColors()
            val selectedButtonColors = ButtonDefaults.buttonColors(
                containerColor = Color.Green,
                contentColor = Color.White
            )
            var scannedTexts by remember { mutableStateOf<List<String>>(emptyList()) }
            val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
            var cameraProvider by remember { mutableStateOf<CameraProvider?>(null) }
            ScanQRCodeExamplesTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.then(if (cameraProvider != null) Modifier else Modifier.fillMaxHeight())
                    ) {
                        if (cameraPermissionState.status != PermissionStatus.Granted) {
                            AskForCameraPermission(permissionState = cameraPermissionState)
                        } else {
                            Providers.forEach { provider ->
                                Button(
                                    colors = if (cameraProvider == provider) selectedButtonColors else defaultButtonColors,
                                    onClick = {
                                        cameraProvider = provider
                                    }) {
                                    Text("Use ${provider.name}")
                                }
                            }
                        }
                    }
                    if (cameraProvider != null) {
                        Column(modifier = Modifier.weight(1f)) {
                            if (cameraPermissionState.status == PermissionStatus.Granted) {
                                println("cameraProvider: $cameraProvider")
                                CameraPreview(cameraProvider = cameraProvider) {
                                    scannedTexts = scannedTexts.toMutableList().apply {
                                        add(it)
                                    }
                                }
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Scanned Codes")
                        LazyColumn {
                            val size = scannedTexts.size
                            itemsIndexed(scannedTexts.reversed()) { index, text ->
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "${size - index}. $text"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CameraPreview(cameraProvider: CameraProvider?, onQRCodeScanned: (String) -> Unit) =
    cameraProvider?.Render(onQRCodeScanned)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AskForCameraPermission(permissionState: PermissionState) {
    Button(onClick = {
        permissionState.launchPermissionRequest()
    }) {
        Text("Request Camera Permission")
    }
}