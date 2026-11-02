package com.example.correntedobem

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MapaFragment : Fragment(R.layout.fragment_mapa) {

    private lateinit var mapa: MapView
    private lateinit var txtBanner: TextView
    private lateinit var botaoPonto: ExtendedFloatingActionButton
    private lateinit var pontos: MutableList<Ponto>
    private var modoAdicionar = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapa = view.findViewById(R.id.mapa)
        txtBanner = view.findViewById(R.id.txtBanner)
        botaoPonto = view.findViewById(R.id.fabPonto)
        pontos = Armazenamento.pontos(requireContext())

        mapa.setTileSource(TileSourceFactory.MAPNIK)
        mapa.setMultiTouchControls(true)
        mapa.setMinZoomLevel(3.0)
        mapa.setMaxZoomLevel(19.0)
        mapa.controller.setZoom(4.0)
        mapa.controller.setCenter(GeoPoint(-14.2350, -51.9253))

        val receptor = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                if (!modoAdicionar) return false
                alternarModo(false)
                pedirDados(p)
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean = false
        }
        mapa.overlays.add(MapEventsOverlay(receptor))

        botaoPonto.setOnClickListener { alternarModo(!modoAdicionar) }
        desenharPontos()
        atualizarBanner()
    }

    private fun alternarModo(ativo: Boolean) {
        modoAdicionar = ativo
        botaoPonto.text = getString(if (ativo) R.string.btn_cancelar else R.string.btn_add_ponto)
        atualizarBanner()
    }

    private fun atualizarBanner() {
        txtBanner.text = if (modoAdicionar) {
            getString(R.string.banner_adicionar)
        } else {
            getString(
                R.string.banner_pontos,
                pontos.count { !it.resolvido },
                pontos.count { it.resolvido }
            )
        }
    }

    private fun desenharPontos() {
        mapa.overlays.removeAll { it is Marker }
        pontos.forEach { ponto ->
            val marcador = Marker(mapa)
            marcador.position = GeoPoint(ponto.lat, ponto.lon)
            marcador.title = ponto.titulo
            marcador.icon = ContextCompat.getDrawable(
                requireContext(),
                if (ponto.resolvido) R.drawable.ic_pino_ok else R.drawable.ic_pino_alerta
            )
            marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marcador.setOnMarkerClickListener { _, _ ->
                mostrarDetalhes(ponto)
                true
            }
            mapa.overlays.add(marcador)
        }
        mapa.invalidate()
    }

    private fun pedirDados(local: GeoPoint) {
        val conteudo = layoutInflater.inflate(R.layout.dialog_ponto, null)
        val edtTitulo = conteudo.findViewById<TextInputEditText>(R.id.edtTituloPonto)
        val edtDescricao = conteudo.findViewById<TextInputEditText>(R.id.edtDescPonto)

        val dialogo = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.novo_ponto_titulo)
            .setView(conteudo)
            .setPositiveButton(R.string.salvar, null)
            .setNegativeButton(R.string.btn_cancelar, null)
            .create()

        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val titulo = edtTitulo.text.toString().trim()
                if (titulo.isEmpty()) {
                    edtTitulo.error = getString(R.string.erro_titulo)
                    return@setOnClickListener
                }
                val descricao = edtDescricao.text.toString().trim()
                val novoPonto = Ponto(
                    id = System.currentTimeMillis(),
                    titulo = titulo,
                    descricao = descricao,
                    lat = local.latitude,
                    lon = local.longitude
                )
                pontos.add(0, novoPonto)
                Armazenamento.salvarPontos(requireContext(), pontos)
                desenharPontos()
                atualizarBanner()
                dialogo.dismiss()
            }
        }
        dialogo.show()
    }

    private fun mostrarDetalhes(ponto: Ponto) {
        val dataFormatada = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(ponto.criadoEm))
        val statusText = getString(if (ponto.resolvido) R.string.status_limpo else R.string.status_pendente)
        val detalheStatus = getString(R.string.detalhe_status, statusText, dataFormatada)
        val mensagem = if (ponto.descricao.isNotBlank()) {
            "${ponto.descricao}\n\n$detalheStatus"
        } else {
            detalheStatus
        }

        val btnStatusText = if (ponto.resolvido) R.string.reabrir else R.string.marcar_limpo

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(ponto.titulo)
            .setMessage(mensagem)
            .setPositiveButton(btnStatusText) { _, _ ->
                val index = pontos.indexOfFirst { it.id == ponto.id }
                if (index != -1) {
                    pontos[index] = ponto.copy(resolvido = !ponto.resolvido)
                    Armazenamento.salvarPontos(requireContext(), pontos)
                    desenharPontos()
                    atualizarBanner()
                }
            }
            .setNeutralButton(R.string.remover) { _, _ ->
                pontos.removeAll { it.id == ponto.id }
                Armazenamento.salvarPontos(requireContext(), pontos)
                desenharPontos()
                atualizarBanner()
            }
            .setNegativeButton(R.string.fechar, null)
            .show()
    }
}
