package com.example.alea

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.readimagetext.ReadImageText
import kotlinx.coroutines.launch
import java.io.File

class ScannerActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val CAMERA_PERMISSION = Manifest.permission.CAMERA
    private val REQUEST_CAMERA_CAPTURE = 101

    private lateinit var ivImage: ImageView
    private lateinit var ivCaptureImage: ImageView
    private lateinit var btnContinue: Button
    private lateinit var btnAddImage: Button
    private lateinit var btnProcessImage: Button
    private lateinit var tvImageText: TextView

    private var imageUri: Uri? = null
    private lateinit var readImageText: ReadImageText

    private val cameraCaptureResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            ivImage.setImageURI(imageUri)
        }
    }

    private val imageChooseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let {
                ivImage.setImageURI(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_scanner_label)

        ivImage = findViewById(R.id.ivCaptureImage)
        ivCaptureImage = findViewById(R.id.ivCaptureImage)
        btnAddImage = findViewById(R.id.btnAdd)
        btnProcessImage = findViewById(R.id.btnProc)
        tvImageText = findViewById(R.id.tvResult)
        btnContinue = findViewById(R.id.btnContinue)

        btnAddImage.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            imageChooseLauncher.launch(intent)
        }

        btnProcessImage.setOnClickListener {
            if (ivImage.drawable != null) {
                val bitmapDrawable = ivImage.drawable as BitmapDrawable
                lifecycleScope.launch {
                    val text = readImageText.processImage(bitmapDrawable.bitmap, "spa")
                    tvImageText.text = text
                }
                btnContinue.visibility = Button.VISIBLE
            }
        }

        btnContinue.setOnClickListener {
            val intent = Intent(this, InfoLabelActivity::class.java)
            startActivity(intent)
        }

        ivCaptureImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(CAMERA_PERMISSION), REQUEST_CAMERA_CAPTURE)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA_PERMISSION), REQUEST_CAMERA_CAPTURE)
        }

        if (ContextCompat.checkSelfPermission(this, STORAGE_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, STORAGE_PERMISSION)) {
                ActivityCompat.requestPermissions(this, arrayOf(STORAGE_PERMISSION), 0)
            } else {
                val intent = Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
        } else {
            readImageText = ReadImageText(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        readImageText.recycle()
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            imageUri = createImageUri()
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        }
        cameraCaptureResult.launch(cameraIntent)
    }

    private fun createImageUri(): Uri {
        val imageFile = File(externalCacheDir, "photo_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(this, "$packageName.provider", imageFile)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_CAPTURE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uriToBitmap(uri: Uri?): Bitmap? {
        return try {
            uri?.let {
                contentResolver.openInputStream(it)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
