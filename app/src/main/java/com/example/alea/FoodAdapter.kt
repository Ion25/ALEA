package com.example.alea

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter(
    private val foodImages: List<Int>,
    private val onFoodClick: (Int) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(120, 120) // Tamaño pequeño
            scaleType = ImageView.ScaleType.CENTER_CROP
            setPadding(8, 8, 8, 8)
        }
        return FoodViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val foodImage = foodImages[position]
        holder.imageView.setImageResource(foodImage)
        holder.imageView.setOnClickListener {
            onFoodClick(foodImage)
        }
    }

    override fun getItemCount(): Int = foodImages.size

    class FoodViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
}
