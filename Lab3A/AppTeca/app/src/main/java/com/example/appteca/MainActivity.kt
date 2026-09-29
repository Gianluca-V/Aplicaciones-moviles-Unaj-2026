package com.example.appteca

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: AppAdapter

    // El estado de la pantalla que NO vive en una vista:
    private var soloFavoritas = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("VIDA", "Main → onCreate")
        setContentView(R.layout.activity_main)

        // Desafío 3 — El rescate manual: recuperar el modo que guardó onSaveInstanceState.
        soloFavoritas = savedInstanceState?.getBoolean(CLAVE_SOLO_FAVORITAS) ?: false

        adapter = AppAdapter(
            onAppClick = { app ->
                val intent = Intent(this, DetalleActivity::class.java)
                intent.putExtra("appId", app.id)
                startActivity(intent)
            },
            onFavoritoClick = { app ->
                // La fila trae una copia (ver AppAdapter): se muta la app real del catálogo.
                val real = Catalogo.apps.first { it.id == app.id }
                real.esFavorita = !real.esFavorita
                aplicarFiltros()          // cambió el estado → recalcular
            })

        val rv = findViewById<RecyclerView>(R.id.rvApps)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        findViewById<EditText>(R.id.etBuscar).addTextChangedListener {
            aplicarFiltros()              // cambió el estado → recalcular
        }

        findViewById<Button>(R.id.btnSoloFav).setOnClickListener {
            soloFavoritas = !soloFavoritas
            aplicarFiltros()              // cambió el estado → recalcular
        }

        aplicarFiltros()                  // estado inicial → primera foto
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(CLAVE_SOLO_FAVORITAS, soloFavoritas)
    }

    override fun onResume() {
        super.onResume()
        Log.d("VIDA", "Main → onResume")
        aplicarFiltros()                  // "por si algo cambió mientras no miraba"
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("VIDA", "Main → onDestroy")
    }

    // LA función: de todo el estado actual, deriva la lista visible y el botón.
    private fun aplicarFiltros() {
        val q = findViewById<EditText>(R.id.etBuscar).text.toString().trim()
        var lista: List<App> = Catalogo.apps
        if (q.isNotEmpty()) lista = lista.filter {
            it.nombre.contains(q, true) || it.categoria.contains(q, true)
        }
        if (soloFavoritas) lista = lista.filter { it.esFavorita }
        // Desafío 2 — Favoritas primero: otro estado derivado más; el orden es estable,
        // así que dentro de cada grupo se respeta el orden del catálogo.
        lista = lista.sortedByDescending { it.esFavorita }
        adapter.actualizarLista(lista)
        findViewById<Button>(R.id.btnSoloFav).text =
            if (soloFavoritas) "⭐ Solo favoritas" else "☆ Todas"
    }

    private companion object {
        const val CLAVE_SOLO_FAVORITAS = "soloFavoritas"
    }
}
