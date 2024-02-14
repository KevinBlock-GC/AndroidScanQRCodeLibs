package com.example.scanqrcodeexamples.decoders

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.scanqrcodeexamples.QRCodeDecoder
import com.journeyapps.barcodescanner.BarcodeView


@Composable
fun AndroidZxingEmbedded(onQRCodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    var barcodeView: BarcodeView? = remember { BarcodeView(context) }
    QRCodeDecoder(qrCodeDecoder = object : QRCodeDecoder {
        override fun onDispose() {
            stopPreview()
            barcodeView = null
        }
        override fun startPreview() {
            barcodeView?.resume()
        }

        override fun stopPreview() {
            barcodeView?.pause()
        }
    }) {
        AndroidView(
            factory = { _ ->
                barcodeView?.apply {
                    decodeContinuous { result ->
                        onQRCodeScanned(result.text)
                    }
                }
                barcodeView!!
            }
        )
    }
}