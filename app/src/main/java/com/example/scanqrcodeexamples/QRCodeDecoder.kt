package com.example.scanqrcodeexamples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun QRCodeDecoder(qrCodeDecoder: QRCodeDecoder, content: @Composable () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                qrCodeDecoder.startPreview()
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                qrCodeDecoder.stopPreview()
            }
        }
        lifecycleOwner.addObserver(observer)
        onDispose {
            qrCodeDecoder.onDispose()
            lifecycleOwner.removeObserver(observer)
        }
    }
    content()
}

interface QRCodeDecoder {
    fun onDispose()
    fun startPreview()
    fun stopPreview()
}