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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.readimagetext.ReadImageText
import kotlinx.coroutines.launch
import java.io.File

class ScannerFragment : Fragment(R.layout.fragment_scanner_label) {

    private val STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE

    private val CAMERA_PERMISSION = Manifest.permission.CAMERA

    private val REQUEST_CAMERA_CAPTURE = 101
    private lateinit var ivCaptureImage: ImageView
    private var imageUri: Uri? = null
    private val cameraCaptureResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            ivImage.setImageURI(imageUri)
        }
    }


    private lateinit var btnAddImage: Button
    private lateinit var btnProcessImage: Button
    private lateinit var ivImage: ImageView
    private lateinit var tvImageText: TextView

    private lateinit var readImageText: ReadImageText

    private val imageChose = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK)
            ivImage.setImageURI(it.data?.data)
    }




    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,  savedInstanceState)

        btnAddImage = view.findViewById(R.id.btnAdd)
        btnProcessImage = view.findViewById(R.id.btnProc)
        ivImage = view.findViewById(R.id.ivSource)
        tvImageText = view.findViewById(R.id.tvResult)

        btnAddImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            imageChose.launch(intent)
        }

        btnProcessImage.setOnClickListener {
            if (ivImage.drawable != null) {
                // Convertir la URI de la imagen a Bitmap
                val bitmap = uriToBitmap(imageUri)
                if (bitmap != null) {
                    lifecycleScope.launch {
                        // Procesar la imagen con Tesseract
                        val text = readImageText.processImage(bitmap, "spa")
                        tvImageText.text = text // Mostrar el texto procesado en el TextView
                    }
                } else {
                    Toast.makeText(requireContext(), "No se pudo procesar la imagen.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        ivCaptureImage = view.findViewById(R.id.ivCaptureImage)

        ivCaptureImage.setOnClickListener {
            // Comprobar si se tiene permiso para usar la cámara
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    CAMERA_PERMISSION
                ) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                // Si no se tiene permiso, pedirlo
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(CAMERA_PERMISSION),
                    REQUEST_CAMERA_CAPTURE
                )
            }
        }

    }

    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                CAMERA_PERMISSION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(CAMERA_PERMISSION),
                REQUEST_CAMERA_CAPTURE
            )
        }

        val permissionCheck = ContextCompat.checkSelfPermission(
            requireContext(), STORAGE_PERMISSION
        )

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // Not allowed permission Storage
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), STORAGE_PERMISSION)) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(STORAGE_PERMISSION), 0)
            } else {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.fromParts("package", requireContext().packageName, null)
                startActivity(intent)
            }
        } else {
            // Storage permissions are allowed
            readImageText = ReadImageText(requireContext())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        readImageText.recycle()
    }
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imageUri = createImageUri()
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraCaptureResult.launch(cameraIntent)
    }

    private fun createImageUri(): Uri {
        val imageFile = File(requireContext().externalCacheDir, "photo_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            requireContext(),
            "com.example.alea.ScannerFragment",
            imageFile
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_CAPTURE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                // El usuario no concedió el permiso
                Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun uriToBitmap(uri: Uri?): Bitmap? {
        return uri?.let {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

}
