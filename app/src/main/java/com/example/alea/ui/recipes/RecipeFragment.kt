package com.example.alea.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alea.Adapters.RandomRecipeAdapter
import com.example.alea.Listeners.RandomRecipeResponseListener
import com.example.alea.Models.RandomRecipeApiResponse
import com.example.alea.R
import com.example.alea.RequestManager

class RecipeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var randomRecipeAdapter: RandomRecipeAdapter
    private lateinit var manager: RequestManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe, container, false) // Asegúrate de tener este layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_random) // Asegúrate de que el ID exista en tu layout
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inicializar el gestor de recetas
        manager = RequestManager(requireContext())

        // Llamar a la API
        fetchRandomRecipes()
    }

    private fun fetchRandomRecipes() {
        manager.getRandomRecipes(object : RandomRecipeResponseListener {
            override fun didFetch(response: RandomRecipeApiResponse, message: String?) {
                randomRecipeAdapter = RandomRecipeAdapter(requireContext(), response.recipes)
                recyclerView.adapter = randomRecipeAdapter
            }

            override fun didError(message: String?) {
                Toast.makeText(requireContext(), message ?: "Error desconocido", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
