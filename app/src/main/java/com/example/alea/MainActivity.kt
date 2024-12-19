package com.example.alea

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.content.Intent
import android.widget.Button
import android.widget.Toast
import android.widget.VideoView
import android.net.Uri

class MainActivity : AppCompatActivity() {

    private lateinit var avatarView: AvatarView  // Referencia al avatar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencia al AvatarView
        avatarView = findViewById(R.id.avatarView)

        val cardSexo = findViewById<CardView>(R.id.cardSexo)
        val tvSexo = findViewById<TextView>(R.id.tvSexo)

        val cardEdad = findViewById<CardView>(R.id.cardEdad)
        val tvEdad = findViewById<TextView>(R.id.tvEdad)

        val cardAltura = findViewById<CardView>(R.id.cardAltura)
        val tvAltura = findViewById<TextView>(R.id.tvAltura)

        val cardPeso = findViewById<CardView>(R.id.cardPeso)
        val tvPeso = findViewById<TextView>(R.id.tvPeso)

        // Botón para abrir ScannerAvatar
        val btnOpenScanner = findViewById<Button>(R.id.btnOpenScanner)
        btnOpenScanner.setOnClickListener {
            val intent = Intent(this, ScannerAvatar::class.java)
            startActivity(intent)
        }

        // Campos del formulario

        val edadField: TextView = findViewById(R.id.tvEdad)
        val pesoField: TextView = findViewById(R.id.tvPeso)
        val alturaField: TextView = findViewById(R.id.tvAltura)
        val generoField: TextView = findViewById(R.id.tvSexo)

        // Recibir datos del Intent
        val intent = intent
        val edad = intent.getStringExtra("edad")
        val peso = intent.getStringExtra("peso")
        val altura = intent.getStringExtra("altura")
        val genero = intent.getStringExtra("genero")
        val btnContinuar: Button = findViewById(R.id.btnContinuar)
        // Llenar el formulario con los datos recibidos

        edadField.text = "Edad: ${edad ?: ""}"
        pesoField.text = "Peso: ${peso ?: ""} kg"
        alturaField.text = "Altura: ${altura ?: ""} cm"
        generoField.text = "Género: ${genero ?: ""}"
        // Verificar que los datos del escaneo no sean nulos o estén en blanco
        if (!genero.isNullOrBlank() && !altura.isNullOrBlank() && !peso.isNullOrBlank()) {
            // Actualizar el avatar con los datos
            actualizarAvatar(sexo = genero, altura = altura, peso = peso)
        } else {
            // Mostrar un mensaje si los datos no están disponibles
            Toast.makeText(this, "Realiza un escaneo para actualizar el avatar.", Toast.LENGTH_SHORT).show()
        }



        // Selector para Sexo
        cardSexo.setOnClickListener {
            val opciones = arrayOf("Masculino", "Femenino")
            AlertDialog.Builder(this)
                .setTitle("Selecciona tu genero")
                .setItems(opciones) { _, which ->
                    tvSexo.text = "Genero: ${opciones[which]}"
                    actualizarAvatar(tvSexo.text.toString(), tvAltura.text.toString(), tvPeso.text.toString())
                }.show()
        }

        // Selector para Edad
        cardEdad.setOnClickListener {
            showNumberPicker("Edad", 18, 99) { value ->
                tvEdad.text = "Edad: $value años"
            }
        }

        // Selector para Altura
        cardAltura.setOnClickListener {
            showNumberPicker("Altura", 100, 250) { value ->
                tvAltura.text = "Altura: $value cm"
                actualizarAvatar(tvSexo.text.toString(), tvAltura.text.toString(), tvPeso.text.toString())
            }
        }

        // Selector para Peso
        cardPeso.setOnClickListener {
            showNumberPicker("Peso", 30, 200) { value ->
                tvPeso.text = "Peso: $value kg"
                actualizarAvatar(tvSexo.text.toString(), tvAltura.text.toString(), tvPeso.text.toString())
            }
        }
        val videoView: VideoView = findViewById(R.id.videoViewFondo)

        // Ruta del video en la carpeta raw
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.fondo)

// Configurar el VideoView para reproducir el video
        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true // Hacer que el video se repita
        }

        videoView.start() // Iniciar la reproducción del video
        btnContinuar.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("gender", genero)
            intent.putExtra("height", altura)
            intent.putExtra("weight", peso)
            startActivity(intent)
        }


    }

    private fun showNumberPicker(title: String, min: Int, max: Int, onValueSelected: (Int) -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_number_picker, null)
        val numberPicker = dialogView.findViewById<NumberPicker>(R.id.numberPicker)
        numberPicker.minValue = min
        numberPicker.maxValue = max
        numberPicker.value = min

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                onValueSelected(numberPicker.value)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Método para actualizar el avatar dinámicamente
    private fun actualizarAvatar(sexo: String, altura: String, peso: String) {
        val genero = if (sexo.contains("Masculino")) "Masculino" else "Femenino"

        // Convertir peso a número entero
        val pesoNumerico = peso.filter { it.isDigit() }.toIntOrNull() ?: 0

        // Categorizar peso en rangos
        val pesoSimplificado = when {
            pesoNumerico in 30..60 -> "Ligero"
            pesoNumerico in 61..75 -> "Medio"
            pesoNumerico > 76 -> "Pesado"
            else -> "Pesado" // Para valores fuera del rango esperado
        }

        avatarView.updateAvatar(genero, altura, pesoSimplificado)
    }


}
