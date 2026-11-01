package com.example.ondadobem

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import org.json.JSONArray

object Armazenamento {

    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences("onda_do_bem", Context.MODE_PRIVATE)

    fun pontos(ctx: Context): MutableList<Ponto> {
        val arr = JSONArray(prefs(ctx).getString("pontos", "[]") ?: "[]")
        val lista = MutableList(arr.length()) { Ponto.deJson(arr.getJSONObject(it)) }
        if (!prefs(ctx).getBoolean("pontos_exemplo", false)) {
            prefs(ctx).edit().putBoolean("pontos_exemplo", true).apply()
            if (lista.isEmpty()) {
                lista.addAll(pontosExemplo())
                salvarPontos(ctx, lista)
            }
        }
        return lista
    }

    fun salvarPontos(ctx: Context, lista: List<Ponto>) {
        val arr = JSONArray()
        lista.forEach { arr.put(it.paraJson()) }
        prefs(ctx).edit().putString("pontos", arr.toString()).apply()
    }

    fun posts(ctx: Context): MutableList<Post> {
        val arr = JSONArray(prefs(ctx).getString("posts", "[]") ?: "[]")
        val lista = MutableList(arr.length()) { Post.deJson(arr.getJSONObject(it)) }
        if (!prefs(ctx).getBoolean("posts_exemplo", false)) {
            prefs(ctx).edit().putBoolean("posts_exemplo", true).apply()
            if (lista.isEmpty()) {
                lista.addAll(postsExemplo(ctx))
                salvarPosts(ctx, lista)
            }
        }
        return lista
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

    private fun postsExemplo(ctx: Context): List<Post> {
        val agora = System.currentTimeMillis()
        val hora = 3_600_000L
        return listOf(
            Post(1, ctx.getString(R.string.autor_ex1), ctx.getString(R.string.post_ex1), "res:praia", agora - 2 * hora, 12),
            Post(2, ctx.getString(R.string.autor_ex2), ctx.getString(R.string.post_ex2), "res:mutirao", agora - 26 * hora, 8),
            Post(3, ctx.getString(R.string.autor_ex3), ctx.getString(R.string.post_ex3), "res:reciclagem", agora - 50 * hora, 5)
        )
    }

    private fun pontosExemplo(): List<Ponto> = listOf(
        Ponto(1, "Praia de Copacabana (RJ)", "Exemplo: lixo deixado na areia após o fim de semana.", -22.9711, -43.1822),
        Ponto(2, "Orla de Santos (SP)", "Exemplo: mutirão de limpeza da praia.", -23.9680, -46.3340, true),
        Ponto(3, "Porto da Barra (BA)", "Exemplo: garrafas e plásticos perto da água.", -13.0100, -38.5330),
        Ponto(4, "Praia de Boa Viagem (PE)", "Exemplo: sacos de lixo para retirar.", -8.1330, -34.9000),
        Ponto(5, "Praia de Iracema (CE)", "Exemplo: ponto de coleta de resíduos.", -3.7230, -38.5100, true),
        Ponto(6, "Ponta Negra (AM)", "Exemplo: lixo na margem do Rio Negro.", -3.0900, -60.0900),
        Ponto(7, "Ver-o-Peso (PA)", "Exemplo: resíduos acumulados perto do rio.", -1.4530, -48.5040),
        Ponto(8, "Orla do Guaíba (RS)", "Exemplo: mutirão realizado na orla.", -30.0500, -51.2400, true),
        Ponto(9, "Lagoa da Conceição (SC)", "Exemplo: plásticos na margem da lagoa.", -27.6050, -48.4600),
        Ponto(10, "Lago Paranoá (DF)", "Exemplo: lixo deixado no entorno do lago.", -15.8100, -47.8600)
    )
}