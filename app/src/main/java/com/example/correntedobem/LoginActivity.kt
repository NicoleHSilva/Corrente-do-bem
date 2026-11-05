package com.example.correntedobem

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    private lateinit var secaoEntrar: View
    private lateinit var secaoCriar: View
    private lateinit var linkEntrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        secaoEntrar = findViewById(R.id.secaoEntrar)
        secaoCriar = findViewById(R.id.secaoCriar)
        linkEntrar = findViewById(R.id.linkEntrar)

        val semContas = Armazenamento.contas(this).isEmpty()
        linkEntrar.visibility = if (semContas) View.GONE else View.VISIBLE
        mostrarCriar(semContas || intent.getBooleanExtra("criar", false))

        val layoutEmailLogin = findViewById<TextInputLayout>(R.id.layoutEmailLogin)
        val layoutSenhaLogin = findViewById<TextInputLayout>(R.id.layoutSenhaLogin)
        val edtEmailLogin = findViewById<TextInputEditText>(R.id.edtEmailLogin)
        val edtSenhaLogin = findViewById<TextInputEditText>(R.id.edtSenhaLogin)

        findViewById<Button>(R.id.btnEntrar).setOnClickListener {
            layoutEmailLogin.error = null
            layoutSenhaLogin.error = null
            val email = edtEmailLogin.text.toString()
            val senha = edtSenhaLogin.text.toString()
            if (Armazenamento.entrar(this, email, senha)) {
                abrirApp()
            } else {
                layoutSenhaLogin.error = getString(R.string.login_invalido)
            }
        }

        val layoutNome = findViewById<TextInputLayout>(R.id.layoutNomeCriar)
        val layoutEmail = findViewById<TextInputLayout>(R.id.layoutEmailCriar)
        val layoutSenha = findViewById<TextInputLayout>(R.id.layoutSenhaCriar)
        val layoutConfirma = findViewById<TextInputLayout>(R.id.layoutConfirmaCriar)
        val edtNome = findViewById<TextInputEditText>(R.id.edtNomeCriar)
        val edtEmail = findViewById<TextInputEditText>(R.id.edtEmailCriar)
        val edtSenha = findViewById<TextInputEditText>(R.id.edtSenhaCriar)
        val edtConfirma = findViewById<TextInputEditText>(R.id.edtConfirmaCriar)

        findViewById<Button>(R.id.btnCriarConta).setOnClickListener {
            layoutNome.error = null
            layoutEmail.error = null
            layoutSenha.error = null
            layoutConfirma.error = null

            val nome = edtNome.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val senha = edtSenha.text.toString()
            val confirma = edtConfirma.text.toString()

            if (nome.isEmpty()) {
                layoutNome.error = getString(R.string.erro_nome)
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                layoutEmail.error = getString(R.string.erro_email_invalido)
                return@setOnClickListener
            }
            if (senha.length < 4) {
                layoutSenha.error = getString(R.string.senha_curta)
                return@setOnClickListener
            }
            if (senha != confirma) {
                layoutConfirma.error = getString(R.string.senha_diferente)
                return@setOnClickListener
            }
            if (Armazenamento.criarConta(this, nome, email, senha)) {
                abrirApp()
            } else {
                layoutEmail.error = getString(R.string.erro_email_existe)
            }
        }

        findViewById<Button>(R.id.linkCriar).setOnClickListener { mostrarCriar(true) }
        linkEntrar.setOnClickListener { mostrarCriar(false) }
    }

    private fun mostrarCriar(criar: Boolean) {
        secaoCriar.visibility = if (criar) View.VISIBLE else View.GONE
        secaoEntrar.visibility = if (criar) View.GONE else View.VISIBLE
    }

    private fun abrirApp() {
        startActivity(
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }
}