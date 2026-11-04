package com.example.correntedobem

import android.os.Bundle
import android.view.View
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

    private lateinit var txtSenhaStatus: TextView
    private lateinit var btnSenha: Button
    private lateinit var btnRemoverSenha: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        txtSenhaStatus = findViewById(R.id.txtSenhaStatus)
        btnSenha = findViewById(R.id.btnSenha)
        btnRemoverSenha = findViewById(R.id.btnRemoverSenha)

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

        btnSenha.setOnClickListener { dialogoNovaSenha() }
        btnRemoverSenha.setOnClickListener { dialogoRemoverSenha() }
        atualizarSenha()

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

    private fun atualizarSenha() {
        val tem = Armazenamento.temSenha(this)
        txtSenhaStatus.text = getString(if (tem) R.string.senha_ativa else R.string.senha_inativa)
        btnSenha.text = getString(if (tem) R.string.senha_alterar else R.string.senha_definir)
        btnRemoverSenha.visibility = if (tem) View.VISIBLE else View.GONE
    }

    private fun dialogoNovaSenha() {
        val tem = Armazenamento.temSenha(this)
        val conteudo = layoutInflater.inflate(R.layout.dialog_nova_senha, null)
        val layoutAtual = conteudo.findViewById<TextInputLayout>(R.id.layoutAtual)
        val layoutNova = conteudo.findViewById<TextInputLayout>(R.id.layoutNova)
        val layoutConfirma = conteudo.findViewById<TextInputLayout>(R.id.layoutConfirma)
        val edtAtual = conteudo.findViewById<TextInputEditText>(R.id.edtAtual)
        val edtNova = conteudo.findViewById<TextInputEditText>(R.id.edtNova)
        val edtConfirma = conteudo.findViewById<TextInputEditText>(R.id.edtConfirma)
        layoutAtual.visibility = if (tem) View.VISIBLE else View.GONE

        val dialogo = MaterialAlertDialogBuilder(this)
            .setTitle(if (tem) R.string.senha_alterar else R.string.senha_definir)
            .setView(conteudo)
            .setPositiveButton(R.string.salvar, null)
            .setNegativeButton(R.string.btn_cancelar, null)
            .create()

        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                layoutAtual.error = null
                layoutNova.error = null
                layoutConfirma.error = null

                if (tem && !Armazenamento.conferirSenha(this, edtAtual.text.toString())) {
                    layoutAtual.error = getString(R.string.senha_incorreta)
                    return@setOnClickListener
                }
                val nova = edtNova.text.toString()
                if (nova.length < 4) {
                    layoutNova.error = getString(R.string.senha_curta)
                    return@setOnClickListener
                }
                if (nova != edtConfirma.text.toString()) {
                    layoutConfirma.error = getString(R.string.senha_diferente)
                    return@setOnClickListener
                }
                Armazenamento.definirSenha(this, nova)
                Sessao.desbloqueado = true
                atualizarSenha()
                Toast.makeText(this, R.string.senha_salva, Toast.LENGTH_SHORT).show()
                dialogo.dismiss()
            }
        }
        dialogo.show()
    }

    private fun dialogoRemoverSenha() {
        val conteudo = layoutInflater.inflate(R.layout.dialog_senha, null)
        val layoutSenha = conteudo.findViewById<TextInputLayout>(R.id.layoutSenha)
        val edtSenha = conteudo.findViewById<TextInputEditText>(R.id.edtSenha)

        val dialogo = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.remover_senha_titulo)
            .setView(conteudo)
            .setPositiveButton(R.string.senha_remover, null)
            .setNegativeButton(R.string.btn_cancelar, null)
            .create()

        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (Armazenamento.conferirSenha(this, edtSenha.text.toString())) {
                    Armazenamento.removerSenha(this)
                    atualizarSenha()
                    Toast.makeText(this, R.string.senha_removida, Toast.LENGTH_SHORT).show()
                    dialogo.dismiss()
                } else {
                    layoutSenha.error = getString(R.string.senha_incorreta)
                }
            }
        }
        dialogo.show()
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