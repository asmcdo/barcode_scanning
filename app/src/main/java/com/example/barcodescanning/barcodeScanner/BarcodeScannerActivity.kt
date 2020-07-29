package com.example.barcodescanning.barcodeScanner

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.barcodescanning.R
import kotlinx.android.synthetic.main.activity_barcode_scanner.*
import java.util.concurrent.ExecutorService

class BarcodeScannerActivity() : AppCompatActivity() {
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_barcode_scanner)
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder().build()

            imageAnalyzer = ImageAnalysis.Builder().build()
                .also {
                    it.setAnalyzer(
                        cameraExecutor, BarcodeScannerAnalyzer { barcode ->
                            println(barcode.rawValue)
                        }
                    )
                }

            val cameraSelector =
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                cameraProvider.unbindAll()

                camera =
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)

                preview?.setSurfaceProvider(scannerPreview.createSurfaceProvider())


            } catch (ex: Exception) {
                Log.e(TAG, "UseCase binding failed", ex)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    companion object {
        private const val TAG = "CameraX"
    }
}