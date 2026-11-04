package com.example.correntedobem

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val menu = findViewById<BottomNavigationView>(R.id.bottomNav)
        menu.setOnItemSelectedListener { item ->
            val tela: Fragment = when (item.itemId) {
                R.id.nav_mapa -> MapaFragment()
                R.id.nav_perfil -> PerfilFragment()
                else -> FeedFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, tela)
                .commit()
            true
        }

        if (savedInstanceState == null) {
            menu.selectedItemId = R.id.nav_feed
        }

        if (Armazenamento.temSenha(this) && !Sessao.desbloqueado) {
            val raiz = findViewById<View>(android.R.id.content)
            raiz.visibility = View.INVISIBLE
            pedirSenha(raiz)
        }
    }

    private fun pedirSenha(raiz: View) {
        val conteudo = layoutInflater.inflate(R.layout.dialog_senha, null)
        val layoutSenha = conteudo.findViewById<TextInputLayout>(R.id.layoutSenha)
        val edtSenha = conteudo.findViewById<TextInputEditText>(R.id.edtSenha)

        val dialogo = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.senha_entrar_titulo)
            .setView(conteudo)
            .setCancelable(false)
            .setPositiveButton(R.string.entrar, null)
            .setNegativeButton(R.string.sair) { _, _ -> finish() }
            .create()

        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (Armazenamento.conferirSenha(this, edtSenha.text.toString())) {
                    Sessao.desbloqueado = true
                    raiz.visibility = View.VISIBLE
                    dialogo.dismiss()
                } else {
                    layoutSenha.error = getString(R.string.senha_incorreta)
                }
            }
        }
        dialogo.show()
    }
}