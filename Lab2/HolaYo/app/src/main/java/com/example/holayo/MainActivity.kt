package com.example.holayo

import android.content.Intent
import android.os.Bundle
import android.util.Log // <- Importante para poder usar Log.d
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

data class Perfil(
    val nombre: String,
    val dato: String,
    val apodo: String?,
    val comidaFavorita: String? = null
)

class MainActivity : AppCompatActivity() {
    private val perfil = Perfil(
        nombre = "Gianluca Vespe",
        dato = "Ayudo al backend",
        apodo = null,
        comidaFavorita = "Milanesas con papas"
    )
    private var saludoFormal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("VIDA", "Main → onCreate")
        setContentView(R.layout.activity_main)

        val tvSaludo = findViewById<TextView>(R.id.tvSaludo)
        val tvDato = findViewById<TextView>(R.id.tvDato)
        val btnSaludar = findViewById<Button>(R.id.btnSaludar)

        val comoLlamarme = perfil.apodo ?: perfil.nombre
        tvSaludo.text = "Hola, soy $comoLlamarme"
        tvDato.text = perfil.dato

        // Desafío opcional: la comida favorita es un dato que puede faltar.
        perfil.comidaFavorita?.let {
            tvDato.text = "${perfil.dato} · Comida favorita: $it"
        }

        btnSaludar.setOnClickListener {
            saludoFormal = !saludoFormal
            tvSaludo.text = if (saludoFormal)
                "Hola, soy $comoLlamarme"
            else
                "¡Buenas! Acá $comoLlamarme"
        }

        val btnIrSegunda = findViewById<Button>(R.id.btnIrSegunda)
        btnIrSegunda.setOnClickListener {
            // Intent EXPLÍCITO: nombra al destino por su clase.
            val intent = Intent(this, SegundaActivity::class.java)
            // El equipaje: un dato que viaja con el mensaje.
            intent.putExtra("nombre", perfil.apodo ?: perfil.nombre)
            startActivity(intent)
        }

        val btnCompartir = findViewById<Button>(R.id.btnCompartir)
        btnCompartir.setOnClickListener {
            // Intent IMPLÍCITO: no nombra destino, describe una acción (ACTION_SEND)
            // y es el sistema quien ofrece todas las apps capaces de cumplirla.
            val intentCompartir = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Hola, soy $comoLlamarme. ${perfil.dato}")
            }
            startActivity(Intent.createChooser(intentCompartir, "Compartir mi perfil"))
        }
    }

    // --- MÉTODOS DEL CICLO DE VIDA AGREGADOS ---
    override fun onRestart() { super.onRestart(); Log.d("VIDA", "Main -> onRestart") }
    override fun onStart() { super.onStart(); Log.d("VIDA", "Main -> onStart") }
    override fun onResume() { super.onResume(); Log.d("VIDA", "Main -> onResume") }
    override fun onPause() { super.onPause(); Log.d("VIDA", "Main -> onPause") }
    override fun onStop() { super.onStop(); Log.d("VIDA", "Main -> onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d("VIDA", "Main -> onDestroy") }
}