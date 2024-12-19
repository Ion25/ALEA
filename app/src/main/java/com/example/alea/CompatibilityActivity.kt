package com.example.alea

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale
import kotlin.random.Random

class CompatibilityActivity : AppCompatActivity() {
    private lateinit var textViewMessage: TextView
    private lateinit var btnClearCircle: View
    private lateinit var recyclerViewCategories: RecyclerView
    private lateinit var recyclerViewFoodList: RecyclerView
    private lateinit var circleContainer: FrameLayout
    private lateinit var mainLayout: View

    private var currentCategory: String? = null
    private var lastFoodImage: Int? = null // Último alimento agregado al plato

    // Mapa de compatibilidades
    private val compatibilities = mapOf(
        R.drawable.carne to listOf(R.drawable.arroz, R.drawable.papas),
        R.drawable.pollo to listOf(R.drawable.arroz),
        R.drawable.pepinillo to listOf(R.drawable.lechuga),
        R.drawable.manzana to listOf(R.drawable.platano),
        R.drawable.queso to listOf(R.drawable.leche)
    )

    // Datos de ejemplo
    private val categories = listOf("Carnes", "Vegetales", "Frutas", "Granos", "Lácteos")
    private val foodByCategory = mapOf(
        "Carnes" to listOf(R.drawable.carne, R.drawable.pollo),
        "Vegetales" to listOf(R.drawable.pepinillo, R.drawable.lechuga),
        "Frutas" to listOf(R.drawable.manzana, R.drawable.platano),
        "Granos" to listOf(R.drawable.arroz, R.drawable.quinoa),
        "Lácteos" to listOf(R.drawable.queso, R.drawable.leche)
    )

    private val REQUEST_CODE_SPEECH_INPUT = 1001
    private val foodNameToImage = mapOf(
        "pollo" to R.drawable.pollo,
        "carne" to R.drawable.carne,
        "pepinillo" to R.drawable.pepinillo,
        "lechuga" to R.drawable.lechuga,
        "manzana" to R.drawable.manzana,
        "platano" to R.drawable.platano,
        "arroz" to R.drawable.arroz,
        "quinoa" to R.drawable.quinoa,
        "queso" to R.drawable.queso,
        "leche" to R.drawable.leche,
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compatibility)

        btnClearCircle = findViewById(R.id.btnClearCircle)
        textViewMessage = findViewById(R.id.textViewMessage)
        circleContainer = findViewById(R.id.platoContainer)
        mainLayout = findViewById(R.id.mainLayout)

        recyclerViewCategories = findViewById(R.id.recyclerViewCategories)
        recyclerViewFoodList = findViewById(R.id.recyclerViewFoodList)

        val btnMicrophone = findViewById<ImageButton>(R.id.btnMicrophone)
        btnMicrophone.setOnClickListener {
            startVoiceRecognition()
        }

        // Configurar RecyclerView para categorías
        recyclerViewCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewCategories.adapter = CategoryAdapter(categories) { categoryName ->
            toggleFoodList(categoryName)
        }

        // Configurar RecyclerView para lista de alimentos
        recyclerViewFoodList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewFoodList.adapter = FoodAdapter(emptyList()) { selectedFoodImage ->
            onFoodSelected(selectedFoodImage)
        }

        // Configurar botón para vaciar el plato
        btnClearCircle.setOnClickListener {
            resetPlate()
        }
    }

    private fun onFoodSelected(foodImage: Int) {
        // Verifica si ya hay un alimento en el plato
        if (lastFoodImage != null) {
            val isCompatible = compatibilities[lastFoodImage]?.contains(foodImage) ?: true

            if (isCompatible) {
                addFoodToPlate(foodImage)
                textViewMessage.text = "¡Alimento agregado al plato!"
                setBackgroundColor(Color.GREEN) // Fondo verde para compatibles
            } else {
                textViewMessage.text = "¡Alimento incompatible!"
                setBackgroundColor(Color.RED) // Fondo rojo para incompatibles
            }
        } else {
            // Para el primer alimento, simplemente lo agrega sin cambiar el fondo
            addFoodToPlate(foodImage)
            textViewMessage.text = "¡Primer alimento agregado al plato!"
        }

        // Actualiza el último alimento seleccionado
        lastFoodImage = foodImage
    }

    private fun toggleFoodList(categoryName: String) {
        if (currentCategory == categoryName) {
            currentCategory = null
            recyclerViewFoodList.visibility = RecyclerView.GONE
        } else {
            currentCategory = categoryName
            val foodImages = foodByCategory[categoryName] ?: emptyList()
            recyclerViewFoodList.adapter = FoodAdapter(foodImages) { selectedFoodImage ->
                onFoodSelected(selectedFoodImage)
            }
            recyclerViewFoodList.visibility = RecyclerView.VISIBLE
        }
    }

    private fun resetPlate() {
        circleContainer.removeAllViews()
        lastFoodImage = null
        textViewMessage.text = "Plato vacío. Selecciona un alimento."
        setBackgroundColor(Color.WHITE) // Fondo blanco para resetear
    }

    private fun addFoodToPlate(foodImage: Int) {
        val imageView = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(120, 120).apply {
                leftMargin = Random.nextInt(0, circleContainer.width - 80)
                topMargin = Random.nextInt(0, circleContainer.height - 80)
            }
            setImageResource(foodImage)
        }
        circleContainer.addView(imageView)
    }

    private fun setBackgroundColor(color: Int) {
        mainLayout.setBackgroundColor(color)
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Diga el nombre del alimento")
        }

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: ActivityNotFoundException) {
            showAlertDialog("El reconocimiento de voz no está disponible en este dispositivo.")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val foodName = result?.get(0)?.lowercase(Locale.getDefault())

            if (foodName != null) {
                handleFoodRecognition(foodName)
            }
        }
    }

    private fun handleFoodRecognition(foodName: String) {
        val foodImage = foodNameToImage[foodName]

        if (foodImage != null) {
            // Si se encuentra el alimento, se agrega al plato
            onFoodSelected(foodImage)
            textViewMessage.text = "¡$foodName agregado al plato!"
        } else {
            // Si no se encuentra, muestra una alerta
            showAlertDialog("Alimento \"$foodName\" no encontrado.")
        }
    }

    private fun showAlertDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Información")
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}