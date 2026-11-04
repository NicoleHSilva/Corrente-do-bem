package com.example.correntedobem

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MapaFragment : Fragment(R.layout.fragment_mapa) {

    private lateinit var mapa: MapView
    private lateinit var txtBanner: TextView
    private lateinit var botaoPonto: ExtendedFloatingActionButton
    private lateinit var pontos: MutableList<Ponto>
    private var modoAdicionar = false
    private var provaCaminho: String? = null
    private var imagemProva: ImageView? = null

    private val escolherProva =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val caminho = Imagens.salvar(requireContext(), uri, 1280)
                if (caminho != null) {
                    provaCaminho = caminho
                    imagemProva?.let {
                        it.imageTintList = null
                        it.setPadding(0, 0, 0, 0)
                        Imagens.carregar(it, caminho, 600)
                    }
                }
            }
        }

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
        val mostrarLimpos = Armazenamento.mostrarLimpos(requireContext())
        pontos.filter { mostrarLimpos || !it.resolvido }.forEach { ponto ->
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
                val novo = Ponto(
                    System.currentTimeMillis(),
                    titulo,
                    edtDescricao.text.toString().trim(),
                    local.latitude,
                    local.longitude
                )
                pontos.add(novo)
                salvarERedesenhar()
                dialogo.dismiss()
            }
        }
        dialogo.show()
    }

    private fun mostrarDetalhes(ponto: Ponto) {
        val conteudo = layoutInflater.inflate(R.layout.dialog_detalhe, null)
        val data = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(ponto.criadoEm))
        val situacao = getString(if (ponto.resolvido) R.string.status_limpo else R.string.status_pendente)
        val descricao = if (ponto.descricao.isEmpty()) "" else ponto.descricao + "\n\n"
        conteudo.findViewById<TextView>(R.id.txtDetalhe).text =
            descricao + getString(R.string.detalhe_status, situacao, data)

        if (ponto.resolvido) {
            val txtProva = conteudo.findViewById<TextView>(R.id.txtDetalheProva)
            val imgProva = conteudo.findViewById<ImageView>(R.id.imgDetalheProva)
            txtProva.visibility = View.VISIBLE
            val foto = ponto.provaFoto
            if (foto != null && File(foto).exists()) {
                val comentario = ponto.provaTexto
                txtProva.text = if (comentario.isNullOrBlank()) {
                    getString(R.string.prova_label)
                } else {
                    getString(R.string.prova_com_texto, comentario)
                }
                imgProva.visibility = View.VISIBLE
                Imagens.carregar(imgProva, foto, 600)
            } else {
                txtProva.text = getString(R.string.prova_sem)
            }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(ponto.titulo)
            .setView(conteudo)
            .setPositiveButton(if (ponto.resolvido) R.string.reabrir else R.string.marcar_limpo) { _, _ ->
                if (ponto.resolvido) {
                    atualizarPonto(ponto.copy(resolvido = false, provaFoto = null, provaTexto = null))
                } else {
                    pedirProva(ponto)
                }
            }
            .setNeutralButton(R.string.remover) { _, _ ->
                pontos.removeAll { it.id == ponto.id }
                salvarERedesenhar()
            }
            .setNegativeButton(R.string.fechar, null)
            .show()
    }

    private fun pedirProva(ponto: Ponto) {
        val conteudo = layoutInflater.inflate(R.layout.dialog_prova, null)
        val imagem = conteudo.findViewById<ImageView>(R.id.imgProva)
        val edtComentario = conteudo.findViewById<TextInputEditText>(R.id.edtComentarioProva)
        provaCaminho = null
        imagemProva = imagem

        conteudo.findViewById<Button>(R.id.btnAnexarProva).setOnClickListener {
            escolherProva.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        val dialogo = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.prova_titulo)
            .setView(conteudo)
            .setPositiveButton(R.string.btn_confirmar, null)
            .setNegativeButton(R.string.btn_cancelar, null)
            .create()

        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val caminho = provaCaminho
                if (caminho == null) {
                    Toast.makeText(requireContext(), R.string.prova_obrigatoria, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                atualizarPonto(
                    ponto.copy(
                        resolvido = true,
                        provaFoto = caminho,
                        provaTexto = edtComentario.text.toString().trim()
                    )
                )
                dialogo.dismiss()
            }
        }
        dialogo.setOnDismissListener {
            imagemProva = null
            provaCaminho = null
        }
        dialogo.show()
    }

    private fun atualizarPonto(novo: Ponto) {
        val indice = pontos.indexOfFirst { it.id == novo.id }
        if (indice >= 0) pontos[indice] = novo
        salvarERedesenhar()
    }

    private fun salvarERedesenhar() {
        Armazenamento.salvarPontos(requireContext(), pontos)
        desenharPontos()
        atualizarBanner()
    }

    override fun onResume() {
        super.onResume()
        mapa.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapa.onPause()
    }

    override fun onDestroyView() {
        mapa.onDetach()
        super.onDestroyView()
    }
}