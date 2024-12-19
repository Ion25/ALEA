package com.example.alea


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.resources.Compatibility
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Recibir datos desde la intención
        val gender = intent.getStringExtra("gender") ?: "Desconocido"
        val height = intent.getStringExtra("height") ?: "N/A"
        val weight = intent.getStringExtra("weight") ?: "N/A"

        // Configurar los datos en los TextView e ImageView
        findViewById<TextView>(R.id.tvGender).text = "Género: $gender"
        findViewById<TextView>(R.id.tvHeight).text = "Altura: $height"
        findViewById<TextView>(R.id.tvWeight).text = "Peso: $weight"
        val avatarView = findViewById<ImageView>(R.id.ivAvatar)

        // Actualizar el avatar
        val avatarResource = when (gender) {
            "Masculino" -> when (weight) {
                "Ligero" -> R.drawable.avatar_hombre_ligero
                "Medio" -> R.drawable.avatar_hombre_medio
                "Pesado" -> R.drawable.avatar_hombre_pesado
                else -> R.drawable.avatar_hombre_medio
            }
            "Mujer" -> when (weight) {
                "Ligero" -> R.drawable.avatar_mujer_ligera
                "Medio" -> R.drawable.avatar_mujer_media
                "Pesado" -> R.drawable.avatar_mujer_pesada
                else -> R.drawable.avatar_mujer_media
            }
            else -> R.drawable.avatar_hombre_ligero
        }
        avatarView.setImageResource(avatarResource)

        // Configurar menú inferior
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Lógica para Home
                    true
                }
                R.id.nav_profile -> {
                    // Lógica para Perfil
                    true
                }
                R.id.nav_settings -> {
                    // Lógica para Configuración
                    val intent = Intent(this, CompatibilityActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

    }
}