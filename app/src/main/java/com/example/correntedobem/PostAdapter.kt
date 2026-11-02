package com.example.correntedobem

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAdapter(
    private val posts: List<Post>,
    private val aoCurtir: (Int) -> Unit,
    private val aoExcluir: (Int) -> Unit
) : RecyclerView.Adapter<PostAdapter.VH>() {

    private val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val autor: TextView = v.findViewById(R.id.txtAutor)
        val data: TextView = v.findViewById(R.id.txtData)
        val texto: TextView = v.findViewById(R.id.txtTexto)
        val foto: ImageView = v.findViewById(R.id.imgFoto)
        val curtir: TextView = v.findViewById(R.id.btnCurtir)
        val compartilhar: TextView = v.findViewById(R.id.btnCompartilhar)
        val excluir: ImageButton = v.findViewById(R.id.btnExcluir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false))

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(h: VH, position: Int) {
        val post = posts[position]
        h.autor.text = post.autor
        h.data.text = formato.format(Date(post.criadoEm))
        h.texto.text = post.texto
        h.texto.visibility = if (post.texto.isEmpty()) View.GONE else View.VISIBLE

        val caminho = post.foto
        val ilustracao = ilustracaoDe(caminho)
        if (ilustracao != null) {
            h.foto.visibility = View.VISIBLE
            h.foto.setImageResource(ilustracao)
        } else if (caminho != null && File(caminho).exists()) {
            h.foto.visibility = View.VISIBLE
            Imagens.carregar(h.foto, caminho, 720)
        } else {
            h.foto.visibility = View.GONE
        }

        h.curtir.text = post.curtidas.toString()
        h.curtir.setCompoundDrawablesWithIntrinsicBounds(
            if (post.curtiu) R.drawable.ic_curtir_cheio else R.drawable.ic_curtir_vazio, 0, 0, 0
        )
        h.curtir.setOnClickListener {
            val pos = h.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) aoCurtir(pos)
        }
        h.compartilhar.setOnClickListener {
            val ctx = h.itemView.context
            val envio = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, ctx.getString(R.string.texto_compartilhar, post.autor, post.texto))
            }
            ctx.startActivity(Intent.createChooser(envio, ctx.getString(R.string.compartilhar_titulo)))
        }
        h.excluir.setOnClickListener {
            val pos = h.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) aoExcluir(pos)
        }
    }

    private fun ilustracaoDe(nome: String?): Int? = when (nome) {
        "res:praia" -> R.drawable.ilustracao_praia
        "res:mutirao" -> R.drawable.ilustracao_mutirao
        "res:reciclagem" -> R.drawable.ilustracao_reciclagem
        else -> null
    }
}