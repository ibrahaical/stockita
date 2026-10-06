package com.stockita.core.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.stockita.feature.dashboard.BerandaScreen
import com.stockita.feature.kasir.KasirScreen
import com.stockita.feature.pengeluaran.PengeluaranScreen
import com.stockita.feature.produk.ProdukScreen
import com.stockita.feature.resep.ResepScreen
import com.stockita.feature.stok.BahanScreen
import com.stockita.feature.stok.MutasiScreen
import com.stockita.feature.tugas.TugasScreen
import com.stockita.ui.theme.InkSoft
import com.stockita.ui.theme.Line
import com.stockita.ui.theme.Orange

import androidx.compose.material.icons.automirrored.filled.List

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Beranda : Screen("beranda", "Beranda", Icons.Default.Home)
    object Stok : Screen("stok", "Stok", Icons.AutoMirrored.Filled.List)
    object Kasir : Screen("kasir", "Kasir", Icons.Default.ShoppingCart)
    object Tugas : Screen("tugas", "Tugas", Icons.Default.CheckCircle)
    object Lainnya : Screen("lainnya", "Lainnya", Icons.Default.Menu)
}

val bottomNavItems = listOf(
    Screen.Beranda,
    Screen.Stok,
    Screen.Kasir,
    Screen.Tugas,
    Screen.Lainnya
)

@Composable
fun StockitaAppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp,
                border = BorderStroke(0.5.dp, Line)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .height(68.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    bottomNavItems.forEach { screen ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        val isCenterKasir = screen == Screen.Kasir

                        if (isCenterKasir) {
                            // Tombol Kasir di tengah: Menonjol dengan rounded orange
                            Column(
                                modifier = Modifier
                                    .weight(1.2f)
                                    .fillMaxHeight()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .shadow(
                                            elevation = if (isSelected) 8.dp else 4.dp,
                                            shape = RoundedCornerShape(16.dp),
                                            spotColor = Orange.copy(alpha = 0.6f),
                                            ambientColor = Orange.copy(alpha = 0.3f)
                                        )
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(Color(0xFFFF7A1A), Orange)
                                            ),
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.title,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = screen.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Orange else InkSoft
                                )
                            }
                        } else {
                            // Tombol navigasi standar (Beranda, Stok, Tugas, Lainnya)
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title,
                                    tint = if (isSelected) Orange else InkSoft,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = screen.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Orange else InkSoft
                                )
                            }
                        }
                    }
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
            composable(Screen.Stok.route) {
                StokMainScreen()
            }
            composable(Screen.Kasir.route) {
                KasirScreen()
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
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Orange
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    selectedContentColor = Orange,
                    unselectedContentColor = InkSoft
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
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Orange
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    selectedContentColor = Orange,
                    unselectedContentColor = InkSoft
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
