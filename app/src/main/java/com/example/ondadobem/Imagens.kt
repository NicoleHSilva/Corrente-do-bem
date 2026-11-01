package com.example.ondadobem

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.widget.ImageView
import java.io.File
import java.util.UUID
import kotlin.math.max
import kotlin.math.min

object Imagens {

    fun salvar(ctx: Context, uri: Uri, maxLado: Int): String? {
        return try {
            val resolver = ctx.contentResolver
            val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, limites) }
            val amostra = calcularAmostra(limites.outWidth, limites.outHeight, maxLado)
            val original = resolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = amostra })
            } ?: return null
            val graus = resolver.openInputStream(uri)?.use { rotacao(ExifInterface(it)) } ?: 0
            val escala = min(1f, maxLado.toFloat() / max(original.width, original.height))
            val matriz = Matrix().apply {
                postRotate(graus.toFloat())
                postScale(escala, escala)
            }
            val pronta = Bitmap.createBitmap(original, 0, 0, original.width, original.height, matriz, true)
            val pasta = File(ctx.filesDir, "fotos").apply { mkdirs() }
            val arquivo = File(pasta, "${UUID.randomUUID()}.jpg")
            arquivo.outputStream().use { pronta.compress(Bitmap.CompressFormat.JPEG, 85, it) }
            arquivo.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun carregar(view: ImageView, caminho: String, alvo: Int) {
        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(caminho, limites)
        val opcoes = BitmapFactory.Options().apply {
            inSampleSize = calcularAmostra(limites.outWidth, limites.outHeight, alvo)
        }
        val bitmap = BitmapFactory.decodeFile(caminho, opcoes)
        if (bitmap != null) view.setImageBitmap(bitmap)
    }

    private fun calcularAmostra(largura: Int, altura: Int, alvo: Int): Int {
        var amostra = 1
        while (max(largura, altura) / (amostra * 2) >= alvo) amostra *= 2
        return amostra
    }

    private fun rotacao(exif: ExifInterface): Int =
        when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
}
