package com.example.appteca

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AppTecaViewModel : ViewModel() {

    // ── El estado interno (antes: variables de la Activity) ──
    private var query = ""
    private var soloFavoritas = false

    // ── El estado publicado (antes: no existía — cada listener redibujaba) ──
    private val _listaVisible = MutableLiveData<List<App>>()
    val listaVisible: LiveData<List<App>> = _listaVisible

    private val _modoSoloFavoritas = MutableLiveData(false)
    val modoSoloFavoritas: LiveData<Boolean> = _modoSoloFavoritas

    init {
        Log.d("VIDA", "ViewModel → creado (${hashCode()})")
        aplicarFiltros()
    }

    // ── Los eventos que la pantalla puede avisar ──
    fun buscar(texto: String) {
        query = texto.trim()
        aplicarFiltros()
    }

    fun alternarModo() {
        soloFavoritas = !soloFavoritas
        aplicarFiltros()
    }

    fun alternarFavorita(app: App) {
        app.esFavorita = !app.esFavorita
        aplicarFiltros()
    }

    // Parche pragmático (5.3): no redibuja "por las dudas", pide a la fuente que re-derive.
    fun refrescar() = aplicarFiltros()

    // ── LA función, mudada casi textual — con una diferencia decisiva ──
    private fun aplicarFiltros() {
        var lista: List<App> = Catalogo.apps
        if (query.isNotEmpty()) lista = lista.filter {
            it.nombre.contains(query, true) || it.categoria.contains(query, true)
        }
        if (soloFavoritas) lista = lista.filter { it.esFavorita }
        _listaVisible.value = lista             // publica — no dibuja
        _modoSoloFavoritas.value = soloFavoritas
    }

    override fun onCleared() {
        Log.d("VIDA", "ViewModel → onCleared (destruido de verdad)")
    }
}
