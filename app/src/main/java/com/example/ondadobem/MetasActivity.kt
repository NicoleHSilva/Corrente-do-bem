package com.example.ondadobem

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.LinearProgressIndicator

class MetasActivity : AppCompatActivity() {

    private lateinit var metas: List<CheckBox>
    private lateinit var txtProgresso: TextView
    private lateinit var txtMensagem: TextView
    private lateinit var barra: LinearProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metas)

        txtProgresso = findViewById(R.id.txtProgresso)
        txtMensagem = findViewById(R.id.txtMensagem)
        barra = findViewById(R.id.barraProgresso)

        metas = listOf(
            findViewById(R.id.meta1),
            findViewById(R.id.meta2),
            findViewById(R.id.meta3),
            findViewById(R.id.meta4),
            findViewById(R.id.meta5)
        )
        barra.max = metas.size

        metas.forEach { it.setOnCheckedChangeListener { _, _ -> atualizarProgresso() } }

        findViewById<Button>(R.id.btnReiniciar).setOnClickListener {
            metas.forEach { it.isChecked = false }
        }
        findViewById<Button>(R.id.btnVoltar).setOnClickListener { finish() }

        atualizarProgresso()
    }

    private fun atualizarProgresso() {
        val cumpridas = metas.count { it.isChecked }
        txtProgresso.text = getString(R.string.progresso_metas, cumpridas, metas.size)
        barra.setProgressCompat(cumpridas, true)
        txtMensagem.text = getString(
            if (cumpridas == metas.size) R.string.msg_parabens else R.string.msg_continue
        )
    }
}
