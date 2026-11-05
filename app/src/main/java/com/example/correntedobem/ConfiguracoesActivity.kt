package com.example.correntedobem

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ConfiguracoesActivity : AppCompatActivity() {

    private lateinit var txtConta: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        txtConta = findViewById(R.id.txtConta)
        atualizarConta()

        findViewById<Button>(R.id.btnRedefinirEmail).setOnClickListener { dialogoEmail() }
        findViewById<Button>(R.id.btnAlterarSenha).setOnClickListener { dialogoAlterarSenha() }
        findViewById<Button>(R.id.btnTrocarConta).setOnClickListener { dialogoTrocarConta() }
        findViewById<Button>(R.id.btnSairConta).setOnClickListener { confirmarSaida() }

        val grupoTema = findViewById<RadioGroup>(R.id.grupoTema)
        when (Armazenamento.modoTema(this)) {
            AppCompatDelegate.MODE_NIGHT_NO -> grupoTema.check(R.id.temaClaro)
            AppCompatDelegate.MODE_NIGHT_YES -> grupoTema.check(R.id.temaEscuro)
            else -> grupoTema.check(R.id.temaSistema)
        }
        grupoTema.setOnCheckedChangeListener { _, id ->
            val modo = when (id) {
                R.id.temaClaro -> AppCompatDelegate.MODE_NIGHT_NO
                R.id.temaEscuro -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            Armazenamento.salvarModoTema(this, modo)
            AppCompatDelegate.setDefaultNightMode(modo)
        }

        val switchLimpos = findViewById<MaterialSwitch>(R.id.switchLimpos)
        switchLimpos.isChecked = Armazenamento.mostrarLimpos(this)
        switchLimpos.setOnCheckedChangeListener { _, marcado ->
            Armazenamento.salvarMostrarLimpos(this, marcado)
        }

        findViewById<Button>(R.id.btnRestaurar).setOnClickListener {
            confirmar(R.string.confirmar_restaurar) { Armazenamento.restaurarExemplos(this) }
        }
        findViewById<Button>(R.id.btnApagar).setOnClickListener {
            confirmar(R.string.confirmar_apagar) { Armazenamento.apagarPublicacoesEPontos(this) }
        }

        val versao = try {
            packageManager.getPackageInfo(packageName, 0).versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
        findViewById<TextView>(R.id.txtVersao).text = getString(R.string.sobre_versao, versao)

        findViewById<Button>(R.id.btnVoltar).setOnClickListener { finish() }
    }

    private fun atualizarConta() {
        val conta = Armazenamento.contaAtual(this)
        txtConta.text = if (conta != null) getString(R.string.conta_conectada, conta.nome, conta.email) else ""
    }

    private fun dialogoEmail() {
        val conteudo = layoutInflater.inflate(R.layout.dialog_email, null)
        val layoutEmail = conteudo.findViewById<TextInputLayout>(R.id.layoutEmail)
        val layoutSenha = conteudo.findViewById<TextInputLayout>(R.id.layoutSenhaEmail)
        val edtEmail = conteudo.findViewById<TextInputEditText>(R.id.edtEmail)
        val edtSenha = conteudo.findViewById<TextInputEditText>(R.id.edtSenhaEmail)

        val dialogo = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.redefinir_email_titulo)
            .setView(conteudo)
            .setPositiveButton(R.string.salvar, null)
            .setNegativeButton(R.string.btn_cancelar, null)
            .create()

        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                layoutEmail.error = null
                layoutSenha.error = null
                val novo = edtEmail.text.toString().trim()
                if (!Patterns.EMAIL_ADDRESS.matcher(novo).matches()) {
                    layoutEmail.error = getString(R.string.erro_email_invalido)
                    return@setOnClickListener
                }
                val erro = Armazenamento.redefinirEmail(this, novo, edtSenha.text.toString())
                if (erro == null) {
                    atualizarConta()
                    Toast.makeText(this, R.string.email_atualizado, Toast.LENGTH_SHORT).show()
                    dialogo.dismiss()
                } else if (erro == R.string.senha_incorreta) {
                    layoutSenha.error = getString(erro)
                } else {
                    layoutEmail.error = getString(erro)
                }
            }
        }
        dialogo.show()
    }

    private fun dialogoAlterarSenha() {
        val conteudo = layoutInflater.inflate(R.layout.dialog_nova_senha, null)
        val layoutAtual = conteudo.findViewById<TextInputLayout>(R.id.layoutAtual)
        val layoutNova = conteudo.findViewById<TextInputLayout>(R.id.layoutNova)
        val layoutConfirma = conteudo.findViewById<TextInputLayout>(R.id.layoutConfirma)
        val edtAtual = conteudo.findViewById<TextInputEditText>(R.id.edtAtual)
        val edtNova = conteudo.findViewById<TextInputEditText>(R.id.edtNova)
        val edtConfirma = conteudo.findViewById<TextInputEditText>(R.id.edtConfirma)

        val dialogo = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.senha_alterar)
            .setView(conteudo)
            .setPositiveButton(R.string.salvar, null)
            .setNegativeButton(R.string.btn_cancelar, null)
            .create()

        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                layoutAtual.error = null
                layoutNova.error = null
                layoutConfirma.error = null
                val nova = edtNova.text.toString()
                if (nova.length < 4) {
                    layoutNova.error = getString(R.string.senha_curta)
                    return@setOnClickListener
                }
                if (nova != edtConfirma.text.toString()) {
                    layoutConfirma.error = getString(R.string.senha_diferente)
                    return@setOnClickListener
                }
                if (Armazenamento.alterarSenha(this, edtAtual.text.toString(), nova)) {
                    Toast.makeText(this, R.string.senha_alterada, Toast.LENGTH_SHORT).show()
                    dialogo.dismiss()
                } else {
                    layoutAtual.error = getString(R.string.senha_incorreta)
                }
            }
        }
        dialogo.show()
    }

    private fun dialogoTrocarConta() {
        val atual = Armazenamento.contaAtualId(this)
        val outras = Armazenamento.contas(this).filter { it.id != atual }
        val opcoes = outras.map { "${it.nome} (${it.email})" } + getString(R.string.adicionar_conta)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.trocar_conta_titulo)
            .setItems(opcoes.toTypedArray()) { _, indice ->
                if (indice < outras.size) {
                    pedirSenhaDaConta(outras[indice])
                } else {
                    Armazenamento.sair(this)
                    abrirLogin(true)
                }
            }
            .setNegativeButton(R.string.btn_cancelar, null)
            .show()
    }

    private fun pedirSenhaDaConta(conta: Conta) {
        val conteudo = layoutInflater.inflate(R.layout.dialog_senha, null)
        val layoutSenha = conteudo.findViewById<TextInputLayout>(R.id.layoutSenha)
        val edtSenha = conteudo.findViewById<TextInputEditText>(R.id.edtSenha)

        val dialogo = MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.senha_da_conta, conta.nome))
            .setView(conteudo)
            .setPositiveButton(R.string.entrar, null)
            .setNegativeButton(R.string.btn_cancelar, null)
            .create()

        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (Armazenamento.trocarConta(this, conta.id, edtSenha.text.toString())) {
                    dialogo.dismiss()
                    abrirPrincipal()
                } else {
                    layoutSenha.error = getString(R.string.senha_incorreta)
                }
            }
        }
        dialogo.show()
    }

    private fun confirmarSaida() {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.sair_confirmar)
            .setPositiveButton(R.string.btn_sair_conta) { _, _ ->
                Armazenamento.sair(this)
                abrirLogin(false)
            }
            .setNegativeButton(R.string.btn_cancelar, null)
            .show()
    }

    private fun abrirLogin(criar: Boolean) {
        startActivity(
            Intent(this, LoginActivity::class.java)
                .putExtra("criar", criar)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    private fun abrirPrincipal() {
        startActivity(
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    private fun confirmar(mensagem: Int, acao: () -> Unit) {
        MaterialAlertDialogBuilder(this)
            .setMessage(mensagem)
            .setPositiveButton(R.string.btn_confirmar) { _, _ ->
                acao()
                Toast.makeText(this, R.string.dados_ok, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.btn_cancelar, null)
            .show()
    }
}