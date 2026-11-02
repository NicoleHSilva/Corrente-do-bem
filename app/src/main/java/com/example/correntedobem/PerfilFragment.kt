package com.example.correntedobem

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText

class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    private var fotoAtual: String? = null
    private lateinit var avatar: ShapeableImageView
    private lateinit var txtNome: TextView
    private lateinit var txtBio: TextView

    private val escolherFoto =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val caminho = Imagens.salvar(requireContext(), uri, 600)
                if (caminho != null) {
                    fotoAtual = caminho
                    mostrarAvatar()
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ctx = requireContext()

        avatar = view.findViewById(R.id.imgAvatar)
        txtNome = view.findViewById(R.id.txtNomePerfil)
        txtBio = view.findViewById(R.id.txtBioPerfil)
        val edtNome = view.findViewById<TextInputEditText>(R.id.edtNome)
        val edtBio = view.findViewById<TextInputEditText>(R.id.edtBio)

        val perfil = Armazenamento.perfil(ctx)
        fotoAtual = perfil.foto
        edtNome.setText(perfil.nome)
        edtBio.setText(perfil.bio)
        atualizarCabecalho(perfil.nome, perfil.bio)
        mostrarAvatar()

        view.findViewById<Button>(R.id.btnEscolherFoto).setOnClickListener {
            escolherFoto.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        view.findViewById<Button>(R.id.btnSalvarPerfil).setOnClickListener {
            val nome = edtNome.text.toString().trim()
            val bio = edtBio.text.toString().trim()
            Armazenamento.salvarPerfil(ctx, Perfil(nome, bio, fotoAtual))
            atualizarCabecalho(nome, bio)
            Toast.makeText(ctx, R.string.perfil_salvo, Toast.LENGTH_SHORT).show()
        }

        view.findViewById<TextView>(R.id.txtQtdPontos).text =
            Armazenamento.pontos(ctx).count { it.id > 1000 }.toString()
        view.findViewById<TextView>(R.id.txtQtdPosts).text =
            Armazenamento.posts(ctx).count { it.id > 1000 }.toString()

        val switchTema = view.findViewById<MaterialSwitch>(R.id.switchTema)
        val modoAtual = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        switchTema.isChecked = modoAtual == Configuration.UI_MODE_NIGHT_YES
        switchTema.setOnCheckedChangeListener { _, marcado ->
            val modo = if (marcado) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            Armazenamento.salvarModoTema(ctx, modo)
            AppCompatDelegate.setDefaultNightMode(modo)
        }
    }

    private fun atualizarCabecalho(nome: String, bio: String) {
        txtNome.text = nome.ifBlank { getString(R.string.perfil_nome_padrao) }
        txtBio.text = bio.ifBlank { getString(R.string.perfil_bio_padrao) }
    }

    private fun mostrarAvatar() {
        val foto = fotoAtual
        if (foto != null) {
            avatar.imageTintList = null
            avatar.setPadding(0, 0, 0, 0)
            Imagens.carregar(avatar, foto, 400)
        } else {
            val margem = (28 * resources.displayMetrics.density).toInt()
            avatar.setPadding(margem, margem, margem, margem)
            avatar.setImageResource(R.drawable.ic_pessoa)
            avatar.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.icone))
        }
    }
}