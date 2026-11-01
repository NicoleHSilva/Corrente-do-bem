package com.example.ondadobem

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import org.json.JSONArray

object Armazenamento {

    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences("onda_do_bem", Context.MODE_PRIVATE)

    fun pontos(ctx: Context): MutableList<Ponto> {
        val arr = JSONArray(prefs(ctx).getString("pontos", "[]") ?: "[]")
        return MutableList(arr.length()) { Ponto.deJson(arr.getJSONObject(it)) }
    }

    fun salvarPontos(ctx: Context, lista: List<Ponto>) {
        val arr = JSONArray()
        lista.forEach { arr.put(it.paraJson()) }
        prefs(ctx).edit().putString("pontos", arr.toString()).apply()
    }

    fun posts(ctx: Context): MutableList<Post> {
        val arr = JSONArray(prefs(ctx).getString("posts", "[]") ?: "[]")
        return MutableList(arr.length()) { Post.deJson(arr.getJSONObject(it)) }
    }

    fun salvarPosts(ctx: Context, lista: List<Post>) {
        val arr = JSONArray()
        lista.forEach { arr.put(it.paraJson()) }
        prefs(ctx).edit().putString("posts", arr.toString()).apply()
    }

    fun perfil(ctx: Context): Perfil {
        val p = prefs(ctx)
        return Perfil(
            p.getString("perfil_nome", "") ?: "",
            p.getString("perfil_bio", "") ?: "",
            p.getString("perfil_foto", null)
        )
    }

    fun salvarPerfil(ctx: Context, perfil: Perfil) {
        prefs(ctx).edit()
            .putString("perfil_nome", perfil.nome)
            .putString("perfil_bio", perfil.bio)
            .putString("perfil_foto", perfil.foto)
            .apply()
    }

    fun modoTema(ctx: Context): Int =
        prefs(ctx).getInt("modo_tema", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

    fun salvarModoTema(ctx: Context, modo: Int) {
        prefs(ctx).edit().putInt("modo_tema", modo).apply()
    }
}
