package com.stockita.feature.laporan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LaporanScreen(
    viewModel: LaporanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.error != null) {
            Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section: Laba Rugi
                item {
                    Text("Laba/Rugi (Bulan Ini)", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val omzet = uiState.labaRugi?.omzet ?: 0L
                            val hpp = uiState.labaRugi?.hpp ?: 0L
                            val expenses = uiState.labaRugi?.expenses ?: 0L
                            val laba = omzet - hpp - expenses

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Omzet:")
                                Text("Rp $omzet")
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total HPP:")
                                Text("- Rp $hpp", color = MaterialTheme.colorScheme.error)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Pengeluaran Lain:")
                                Text("- Rp $expenses", color = MaterialTheme.colorScheme.error)
                            }
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Laba Bersih:", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = "Rp $laba",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (laba >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                // Section: Top Products
                item {
                    Text("Produk Terlaris", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (uiState.topProducts.isEmpty()) {
                    item {
                        Text("Belum ada data penjualan.")
                    }
                } else {
                    items(uiState.topProducts) { product ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(product.itemName, style = MaterialTheme.typography.titleMedium)
                                    Text("Terjual: ${product.totalQty.toInt()}", style = MaterialTheme.typography.bodySmall)
                                }
                                Text("Rp ${product.totalRevenue}", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
