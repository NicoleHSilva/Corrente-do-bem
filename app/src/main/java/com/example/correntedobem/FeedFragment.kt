package com.example.correntedobem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText

class FeedFragment : Fragment(R.layout.fragment_feed) {

    private lateinit var posts: MutableList<Post>
    private lateinit var adapter: PostAdapter
    private lateinit var lista: RecyclerView
    private lateinit var edtPost: TextInputEditText
    private lateinit var imgPrevia: ImageView
    private lateinit var txtVazio: TextView
    private var fotoPendente: String? = null

    private val escolherFoto =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val caminho = Imagens.salvar(requireContext(), uri, 1280)
                if (caminho != null) {
                    fotoPendente = caminho
                    imgPrevia.visibility = View.VISIBLE
                    Imagens.carregar(imgPrevia, caminho, 600)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ctx = requireContext()

        lista = view.findViewById(R.id.listaPosts)
        edtPost = view.findViewById(R.id.edtPost)
        imgPrevia = view.findViewById(R.id.imgPrevia)
        txtVazio = view.findViewById(R.id.txtVazio)

        posts = Armazenamento.posts(ctx)
        adapter = PostAdapter(posts, ::curtir, ::confirmarExclusao)
        lista.layoutManager = LinearLayoutManager(ctx)
        lista.adapter = adapter
        atualizarVazio()

        view.findViewById<Button>(R.id.btnAtalhoDicas).setOnClickListener {
            startActivity(Intent(ctx, DicasActivity::class.java))
        }
        view.findViewById<Button>(R.id.btnAtalhoMetas).setOnClickListener {
            startActivity(Intent(ctx, MetasActivity::class.java))
        }

        val avatar = view.findViewById<ShapeableImageView>(R.id.imgAvatarComposer)
        val fotoPerfil = Armazenamento.perfil(ctx).foto
        if (fotoPerfil != null) {
            avatar.imageTintList = null
            avatar.setPadding(0, 0, 0, 0)
            Imagens.carregar(avatar, fotoPerfil, 200)
        }

        view.findViewById<Button>(R.id.btnFotoPost).setOnClickListener {
            escolherFoto.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
        view.findViewById<Button>(R.id.btnPublicar).setOnClickListener { publicar() }
    }

    private fun publicar() {
        val ctx = requireContext()
        val texto = edtPost.text.toString().trim()
        if (texto.isEmpty() && fotoPendente == null) {
            Toast.makeText(ctx, R.string.post_vazio_erro, Toast.LENGTH_SHORT).show()
            return
        }
        val autor = Armazenamento.perfil(ctx).nome.ifBlank { getString(R.string.autor_padrao) }
        val agora = System.currentTimeMillis()
        posts.add(0, Post(agora, autor, texto, fotoPendente, agora))
        adapter.notifyItemInserted(0)
        (requireView() as NestedScrollView).smoothScrollTo(0, 0)
        Armazenamento.salvarPosts(ctx, posts)

        edtPost.text?.clear()
        fotoPendente = null
        imgPrevia.setImageDrawable(null)
        imgPrevia.visibility = View.GONE
        atualizarVazio()
    }

    private fun curtir(posicao: Int) {
        val post = posts[posicao]
        posts[posicao] = if (post.curtiu) {
            post.copy(curtiu = false, curtidas = post.curtidas - 1)
        } else {
            post.copy(curtiu = true, curtidas = post.curtidas + 1)
        }
        adapter.notifyItemChanged(posicao)
        Armazenamento.salvarPosts(requireContext(), posts)
    }

    private fun confirmarExclusao(posicao: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.excluir_post_titulo)
            .setPositiveButton(R.string.excluir) { _, _ ->
                posts.removeAt(posicao)
                adapter.notifyItemRemoved(posicao)
                Armazenamento.salvarPosts(requireContext(), posts)
                atualizarVazio()
            }
            .setNegativeButton(R.string.btn_cancelar, null)
            .show()
    }

    private fun atualizarVazio() {
        txtVazio.visibility = if (posts.isEmpty()) View.VISIBLE else View.GONE
    }
}