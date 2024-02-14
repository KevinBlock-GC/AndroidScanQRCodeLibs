package com.example.scanqrcodeexamples.decoders

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.scanqrcodeexamples.QRCodeDecoder
import com.google.zxing.BarcodeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun YuriyBudiyevCodeScanner(onQRCodeScanned: (String) -> Unit) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var barcodeView: CodeScannerView? = remember { CodeScannerView(context) }
    var codeScanner: CodeScanner? = null

    QRCodeDecoder(qrCodeDecoder = object : QRCodeDecoder {
        override fun onDispose() {
            stopPreview()
            codeScanner?.releaseResources()
            barcodeView = null
            codeScanner = null
        }
        override fun startPreview() {
            codeScanner?.startPreview()
        }

        override fun stopPreview() {
            codeScanner?.stopPreview()
        }
    }) {
        AndroidView(
            modifier = Modifier.clipToBounds(),
            factory = { context ->
                barcodeView?.apply {
                    frameThickness = 0
                    codeScanner = CodeScanner(context, this).apply {
                        camera = CodeScanner.CAMERA_BACK
                        formats = listOf(BarcodeFormat.QR_CODE)
                        autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
                        scanMode = ScanMode.CONTINUOUS // or CONTINUOUS or PREVIEW
                        isAutoFocusEnabled = true // Whether to enable auto focus or not
                        isFlashEnabled = false // Whether to enable flash or not
                        // Callbacks
                        decodeCallback = DecodeCallback {
                            coroutineScope.launch(Dispatchers.IO) {
                                onQRCodeScanned(it.text)
                            }
                        }
                    }
                    clipChildren = true
                }
                barcodeView!!
            }
        )
    }
}