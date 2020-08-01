package com.example.barcodescanning.barcodeScanner

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.barcodescanning.R
import com.example.barcodescanning.result.ResultFragment
import kotlinx.android.synthetic.main.activity_barcode_scanner.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BarcodeScannerActivity : AppCompatActivity() {
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner)
        startCamera()
    }

    override fun onPostResume() {
        super.onPostResume()
        ResultFragment.dismiss(supportFragmentManager)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder().build()

            cameraExecutor = Executors.newSingleThreadExecutor()

            imageAnalyzer = ImageAnalysis.Builder().build()
                .also {
                    it.setAnalyzer(
                        cameraExecutor, BarcodeScannerAnalyzer(
                            onSuccessListener = { barcode ->
                                val intent = Intent().putExtra(BARCODE_INTENT, barcode.rawValue)
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            },
                            onFailureListener = { exception ->
                                Toast.makeText(
                                    this@BarcodeScannerActivity,
                                    exception.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                setResult(Activity.RESULT_CANCELED)
                                finish()
                            }
                        )
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
        private const val BARCODE_INTENT = "barcode_intent"
    }
}