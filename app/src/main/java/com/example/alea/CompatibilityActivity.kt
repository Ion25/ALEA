package com.example.alea

import FoodAdapter
import FoodItem
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import kotlin.math.log

class CompatibilityActivity : BaseActivity.BaseActivity() {

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

    private val incompatibilityReasons = mapOf(
        Pair("Pollo", "Canela") to "La combinación de pollo y canela puede ralentizar la digestión, lo que afecta el metabolismo. Esto puede llevar a una sensación de pesadez y dificultar la quema eficiente de calorías.",
        Pair("Carne", "Plátano") to "La carne es un alimento difícil de digerir, y al combinarla con plátano, se aumenta la fermentación en el sistema digestivo, lo cual puede causar hinchazón y aumentar la retención de líquidos, afectando tu peso ideal.",
        Pair("Pescado", "Leche") to "El pescado combinado con leche puede dificultar la absorción de nutrientes, lo que ralentiza el metabolismo y puede generar un impacto en la eficiencia de tu proceso digestivo, afectando negativamente el control del peso.",
        Pair("Arroz", "Azúcar") to "El arroz con azúcar puede causar picos de insulina, lo que favorece el almacenamiento de grasa, particularmente en la zona abdominal. Esta combinación no es ideal si buscas mantener un peso saludable.",
        Pair("Fresas", "Leche") to "La combinación de fresas y leche puede afectar la digestión, lo que podría causar incomodidad estomacal y reducir la capacidad del cuerpo para procesar los alimentos eficientemente, afectando el metabolismo y el control del peso.",
        Pair("Sal", "Pescado") to "El exceso de sal combinado con pescado puede llevar a la retención de líquidos, lo que incrementa la hinchazón y puede hacerte sentir más pesado, lo cual no es adecuado cuando estás tratando de mantener un peso ideal.",
        Pair("Huevo", "Canela") to "La combinación de huevo con canela puede causar problemas digestivos, lo que dificulta la correcta absorción de los nutrientes y podría afectar la eficiencia en el metabolismo, interferiendo con tus objetivos de control de peso.",
        Pair("Pollo", "Azúcar") to "El pollo con azúcar puede interrumpir la digestión eficiente de proteínas y promover un aumento en los niveles de glucosa en sangre, lo que puede promover el almacenamiento de grasa y dificultar la pérdida de peso."
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
        val selectedFoods = mutableSetOf<FoodItem>() // Lista de alimentos seleccionados
        // Plato vacío
        val emptyPlate = findViewById<ImageView>(R.id.emptyPlate)
        val foodAdapter = FoodAdapter(foods) { selectedFood ->
            if (selectedFoods.contains(selectedFood)) {
                Toast.makeText(this, "Este alimento ya está en el plato.", Toast.LENGTH_SHORT).show()
            } else {
                selectedFoods.add(selectedFood) // Agregar el alimento seleccionado
                updatePlateContent(selectedFoods.toList(), emptyPlate) // Actualizar el plato
                checkForIncompatibilities(selectedFoods.toList()) // Verificar incompatibilidades después de agregar el alimento
            }
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
                clearPlate(emptyPlate, selectedFoods) // Limpia el plato antes de cambiar la vista
                foodListHorizontal.visibility = View.GONE
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
                clearPlate(emptyPlate, selectedFoods) // Limpia el plato y oculta el contenido
                foodListHorizontal.visibility = View.GONE

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
        val plateContainer = findViewById<ConstraintLayout>(R.id.main)

        // Eliminar imágenes previas asociadas con el plato
        plateContainer.children.filter { it.tag == "foodItem" }.forEach {
            plateContainer.removeView(it)
        }

        // Obtener las dimensiones del plato
        val plateCenterX = plateView.x + plateView.width / 2
        val plateCenterY = plateView.y + plateView.height / 2

        // Agregar nuevas imágenes seleccionadas
        selectedFoods.forEachIndexed { index, food ->
            val foodImage = ImageView(this).apply {
                setImageResource(food.imageRes)
                tag = "foodItem" // Etiqueta para identificar imágenes dinámicas

                // Tamaño de la imagen
                layoutParams = ConstraintLayout.LayoutParams(100, 100)
            }

            // Calcular desplazamientos para superposición
            val offsetX = (index % 3) * 40 - 60 // Ajusta horizontalmente (-60 centra las imágenes)
            val offsetY = (index / 3) * 40 - 40 // Ajusta verticalmente (-40 para empezar en el centro)

            // Posicionar la imagen relativa al centro del plato
            foodImage.x = plateCenterX + offsetX
            foodImage.y = plateCenterY + offsetY

            // Añadir la imagen al contenedor principal
            plateContainer.addView(foodImage)
        }

        // Filtrar recetas en función de los alimentos seleccionados
        val filteredRecipes = filterRecipesByIngredients(selectedFoods)
        updateRecipeList(filteredRecipes) // Actualizar lista de recetas debajo del plato
    }

    private fun animateFoodToPlate(foodView: ImageView, plateView: ImageView) {
        val plateCenterX = plateView.x + plateView.width / 2
        val plateCenterY = plateView.y + plateView.height / 2
        ObjectAnimator.ofFloat(foodView, "translationX", plateCenterX - foodView.x).apply {
            duration = 500
            start()
        }
        ObjectAnimator.ofFloat(foodView, "translationY", plateCenterY - foodView.y).apply {
            duration = 500
            start()
        }
    }

    private fun filterRecipesByIngredients(selectedFoods: List<FoodItem>): List<Recipe> {
        val selectedFoodNames = selectedFoods.map { it.name.toLowerCase() }
        return recipes.filter { recipe ->
            recipe.ingredients.split(", ").any { ingredient ->
                selectedFoodNames.any { selectedFood ->
                    ingredient.toLowerCase().contains(selectedFood)
                }
            }
        }
    }

    private fun updateRecipeList(filteredRecipes: List<Recipe>) {
        val recipeList = findViewById<RecyclerView>(R.id.recipeList)

        if (filteredRecipes.isNotEmpty()) {
            adapter.updateRecipes(filteredRecipes)
            recipeList.visibility = View.VISIBLE
        } else {
            adapter.updateRecipes(emptyList()) // Actualizar a una lista vacía si no hay coincidencias
            recipeList.visibility = View.GONE
            Toast.makeText(this, "No hay recetas que coincidan con los alimentos seleccionados", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearPlate(plateView: ImageView, selectedFoods: MutableSet<FoodItem>) {
        val plateContainer = findViewById<ConstraintLayout>(R.id.main)

        // Eliminar todas las vistas dinámicas asociadas con el plato
        val itemsToRemove = plateContainer.children.filter { it.tag == "foodItem" }.toList()
        itemsToRemove.forEach { plateContainer.removeView(it) }

        // Limpiar la lista de alimentos seleccionados
        selectedFoods.clear()

        // Ocultar el plato vacío
        plateView.visibility = View.GONE
    }

    private fun checkForIncompatibilities(selectedFoods: List<FoodItem>) {
        val alert = findViewById<FrameLayout>(R.id.incompatibilityAlert)
        val animation = findViewById<LottieAnimationView>(R.id.incompatibilityAnimation)
        val textView = findViewById<TextView>(R.id.incompatibilityText)
        val reasonsTextView = findViewById<TextView>(R.id.incompatibilityReasons)
        val buttonUnderstood = findViewById<Button>(R.id.buttonUnderstood)

        if (selectedFoods.size < 2) {
            // Ocultar alerta si hay menos de dos alimentos seleccionados
            alert.visibility = View.GONE
            return
        }

        val incompatibleFoods = mutableListOf<Pair<String, String>>()
        val detectedReasons = mutableListOf<String>()

        // Detectar incompatibilidades y razones
        for (i in selectedFoods.indices) {
            for (j in i + 1 until selectedFoods.size) {
                val food1 = selectedFoods[i].name
                val food2 = selectedFoods[j].name
                val reason = incompatibilityReasons[Pair(food1, food2)] ?: incompatibilityReasons[Pair(food2, food1)]
                if (reason != null) {
                    incompatibleFoods.add(Pair(food1, food2))
                    detectedReasons.add(reason)
                }
            }
        }

        if (incompatibleFoods.isNotEmpty()) {
            // Mostrar la alerta
            alert.visibility = View.VISIBLE
            animation.playAnimation()
            textView.visibility = View.VISIBLE

            // Mostrar razones de incompatibilidad
            reasonsTextView.text = detectedReasons.joinToString(separator = "\n") { it }

            // Configurar botón "Entendido"
            buttonUnderstood.setOnClickListener {
                alert.visibility = View.GONE // Ocultar alerta al presionar el botón
            }
        } else {
            // Ocultar alerta si no hay incompatibilidades
            alert.visibility = View.GONE
        }
    }
}
