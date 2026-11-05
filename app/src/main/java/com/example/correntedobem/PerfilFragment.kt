package com.example.correntedobem

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    private lateinit var avatar: ShapeableImageView
    private lateinit var txtNome: TextView
    private lateinit var txtBio: TextView
    private lateinit var txtQtdPosts: TextView
    private lateinit var txtQtdCurtidas: TextView
    private lateinit var lista: RecyclerView
    private lateinit var txtSemPosts: TextView
    private var fotoTemporaria: String? = null
    private var imagemEdicao: ShapeableImageView? = null

    private val escolherFoto =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val caminho = Imagens.salvar(requireContext(), uri, 600)
                if (caminho != null) {
                    fotoTemporaria = caminho
                    imagemEdicao?.let { exibirAvatar(it, caminho, 24) }
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ctx = requireContext()

        avatar = view.findViewById(R.id.imgAvatar)
        txtNome = view.findViewById(R.id.txtNomePerfil)
        txtBio = view.findViewById(R.id.txtBioPerfil)
        txtQtdPosts = view.findViewById(R.id.txtQtdPosts)
        txtQtdCurtidas = view.findViewById(R.id.txtQtdCurtidas)
        lista = view.findViewById(R.id.listaMeusPosts)
        txtSemPosts = view.findViewById(R.id.txtSemPosts)

        lista.layoutManager = GridLayoutManager(ctx, 3)

        view.findViewById<ImageButton>(R.id.btnConfiguracoes).setOnClickListener {
            startActivity(Intent(ctx, ConfiguracoesActivity::class.java))
        }
        view.findViewById<Button>(R.id.btnEditarPerfil).setOnClickListener { editarPerfil() }

        atualizar()
    }

    private fun atualizar() {
        val ctx = requireContext()
        val conta = Armazenamento.contaAtual(ctx) ?: return

        txtNome.text = conta.nome
        txtBio.text = conta.bio.ifBlank { getString(R.string.perfil_bio_padrao) }
        exibirAvatar(avatar, conta.foto, 22)

        val meusPosts = Armazenamento.posts(ctx).filter { it.autorId == conta.id }
        txtQtdPosts.text = meusPosts.size.toString()
        txtQtdCurtidas.text = meusPosts.sumOf { it.curtidas }.toString()
        lista.adapter = GradeAdapter(meusPosts)
        txtSemPosts.visibility = if (meusPosts.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun exibirAvatar(view: ShapeableImageView, caminho: String?, paddingDp: Int) {
        if (caminho != null && File(caminho).exists()) {
            view.imageTintList = null
            view.setPadding(0, 0, 0, 0)
            Imagens.carregar(view, caminho, 400)
        } else {
            val margem = (paddingDp * resources.displayMetrics.density).toInt()
            view.setPadding(margem, margem, margem, margem)
            view.setImageResource(R.drawable.ic_pessoa)
            view.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.icone))
        }
    }

    private fun editarPerfil() {
        val ctx = requireContext()
        val conta = Armazenamento.contaAtual(ctx) ?: return
        val conteudo = layoutInflater.inflate(R.layout.dialog_editar_perfil, null)
        val imagem = conteudo.findViewById<ShapeableImageView>(R.id.imgAvatarEdicao)
        val edtNome = conteudo.findViewById<TextInputEditText>(R.id.edtNomeEdicao)
        val edtBio = conteudo.findViewById<TextInputEditText>(R.id.edtBioEdicao)

        fotoTemporaria = conta.foto
        imagemEdicao = imagem
        exibirAvatar(imagem, fotoTemporaria, 24)
        edtNome.setText(conta.nome)
        edtBio.setText(conta.bio)

        conteudo.findViewById<Button>(R.id.btnTrocarFoto).setOnClickListener {
            escolherFoto.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        val dialogo = MaterialAlertDialogBuilder(ctx)
            .setTitle(R.string.editar_perfil_titulo)
            .setView(conteudo)
            .setPositiveButton(R.string.salvar, null)
            .setNegativeButton(R.string.btn_cancelar, null)
            .create()

        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val nome = edtNome.text.toString().trim()
                if (nome.isEmpty()) {
                    edtNome.error = getString(R.string.erro_nome)
                    return@setOnClickListener
                }
                Armazenamento.atualizarPerfil(ctx, nome, edtBio.text.toString().trim(), fotoTemporaria)
                atualizar()
                dialogo.dismiss()
            }
        }
        dialogo.setOnDismissListener { imagemEdicao = null }
        dialogo.show()
    }
}