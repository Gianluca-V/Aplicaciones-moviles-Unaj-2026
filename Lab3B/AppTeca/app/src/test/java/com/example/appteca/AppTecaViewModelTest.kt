package com.example.appteca

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AppTecaViewModelTest {

    // LiveData publishes on the main thread; this rule runs it synchronously in plain JVM tests.
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Catalogo is a process-wide singleton shared between tests: reset favorites before each one.
    @Before
    fun resetCatalogo() {
        Catalogo.apps.forEach { it.esFavorita = false }
    }

    private fun nuevoVm(state: SavedStateHandle = SavedStateHandle()) = AppTecaViewModel(state)

    private fun AppTecaViewModel.nombres() = listaVisible.value!!.map { it.nombre }

    // Rows hand out copies, so tests toggle favorites with a copy too, like the screen does.
    private fun AppTecaViewModel.tocarEstrella(nombre: String) =
        alternarFavorita(Catalogo.apps.first { it.nombre == nombre }.copy())

    @Test
    fun buscar_filtraPorNombre() {
        val vm = nuevoVm()
        vm.buscar("red")
        assertEquals(listOf("Reddit"), vm.nombres())
    }

    @Test
    fun buscar_filtraPorCategoriaSinDistinguirMayusculas() {
        val vm = nuevoVm()
        vm.buscar("FINANZAS")
        assertEquals(listOf("Mercado Pago"), vm.nombres())
    }

    @Test
    fun modoSoloFavoritas_combinaConBusqueda() {
        val vm = nuevoVm()
        vm.tocarEstrella("WhatsApp")
        vm.tocarEstrella("Discord")
        vm.buscar("d")
        vm.alternarModo()
        assertTrue(vm.modoSoloFavoritas.value!!)
        assertEquals(listOf("Discord"), vm.nombres())
    }

    @Test
    fun desmarcarEnModoFavoritas_laAppDesaparece() {
        val vm = nuevoVm()
        vm.tocarEstrella("Reddit")
        vm.alternarModo()
        assertEquals(listOf("Reddit"), vm.nombres())
        vm.tocarEstrella("Reddit")
        assertTrue(vm.nombres().isEmpty())
    }

    @Test
    fun favoritasPrimero_respetandoElOrdenDelCatalogo() {
        val vm = nuevoVm()
        vm.tocarEstrella("GitHub")
        vm.tocarEstrella("YouTube")
        assertEquals(listOf("YouTube", "GitHub", "WhatsApp"), vm.nombres().take(3))
    }

    @Test
    fun savedStateHandle_rescataModoYBusquedaEnUnViewModelNuevo() {
        val state = SavedStateHandle()
        val antes = nuevoVm(state)
        antes.buscar("o")
        antes.alternarModo()

        // Simula la muerte del proceso: un ViewModel nuevo recibe el mismo estado rescatado.
        val despues = nuevoVm(SavedStateHandle(state.keys().associateWith { state.get<Any>(it) }))
        assertTrue(despues.modoSoloFavoritas.value!!)
        assertEquals("o", state.get<String>("query"))
    }
}
