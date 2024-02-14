package com.example.scanqrcodeexamples.decoders

import android.util.Size
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.example.scanqrcodeexamples.QRCodeDecoder
import com.example.scanqrcodeexamples.mlkit.QRCodeAnalyzer
import java.util.concurrent.Executors

@Composable
fun MLKitQRScanner(onQRCodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    var previewView: PreviewView? = remember { PreviewView(context) }
    val cameraController = remember { LifecycleCameraController(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    val lifecycleOwner = LocalLifecycleOwner.current

    previewView!!.apply {
        scaleType = PreviewView.ScaleType.FILL_CENTER
        implementationMode = PreviewView.ImplementationMode.PERFORMANCE
    }.also {
        it.controller = cameraController
        cameraController.previewTargetSize = CameraController.OutputSize(Size(1200, 1600))
        cameraController.setImageAnalysisAnalyzer(executor, QRCodeAnalyzer(onQRCodeScanned))
        cameraController.bindToLifecycle(lifecycleOwner)
    }

    QRCodeDecoder(qrCodeDecoder = object : QRCodeDecoder {
        override fun onDispose() {
            stopPreview()
            previewView = null
            cameraController.clearImageAnalysisAnalyzer()
        }
        override fun startPreview() {
            cameraController.bindToLifecycle(lifecycleOwner)
        }

        override fun stopPreview() {
            cameraController.unbind()
        }
    }) {
        AndroidView(
            modifier = Modifier.fillMaxSize().clipToBounds(),
            factory = { previewView!! }
        )
    }
}