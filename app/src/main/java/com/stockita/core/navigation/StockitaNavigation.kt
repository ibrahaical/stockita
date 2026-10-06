package com.stockita.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Column
import com.stockita.feature.produk.ProdukScreen
import com.stockita.feature.stok.BahanScreen
import com.stockita.feature.stok.MutasiScreen
import com.stockita.feature.resep.ResepScreen
import com.stockita.feature.kasir.KasirScreen
import com.stockita.feature.pengeluaran.PengeluaranScreen
import com.stockita.feature.tugas.TugasScreen
import com.stockita.feature.dashboard.BerandaScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Beranda : Screen("beranda", "Beranda", Icons.Default.Home)
    object Kasir : Screen("kasir", "Kasir", Icons.Default.ShoppingCart)
    object Stok : Screen("stok", "Stok", Icons.Default.List)
    object Tugas : Screen("tugas", "Tugas", Icons.Default.CheckCircle)
    object Lainnya : Screen("lainnya", "Lainnya", Icons.Default.Menu)
}

val bottomNavItems = listOf(
    Screen.Beranda,
    Screen.Kasir,
    Screen.Stok,
    Screen.Tugas,
    Screen.Lainnya
)

@Composable
fun StockitaAppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Beranda.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Beranda.route) { 
                BerandaScreen()
            }
            composable(Screen.Kasir.route) { 
                KasirScreen()
            }
            composable(Screen.Stok.route) { 
                StokMainScreen()
            }
            composable(Screen.Tugas.route) { 
                TugasScreen()
            }
            composable(Screen.Lainnya.route) { 
                LainnyaMainScreen()
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}

@Composable
fun StokMainScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Bahan", "Produk", "Mutasi")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }
        
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTabIndex) {
                0 -> BahanScreen()
                1 -> ProdukScreen()
                2 -> MutasiScreen()
            }
        }
    }
}
@Composable
fun LainnyaMainScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Resep", "Pengeluaran", "Laporan", "Backup")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }
        
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTabIndex) {
                0 -> ResepScreen()
                1 -> PengeluaranScreen()
                2 -> com.stockita.feature.laporan.LaporanScreen()
                3 -> com.stockita.feature.backup.BackupScreen()
            }
        }
    }
}
