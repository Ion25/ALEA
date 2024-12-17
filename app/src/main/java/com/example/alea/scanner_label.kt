package com.example.alea

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.readimagetext.ReadImageText
import kotlinx.coroutines.launch

class ScannerFragment : Fragment(R.layout.fragment_scanner_label) {

    private val STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE

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
        super.onViewCreated(view, savedInstanceState)

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
                lifecycleScope.launch {
                    val bitmapDrawable: BitmapDrawable = ivImage.drawable as BitmapDrawable
                    tvImageText.text = readImageText.processImage(bitmapDrawable.bitmap, "spa")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()



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
}
