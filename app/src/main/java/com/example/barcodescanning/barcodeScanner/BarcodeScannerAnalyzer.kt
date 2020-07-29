package com.example.barcodescanning.barcodeScanner

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.OnFailureListener
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class BarcodeScannerAnalyzer(private val onSuccessListener: (Barcode) -> Unit) : ImageAnalysis.Analyzer {
    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val options = BarcodeScannerOptions.Builder()
            .build()

        val mediaImage = imageProxy.image

        mediaImage?.let { it ->
            val image = InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees)

            val scanner = BarcodeScanning.getClient(options)

            scanner.process(image)
                .addOnSuccessListener { barcodeList ->
                    onSuccessListener(barcodeList.first())
                }
                .addOnFailureListener { exception ->
                    throw(exception)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}