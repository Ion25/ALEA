package com.example.alea

import FoodAdapter
import FoodItem
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import kotlin.math.log

class CompatibilityActivity : AppCompatActivity() {

    private lateinit var adapter: RecipeAdapter
    private val recipes = listOf(
        Recipe(R.drawable.recipe_bananaoatmealbread, "Pan de plátano y avena", "200kcal", "5g", "2mg", "29g", "Avena, Yogurt, Plátano"),
        Recipe(R.drawable.recipe_bakedberryoatmeal, "Avena horneada con bayas", "280kcal", "9g", "2mg", "46g", "Avena, Azúcar, Sal, Canela, Bayas"),
        Recipe(R.drawable.recipe_beefandbroccoli, "Carne de res y brócoli", "210kcal", "13g", "18mg", "23g", "Carne molida, Jengibre, Ajo, Brócoli, Agua"),
        Recipe(R.drawable.recipe_easymeatballs, "Albóndigas fáciles de preparar", "200kcal", "14g", "3mg", "19g", "Carne molida, Arroz blanco, Leche, Huevo, Cebolla, Zanahoria"),
        Recipe(R.drawable.recipe_watermelonandfruitsalad, "Ensalada de frutas y sandía", "40kcal", "1g", "29mg", "10g", "Sandía, Fresas, Arándanos, Fruta, Jugo de limón, Miel"),
        Recipe(R.drawable.recipe_classicmacaroniandcheese, "Macarrones con queso clásico", "200kcal", "11g", "0mg", "29g", "Macarrones, Cebolla, Leche, Huevo, Pimienta, Queso Cheddar")
    )

    private lateinit var adapterFood: FoodAdapter
    private val foods = listOf(
        FoodItem(R.drawable.comida_carne, "Carne"),
        FoodItem(R.drawable.comida_pollo, "Pollo"),
        FoodItem(R.drawable.comida_pescado, "Pescado"),
        FoodItem(R.drawable.comida_canela, "Canela"),
        FoodItem(R.drawable.comida_azucar, "Azúcar"),
        FoodItem(R.drawable.comida_sal, "Sal"),
        FoodItem(R.drawable.comida_platano, "Plátano"),
        FoodItem(R.drawable.comida_fresa, "Fresas"),
        FoodItem(R.drawable.comida_arroz, "Arroz"),
        FoodItem(R.drawable.comida_huevo, "Huevo")
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        setContentView(R.layout.activity_compatibility)

        // Configuración de paddings para las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar encabezado animado
        val textViewHeader = findViewById<TextView>(R.id.textViewHeader)
        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        textViewHeader.startAnimation(bounceAnimation)

        // Configurar RecyclerView
        val recipeList = findViewById<RecyclerView>(R.id.recipeList)
        recipeList.layoutManager = LinearLayoutManager(this)
        adapter = RecipeAdapter(recipes)
        recipeList.adapter = adapter

        // Configuración de RecyclerView horizontal
        val foodListHorizontal = findViewById<RecyclerView>(R.id.foodListHorizontal)
        foodListHorizontal.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val selectedFoods = mutableListOf<FoodItem>() // Lista de alimentos seleccionados
        // Plato vacío
        val emptyPlate = findViewById<ImageView>(R.id.emptyPlate)
        val foodAdapter = FoodAdapter(foods) { selectedFood ->
            selectedFoods.add(selectedFood) // Agregar el alimento seleccionado a la lista
            updatePlateContent(selectedFoods, emptyPlate) // Actualizar el contenido del plato
        }
        foodListHorizontal.adapter = foodAdapter

        // Configurar animación 2
        val animation2 = findViewById<LottieAnimationView>(R.id.animation2)
        animation2.setOnClickListener {
            if (foodListHorizontal.visibility == View.VISIBLE && emptyPlate.visibility == View.VISIBLE) {
                // Oculta la lista y el plato vacío si ya están visibles
                foodListHorizontal.visibility = View.GONE
                emptyPlate.visibility = View.GONE
            } else {
                // Oculta las recetas (todas o favoritas)
                recipeList.visibility = View.GONE

                // Muestra la lista horizontal y el plato vacío
                foodListHorizontal.visibility = View.VISIBLE
                emptyPlate.visibility = View.VISIBLE
                Toast.makeText(this, "Selecciona tus alimentos", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para mostrar todas las recetas
        val animation1 = findViewById<LottieAnimationView>(R.id.animation1)
        animation1.setOnClickListener {
            if (recipeList.visibility == View.VISIBLE && adapter.getDisplayedRecipes() == recipes) {
                // Si ya se muestran todas las recetas, oculta el RecyclerView
                recipeList.visibility = View.GONE
            } else {
                // Oculta la lista horizontal y el plato vacío
                foodListHorizontal.visibility = View.GONE
                emptyPlate.visibility = View.GONE
                // Muestra todas las recetas
                adapter.updateRecipes(recipes)
                recipeList.visibility = View.VISIBLE
                Toast.makeText(this, "Mostrando todas las recetas", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para mostrar recetas favoritas
        val animation3 = findViewById<LottieAnimationView>(R.id.animation3)
        animation3.setOnClickListener {
            val favoriteRecipes = adapter.getFavoriteRecipes()
            if (recipeList.visibility == View.VISIBLE && adapter.getDisplayedRecipes() == favoriteRecipes) {
                // Si ya se muestran las recetas favoritas, oculta el RecyclerView
                recipeList.visibility = View.GONE
            } else {
                // Oculta la lista horizontal y el plato vacío
                foodListHorizontal.visibility = View.GONE
                emptyPlate.visibility = View.GONE

                if (favoriteRecipes.isNotEmpty()) {
                    // Muestra solo las recetas favoritas
                    adapter.updateRecipes(favoriteRecipes)
                    recipeList.visibility = View.VISIBLE
                    Toast.makeText(this, "Mostrando recetas favoritas", Toast.LENGTH_SHORT).show()
                } else {
                    // No hay recetas favoritas, oculta el RecyclerView
                    recipeList.visibility = View.GONE
                    Toast.makeText(this, "No hay recetas favoritas aún.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        animation1.setAnimation(R.raw.animation1)
        animation2.setAnimation(R.raw.animation2)
        animation3.setAnimation(R.raw.animation3)

    }

    private fun updatePlateContent(selectedFoods: List<FoodItem>, plateView: ImageView) {
        // Aquí puedes mostrar los alimentos seleccionados en el plato
        // Por ejemplo, cambia el contenido del plato o muestra un mensaje

        val foodNames = selectedFoods.joinToString(", ") { it.name }
        Toast.makeText(this, "Alimentos en el plato: $foodNames", Toast.LENGTH_SHORT).show()

        // Alternativamente, puedes implementar un mecanismo para visualizar las imágenes en el plato
    }

}
