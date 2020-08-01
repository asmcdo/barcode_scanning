package com.example.barcodescanning

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.barcodescanning.barcodeScanner.BarcodeScannerActivity
import com.example.barcodescanning.result.ResultFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            ResultFragment.show(supportFragmentManager, result.data?.getStringExtra(BARCODE_INTENT))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (allPermissionsGranted()) {
            startListeners()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun startListeners() {
        barcodeScannerButton.setOnClickListener {
            openCameraActivity()
        }
    }

    private fun openCameraActivity() {
        val intent = Intent(this, BarcodeScannerActivity::class.java)
        startForResult.launch(intent)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val BARCODE_INTENT = "barcode_intent"
    }
}
