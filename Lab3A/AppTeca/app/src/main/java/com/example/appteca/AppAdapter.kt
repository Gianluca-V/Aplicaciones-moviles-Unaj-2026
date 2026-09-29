package com.example.appteca

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppAdapter(
    private var items: List<App>,
    private val onAppClick: (App) -> Unit,
    private val onFavoritoClick: (App) -> Unit
) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    // El ViewHolder: "sostiene" las vistas de UNA fila ya encontradas,
    // para no buscarlas (findViewById) en cada scroll.
    class AppViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre: TextView = v.findViewById(R.id.tvNombre)
        val tvCategoria: TextView = v.findViewById(R.id.tvCategoria)
        val tvEstrella: TextView = v.findViewById(R.id.tvEstrella)
    }

    // Pregunta 2: ¿cómo se crea una fila vacía? (se llama pocas veces: recicla)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        return AppViewHolder(v)
    }

    // Pregunta 3: ¿cómo se llena la fila con el dato de la posición?
    // (se llama TODO el tiempo: cada reciclado pasa por acá)
    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = items[position]
        holder.tvNombre.text = app.nombre
        holder.tvCategoria.text = app.categoria
        holder.tvEstrella.text = if (app.esFavorita) "⭐" else "☆"
        holder.itemView.setOnClickListener { onAppClick(app) }
        holder.tvEstrella.setOnClickListener { onFavoritoClick(app) }
    }

    fun actualizarLista(nueva: List<App>) {
        items = nueva
        notifyDataSetChanged()
    }

    // Pregunta 1: ¿cuántos hay?
    override fun getItemCount() = items.size
}
