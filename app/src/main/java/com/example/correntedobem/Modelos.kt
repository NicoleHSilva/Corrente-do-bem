package com.example.correntedobem

import org.json.JSONObject

data class Ponto(
    val id: Long,
    val titulo: String,
    val descricao: String,
    val lat: Double,
    val lon: Double,
    val resolvido: Boolean = false,
    val criadoEm: Long = System.currentTimeMillis()
) {
    fun paraJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("titulo", titulo)
        .put("descricao", descricao)
        .put("lat", lat)
        .put("lon", lon)
        .put("resolvido", resolvido)
        .put("criadoEm", criadoEm)

    companion object {
        fun deJson(o: JSONObject) = Ponto(
            o.getLong("id"),
            o.getString("titulo"),
            o.getString("descricao"),
            o.getDouble("lat"),
            o.getDouble("lon"),
            o.getBoolean("resolvido"),
            o.getLong("criadoEm")
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
    val curtiu: Boolean = false
) {
    fun paraJson(): JSONObject {
        val o = JSONObject()
            .put("id", id)
            .put("autor", autor)
            .put("texto", texto)
            .put("criadoEm", criadoEm)
            .put("curtidas", curtidas)
            .put("curtiu", curtiu)
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
            o.getBoolean("curtiu")
        )
    }
}

data class Perfil(
    val nome: String = "",
    val bio: String = "",
    val foto: String? = null
)
