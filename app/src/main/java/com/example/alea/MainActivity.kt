package com.example.alea

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.alea.databinding.ActivityMainBinding
import android.content.Intent
import android.view.MenuItem

import android.widget.Button


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val button: Button = findViewById(R.id.button2)

        // Set an OnClickListener
        button.setOnClickListener {
            // Obtener el NavController desde el navHostFragment
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            navView.visibility = BottomNavigationView.GONE
            button.visibility = Button.GONE
            // Navegar al ScannerFragment
            navController.navigate(R.id.scannerFragment)

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Mostrar de nuevo la barra de navegación al volver al MainActivity
        val bottomNav: BottomNavigationView = findViewById(R.id.nav_view)
        bottomNav.visibility = BottomNavigationView.VISIBLE

        val button: Button = findViewById(R.id.button2)
        button.visibility = Button.VISIBLE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // Volver al fragmento anterior o realizar alguna acción personalizada
            findNavController(R.id.nav_host_fragment_activity_main).navigateUp() // Esto debería manejar la acción de retroceso

            val bottomNav: BottomNavigationView = findViewById(R.id.nav_view)
            bottomNav.visibility = BottomNavigationView.VISIBLE

            val button: Button = findViewById(R.id.button2)
            button.visibility = Button.VISIBLE
            return true

        }
        return super.onOptionsItemSelected(item)
    }
}