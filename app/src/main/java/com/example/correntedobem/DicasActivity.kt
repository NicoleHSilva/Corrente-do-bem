package com.example.correntedobem

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DicasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dicas)

        findViewById<Button>(R.id.btnVoltar).setOnClickListener { finish() }
    }
}
