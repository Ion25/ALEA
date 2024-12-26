package com.example.alea.ui.ScannerInfo

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
import com.example.alea.BaseActivity
import com.example.alea.MainActivity
import com.example.alea.ProfileActivity
import com.example.alea.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.io.File

class ScannerActivity : BaseActivity.BaseActivity() {


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
            openCamera()

        }
    }

    override fun onResume() {
        super.onResume()


        readImageText = ReadImageText(this)

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
