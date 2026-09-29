package com.example.appteca

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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

    @Test
    fun buscar_filtraPorNombre() {
        val vm = AppTecaViewModel()
        vm.buscar("spo")
        assertEquals(listOf("Spotify"), vm.listaVisible.value!!.map { it.nombre })
    }

    @Test
    fun buscar_filtraPorCategoriaSinDistinguirMayusculas() {
        val vm = AppTecaViewModel()
        vm.buscar("FINANZAS")
        assertEquals(listOf("Mercado Pago"), vm.listaVisible.value!!.map { it.nombre })
    }

    @Test
    fun modoSoloFavoritas_combinaConBusqueda() {
        val vm = AppTecaViewModel()
        vm.alternarFavorita(Catalogo.apps.first { it.nombre == "WhatsApp" })
        vm.alternarFavorita(Catalogo.apps.first { it.nombre == "Discord" })
        vm.buscar("p")
        vm.alternarModo()
        assertTrue(vm.modoSoloFavoritas.value!!)
        assertEquals(listOf("WhatsApp"), vm.listaVisible.value!!.map { it.nombre })
    }

    @Test
    fun desmarcarEnModoFavoritas_laAppDesaparece() {
        val vm = AppTecaViewModel()
        val spotify = Catalogo.apps.first { it.nombre == "Spotify" }
        vm.alternarFavorita(spotify)
        vm.alternarModo()
        assertEquals(1, vm.listaVisible.value!!.size)
        vm.alternarFavorita(spotify)
        assertTrue(vm.listaVisible.value!!.isEmpty())
    }
}
