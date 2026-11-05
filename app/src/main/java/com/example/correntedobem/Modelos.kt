package com.example.correntedobem

import org.json.JSONObject

data class Ponto(
    val id: Long,
    val titulo: String,
    val descricao: String,
    val lat: Double,
    val lon: Double,
    val resolvido: Boolean = false,
    val criadoEm: Long = System.currentTimeMillis(),
    val provaFoto: String? = null,
    val provaTexto: String? = null
) {
    fun paraJson(): JSONObject {
        val o = JSONObject()
            .put("id", id)
            .put("titulo", titulo)
            .put("descricao", descricao)
            .put("lat", lat)
            .put("lon", lon)
            .put("resolvido", resolvido)
            .put("criadoEm", criadoEm)
        if (provaFoto != null) o.put("provaFoto", provaFoto)
        if (provaTexto != null) o.put("provaTexto", provaTexto)
        return o
    }

    companion object {
        fun deJson(o: JSONObject) = Ponto(
            o.getLong("id"),
            o.getString("titulo"),
            o.getString("descricao"),
            o.getDouble("lat"),
            o.getDouble("lon"),
            o.getBoolean("resolvido"),
            o.getLong("criadoEm"),
            if (o.has("provaFoto")) o.getString("provaFoto") else null,
            if (o.has("provaTexto")) o.getString("provaTexto") else null
        )
    }
}

data class Post(
    val id: Long,
    val autor: String,
    val texto: String,
    val foto: String?,
    val criadoEm: Long,
    val curtidas: Int = 0,
    val curtiu: Boolean = false,
    val autorId: Long = 0
) {
    fun paraJson(): JSONObject {
        val o = JSONObject()
            .put("id", id)
            .put("autor", autor)
            .put("texto", texto)
            .put("criadoEm", criadoEm)
            .put("curtidas", curtidas)
            .put("curtiu", curtiu)
            .put("autorId", autorId)
        if (foto != null) o.put("foto", foto)
        return o
    }

    companion object {
        fun deJson(o: JSONObject) = Post(
            o.getLong("id"),
            o.getString("autor"),
            o.getString("texto"),
            if (o.has("foto")) o.getString("foto") else null,
            o.getLong("criadoEm"),
            o.getInt("curtidas"),
            o.getBoolean("curtiu"),
            if (o.has("autorId")) o.getLong("autorId") else 0L
        )
    }
}

data class Perfil(
    val nome: String = "",
    val bio: String = "",
    val foto: String? = null
)

data class Conta(
    val id: Long,
    val nome: String,
    val email: String,
    val bio: String,
    val foto: String?,
    val sal: String,
    val hash: String
) {
    fun paraJson(): JSONObject {
        val o = JSONObject()
            .put("id", id)
            .put("nome", nome)
            .put("email", email)
            .put("bio", bio)
            .put("sal", sal)
            .put("hash", hash)
        if (foto != null) o.put("foto", foto)
        return o
    }

    companion object {
        fun deJson(o: JSONObject) = Conta(
            o.getLong("id"),
            o.getString("nome"),
            o.getString("email"),
            o.getString("bio"),
            if (o.has("foto")) o.getString("foto") else null,
            o.getString("sal"),
            o.getString("hash")
        )
    }
}