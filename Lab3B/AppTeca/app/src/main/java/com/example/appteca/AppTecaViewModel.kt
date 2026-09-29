package com.example.appteca

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

// Desafío 2 — Sobrevivir también al proceso: el sistema entrega el SavedStateHandle
// al construir el ViewModel (by viewModels() ya sabe hacerlo) y lo que se guarda ahí
// se rescata tras la muerte del proceso, igual que el estado de las vistas con id.
class AppTecaViewModel(private val state: SavedStateHandle) : ViewModel() {

    // ── El estado interno (antes: variables de la Activity), ahora respaldado por el handle ──
    private var query: String
        get() = state[CLAVE_QUERY] ?: ""
        set(value) { state[CLAVE_QUERY] = value }

    private var soloFavoritas: Boolean
        get() = state[CLAVE_SOLO_FAVORITAS] ?: false
        set(value) { state[CLAVE_SOLO_FAVORITAS] = value }

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
        // La fila trae una copia (ver AppAdapter): se muta la app real del catálogo.
        val real = Catalogo.apps.first { it.id == app.id }
        real.esFavorita = !real.esFavorita
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
        lista = lista.sortedByDescending { it.esFavorita }   // favoritas primero (desafío 2 del 3A)
        _listaVisible.value = lista             // publica — no dibuja
        _modoSoloFavoritas.value = soloFavoritas
    }

    override fun onCleared() {
        Log.d("VIDA", "ViewModel → onCleared (destruido de verdad)")
    }

    private companion object {
        const val CLAVE_QUERY = "query"
        const val CLAVE_SOLO_FAVORITAS = "soloFavoritas"
    }
}
