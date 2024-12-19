package com.example.alea

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val categories: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private val categoryImageMap = mapOf(
        "Carnes" to R.drawable.carne,
        "Verduras" to R.drawable.pepinillo,
        "Frutas" to R.drawable.manzana,
        "Lácteos" to R.drawable.leche
    )

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        private val categoryImage: ImageView = itemView.findViewById(R.id.categoryImage)

        fun bind(category: String) {
            categoryName.text = category
            val imageRes = categoryImageMap[category] ?: R.drawable.baseline_food_bank_24 // Imagen predeterminada
            categoryImage.setImageResource(imageRes)
            itemView.setOnClickListener { onClick(category) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size
}
