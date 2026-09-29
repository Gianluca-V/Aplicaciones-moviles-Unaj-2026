package com.example.appteca

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val vm: AppTecaViewModel by viewModels()
    private lateinit var adapter: AppAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("VIDA", "Main → onCreate")
        setContentView(R.layout.activity_main)

        adapter = AppAdapter(emptyList(),
            onAppClick = { app ->
                val intent = Intent(this, DetalleActivity::class.java)
                intent.putExtra("appId", app.id)
                startActivity(intent)
            },
            onFavoritoClick = { app -> vm.alternarFavorita(app) })

        val rv = findViewById<RecyclerView>(R.id.rvApps)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        findViewById<EditText>(R.id.etBuscar).addTextChangedListener { texto ->
            vm.buscar(texto.toString())
        }

        findViewById<Button>(R.id.btnSoloFav).setOnClickListener {
            vm.alternarModo()
        }

        // ── LAS suscripciones: el único lugar donde el estado toca la pantalla ──
        vm.listaVisible.observe(this) { lista ->
            adapter.actualizarLista(lista)
        }
        vm.modoSoloFavoritas.observe(this) { activo ->
            findViewById<Button>(R.id.btnSoloFav).text =
                if (activo) "⭐ Solo favoritas" else "☆ Todas"
        }
    }

    override fun onResume() {
        super.onResume()
        vm.refrescar()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("VIDA", "Main → onDestroy")
    }
}
