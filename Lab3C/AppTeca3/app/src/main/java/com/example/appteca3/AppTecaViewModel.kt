package com.example.appteca3

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Desafío 5 — Alinear las defensas: la búsqueda y el modo viven en el SavedStateHandle,
// así el texto del campo y el filtro que se aplica se rescatan JUNTOS tras la muerte del proceso.
class AppTecaViewModel(private val state: SavedStateHandle) : ViewModel() {

    // ── El texto del buscador: el campo es controlado DESDE el ViewModel (sin rememberSaveable) ──
    val textoBusqueda: StateFlow<String> = state.getStateFlow(CLAVE_QUERY, "")

    private var soloFavoritas: Boolean
        get() = state[CLAVE_SOLO_FAVORITAS] ?: false
        set(value) { state[CLAVE_SOLO_FAVORITAS] = value }

    // ── ANTES (3B): MutableLiveData<List<App>>() / LiveData<List<App>>
    // ── AHORA:
    private val _listaVisible = MutableStateFlow<List<App>>(emptyList())
    val listaVisible: StateFlow<List<App>> = _listaVisible

    private val _modoSoloFavoritas = MutableStateFlow(false)
    val modoSoloFavoritas: StateFlow<Boolean> = _modoSoloFavoritas

    // Desafío 3 — con Navigation Compose la pantalla actual la decide el NavHost;
    // el detalle busca su app por id en el catálogo completo (no en la lista filtrada).
    private val _catalogo = MutableStateFlow<List<App>>(emptyList())
    val catalogo: StateFlow<List<App>> = _catalogo

    init {
        Log.d("VIDA", "ViewModel → creado (${hashCode()})")
        aplicarFiltros()
    }

    fun buscar(texto: String) {
        state[CLAVE_QUERY] = texto
        aplicarFiltros()
    }

    fun alternarModo() {
        soloFavoritas = !soloFavoritas
        aplicarFiltros()
    }

    fun alternarFavorita(app: App) {
        val nuevas = Catalogo.apps.map {
            if (it.id == app.id) it.copy(esFavorita = !it.esFavorita) else it
        }
        Catalogo.apps.clear()
        Catalogo.apps.addAll(nuevas)
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        val query = textoBusqueda.value.trim()
        var lista: List<App> = Catalogo.apps.toList()
        if (query.isNotEmpty()) lista = lista.filter {
            it.nombre.contains(query, true) || it.categoria.contains(query, true)
        }
        if (soloFavoritas) lista = lista.filter { it.esFavorita }
        lista = lista.sortedByDescending { it.esFavorita }   // favoritas primero (desafío 2 del 3A)
        _listaVisible.value = lista
        _modoSoloFavoritas.value = soloFavoritas
        _catalogo.value = Catalogo.apps.toList()
    }

    override fun onCleared() {
        Log.d("VIDA", "ViewModel → onCleared")
    }

    private companion object {
        const val CLAVE_QUERY = "query"
        const val CLAVE_SOLO_FAVORITAS = "soloFavoritas"
    }
}
