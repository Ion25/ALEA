package com.example.alea

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

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

        // Selector para Sexo
        cardSexo.setOnClickListener {
            val opciones = arrayOf("Hombre", "Mujer")
            AlertDialog.Builder(this)
                .setTitle("Selecciona tu sexo")
                .setItems(opciones) { _, which ->
                    tvSexo.text = "Sexo: ${opciones[which]}"
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
        val genero = if (sexo.contains("Hombre")) "Hombre" else "Mujer"

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
