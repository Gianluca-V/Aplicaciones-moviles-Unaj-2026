package com.example.appteca3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appteca3.ui.theme.AppTeca3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTeca3Theme {
                // Scaffold only provides the system-bar insets (targetSdk 36 is edge-to-edge).
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        AppTecaNav()
                    }
                }
            }
        }
    }
}

// Desafío 3 — La biblioteca de verdad: el `if (seleccionada != null)` se reemplazó por un
// NavHost con dos rutas. El ViewModel se pide FUERA del NavHost: adentro de un destino,
// viewModel() quedaría atado a esa entrada de la pila y cada pantalla tendría el suyo.
@Composable
fun AppTecaNav(vm: AppTecaViewModel = viewModel()) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = RUTA_LISTA) {
        composable(RUTA_LISTA) {
            PantallaAppTeca(vm, onAppClick = { app -> nav.navigate("detalle/${app.id}") })
        }
        composable(
            route = "detalle/{appId}",
            arguments = listOf(navArgument("appId") { type = NavType.IntType })
        ) { entrada ->
            val appId = entrada.arguments?.getInt("appId")
            val catalogo by vm.catalogo.collectAsStateWithLifecycle()
            val app = catalogo.find { it.id == appId }
            if (app != null) {
                DetalleApp(app = app, onFavoritoClick = { vm.alternarFavorita(app) })
            }
        }
    }
}

private const val RUTA_LISTA = "lista"

@Composable
fun PantallaAppTeca(vm: AppTecaViewModel, onAppClick: (App) -> Unit) {
    val lista by vm.listaVisible.collectAsStateWithLifecycle()
    val modoFav by vm.modoSoloFavoritas.collectAsStateWithLifecycle()
    val textoBusqueda by vm.textoBusqueda.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = { nuevo -> vm.buscar(nuevo) },
            label = { Text("Buscar por nombre o categoría…") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Button(
            onClick = { vm.alternarModo() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(if (modoFav) "⭐ Solo favoritas" else "☆ Todas")
        }

        ListaApps(
            apps = lista,
            onAppClick = onAppClick,
            onFavoritoClick = { app -> vm.alternarFavorita(app) }
        )
    }
}

// Con Navigation Compose el "atrás" lo resuelve la pila real: ya no hace falta BackHandler.
@Composable
fun DetalleApp(app: App, onFavoritoClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(app.nombre, style = MaterialTheme.typography.headlineLarge)
        Text(app.categoria, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))
        Text(app.descripcion, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onFavoritoClick) {
            Text(if (app.esFavorita) "★ Quitar de favoritas" else "☆ Marcar favorita")
        }
    }
}

@Composable
fun FilaApp(
    app: App,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(app.nombre, style = MaterialTheme.typography.titleMedium)
            Text(app.categoria, style = MaterialTheme.typography.bodySmall)
        }
        Text(
            text = if (app.esFavorita) "⭐" else "☆",
            fontSize = 24.sp,
            modifier = Modifier
                .clickable { onFavoritoClick() }
                .padding(8.dp)
        )
    }
}

@Composable
fun ListaApps(
    apps: List<App>,
    onAppClick: (App) -> Unit,
    onFavoritoClick: (App) -> Unit
) {
    LazyColumn {
        items(apps, key = { it.id }) { app ->
            FilaApp(
                app = app,
                onClick = { onAppClick(app) },
                onFavoritoClick = { onFavoritoClick(app) },
                // Desafío 4 — El caramelo: gracias a key = { it.id } Compose sabe qué fila es
                // cuál entre recomposiciones, y puede animar su entrada, salida y cambio de lugar.
                modifier = Modifier.animateItem()
            )
        }
    }
}
