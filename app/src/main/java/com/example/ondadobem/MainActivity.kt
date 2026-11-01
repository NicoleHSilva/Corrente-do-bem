package com.example.ondadobem

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val menu = findViewById<BottomNavigationView>(R.id.bottomNav)
        menu.setOnItemSelectedListener { item ->
            val tela: Fragment = when (item.itemId) {
                R.id.nav_mapa -> MapaFragment()
                R.id.nav_feed -> FeedFragment()
                R.id.nav_perfil -> PerfilFragment()
                else -> HomeFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, tela)
                .commit()
            true
        }

        if (savedInstanceState == null) {
            menu.selectedItemId = R.id.nav_inicio
        }
    }
}
