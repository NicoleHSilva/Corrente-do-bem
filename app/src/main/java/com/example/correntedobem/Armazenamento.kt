package com.example.correntedobem

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import org.json.JSONArray
import java.security.MessageDigest
import java.util.UUID

object Armazenamento {

    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences("corrente_do_bem", Context.MODE_PRIVATE)

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
        val conta = contaAtual(ctx) ?: return Perfil()
        return Perfil(conta.nome, conta.bio, conta.foto)
    }

    private fun lerContas(ctx: Context): MutableList<Conta> {
        val arr = JSONArray(prefs(ctx).getString("contas", "[]") ?: "[]")
        return MutableList(arr.length()) { Conta.deJson(arr.getJSONObject(it)) }
    }

    private fun salvarContas(ctx: Context, lista: List<Conta>) {
        val arr = JSONArray()
        lista.forEach { arr.put(it.paraJson()) }
        prefs(ctx).edit().putString("contas", arr.toString()).apply()
    }

    fun contas(ctx: Context): List<Conta> = lerContas(ctx)

    fun contaAtual(ctx: Context): Conta? {
        val id = prefs(ctx).getLong("conta_atual", -1L)
        return lerContas(ctx).firstOrNull { it.id == id }
    }

    fun contaAtualId(ctx: Context): Long = contaAtual(ctx)?.id ?: 0L

    fun criarConta(ctx: Context, nome: String, email: String, senha: String): Boolean {
        val lista = lerContas(ctx)
        val emailLimpo = email.trim().lowercase()
        if (lista.any { it.email == emailLimpo }) return false
        val sal = UUID.randomUUID().toString()
        val nova = Conta(System.currentTimeMillis(), nome.trim(), emailLimpo, "", null, sal, gerarHash(sal + senha))
        if (lista.isEmpty()) adotarPostsAntigos(ctx, nova.id)
        lista.add(nova)
        salvarContas(ctx, lista)
        prefs(ctx).edit().putLong("conta_atual", nova.id).apply()
        return true
    }

    fun entrar(ctx: Context, email: String, senha: String): Boolean {
        val conta = lerContas(ctx).firstOrNull { it.email == email.trim().lowercase() } ?: return false
        if (gerarHash(conta.sal + senha) != conta.hash) return false
        prefs(ctx).edit().putLong("conta_atual", conta.id).apply()
        return true
    }

    fun trocarConta(ctx: Context, id: Long, senha: String): Boolean {
        val conta = lerContas(ctx).firstOrNull { it.id == id } ?: return false
        if (gerarHash(conta.sal + senha) != conta.hash) return false
        prefs(ctx).edit().putLong("conta_atual", conta.id).apply()
        return true
    }

    fun sair(ctx: Context) {
        prefs(ctx).edit().remove("conta_atual").apply()
    }

    fun atualizarPerfil(ctx: Context, nome: String, bio: String, foto: String?) {
        val lista = lerContas(ctx)
        val indice = lista.indexOfFirst { it.id == contaAtualId(ctx) }
        if (indice < 0) return
        lista[indice] = lista[indice].copy(nome = nome, bio = bio, foto = foto)
        salvarContas(ctx, lista)
    }

    fun redefinirEmail(ctx: Context, novoEmail: String, senha: String): Int? {
        val lista = lerContas(ctx)
        val indice = lista.indexOfFirst { it.id == contaAtualId(ctx) }
        if (indice < 0) return R.string.login_invalido
        val conta = lista[indice]
        if (gerarHash(conta.sal + senha) != conta.hash) return R.string.senha_incorreta
        val emailLimpo = novoEmail.trim().lowercase()
        if (lista.any { it.email == emailLimpo && it.id != conta.id }) return R.string.erro_email_existe
        lista[indice] = conta.copy(email = emailLimpo)
        salvarContas(ctx, lista)
        return null
    }

    fun alterarSenha(ctx: Context, atual: String, nova: String): Boolean {
        val lista = lerContas(ctx)
        val indice = lista.indexOfFirst { it.id == contaAtualId(ctx) }
        if (indice < 0) return false
        val conta = lista[indice]
        if (gerarHash(conta.sal + atual) != conta.hash) return false
        val sal = UUID.randomUUID().toString()
        lista[indice] = conta.copy(sal = sal, hash = gerarHash(sal + nova))
        salvarContas(ctx, lista)
        return true
    }

    private fun adotarPostsAntigos(ctx: Context, id: Long) {
        val lista = posts(ctx)
        var mudou = false
        for (i in lista.indices) {
            if (lista[i].id > 1000 && lista[i].autorId == 0L) {
                lista[i] = lista[i].copy(autorId = id)
                mudou = true
            }
        }
        if (mudou) salvarPosts(ctx, lista)
    }

    fun modoTema(ctx: Context): Int =
        prefs(ctx).getInt("modo_tema", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

    fun salvarModoTema(ctx: Context, modo: Int) {
        prefs(ctx).edit().putInt("modo_tema", modo).apply()
    }

    fun mostrarLimpos(ctx: Context): Boolean = prefs(ctx).getBoolean("mostrar_limpos", true)

    fun salvarMostrarLimpos(ctx: Context, valor: Boolean) {
        prefs(ctx).edit().putBoolean("mostrar_limpos", valor).apply()
    }

    fun restaurarExemplos(ctx: Context) {
        salvarPontos(ctx, pontosExemplo())
        salvarPosts(ctx, postsExemplo(ctx))
        prefs(ctx).edit().putBoolean("pontos_exemplo", true).putBoolean("posts_exemplo", true).apply()
    }

    fun apagarPublicacoesEPontos(ctx: Context) {
        salvarPontos(ctx, emptyList())
        salvarPosts(ctx, emptyList())
        prefs(ctx).edit().putBoolean("pontos_exemplo", true).putBoolean("posts_exemplo", true).apply()
    }

    private fun gerarHash(texto: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(texto.toByteArray())
            .joinToString("") { "%02x".format(it) }

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