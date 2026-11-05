package com.example.correntedobem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class GradeAdapter(private val posts: List<Post>) : RecyclerView.Adapter<GradeAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val imagem: ImageView = v.findViewById(R.id.imgMiniatura)
        val texto: TextView = v.findViewById(R.id.txtMiniatura)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_post_grade, parent, false))

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(h: VH, position: Int) {
        val post = posts[position]
        val caminho = post.foto
        val ilustracao = ilustracaoDe(caminho)
        if (ilustracao != null) {
            h.imagem.setImageResource(ilustracao)
            h.texto.visibility = View.GONE
        } else if (caminho != null && File(caminho).exists()) {
            Imagens.carregar(h.imagem, caminho, 400)
            h.texto.visibility = View.GONE
        } else {
            h.imagem.setImageDrawable(null)
            h.texto.visibility = View.VISIBLE
            h.texto.text = post.texto
        }
    }

    private fun ilustracaoDe(nome: String?): Int? = when (nome) {
        "res:praia" -> R.drawable.ilustracao_praia
        "res:mutirao" -> R.drawable.ilustracao_mutirao
        "res:reciclagem" -> R.drawable.ilustracao_reciclagem
        else -> null
    }
}