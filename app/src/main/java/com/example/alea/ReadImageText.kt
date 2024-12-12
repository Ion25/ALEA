package com.example.alea

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.InputStream

class ReadImageText (val context: Context){
    private val tess = TessBaseAPI()
    private val folderTessDataName : String = "tessdata";
    private val pathDir = context.getExternalFilesDir(null).toString()

    init{
        val folder = File(pathDir, folderTessDataName)

        if(!folder.exists()){
            folder.mkdir();
        }
        if(folder.exists()){
            addFile("spa.traineddata",R.raw.spa);
            addFile("eng.traineddata",R.raw.eng)
        }
    }
    private fun addFile(name: String, fileResource: Int){
        val file = File("$pathDir/$folderTessDataName/$name")
        if(!file.exists()){
            val inputStream: InputStream = context.resources.openRawResource(fileResource)
            file.appendBytes(inputStream.readBytes())
            file.createNewFile()
        }
    }
    fun processImage (image: Bitmap, lang:String) : String{
        tess.init(pathDir, lang)
        tess.setImage(image)
        return tess.utF8Text
    }
    fun recycle(){
        tess.recycle()
    }
}