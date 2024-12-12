package com.example.alea

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import androidx.core.content.ContextCompat

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable

import androidx.activity.result.contract.ActivityResultContracts

import androidx.lifecycle.lifecycleScope

import kotlinx.coroutines.launch

class camera : Fragment() {



    private lateinit var btnAddImage : Button
    private lateinit var btnProcessImage : Button
    private lateinit var ivImage : ImageView
    private lateinit var tvImageText : TextView

    private lateinit var readImageText : ReadImageText

    private val imageChose = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode== Activity.RESULT_OK)
            ivImage.setImageURI(it.data?.data)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val view = inflater.inflate(R.layout.fragment_camera,container,false)


        btnAddImage = view.findViewById(R.id.btnAdd)
        btnProcessImage = view.findViewById(R.id.btnProc)
        ivImage = view.findViewById(R.id.ivSource)
        tvImageText = view.findViewById(R.id.tvResult)

        btnAddImage.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            imageChose.launch(intent)

        }
        btnProcessImage.setOnClickListener{

            if(ivImage.drawable != null){
                lifecycleScope.launch{
                    val bitmapDrawable : BitmapDrawable = ivImage.drawable as BitmapDrawable
                    tvImageText.text= readImageText.processImage(bitmapDrawable.bitmap, "spa")
                }
            }
        }
        return view
    }
    override fun onResume(){
        super.onResume()


        readImageText = ReadImageText(requireContext())
    }
    override fun onDestroy(){
        super.onDestroy()
        readImageText.recycle()
    }
}