package com.stockita.feature.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stockita.R
import com.stockita.core.util.formatRupiah
import com.stockita.feature.dashboard.component.StockMarketChart
import com.stockita.feature.dashboard.model.ChartDataHelper
import com.stockita.feature.dashboard.model.ChartPoint
import com.stockita.feature.dashboard.model.TimeRangeFilter

@Composable
fun BerandaScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf(TimeRangeFilter.ONE_DAY) }
    var scrubbedPoint by remember { mutableStateOf<ChartPoint?>(null) }

    val chartDataSet = remember(uiState.allTransactions, selectedFilter) {
        ChartDataHelper.generateChartData(uiState.allTransactions, selectedFilter)
    }

    val displayedAmount = scrubbedPoint?.amount ?: chartDataSet.totalAmount
    val displayedLabel = scrubbedPoint?.formattedTime ?: chartDataSet.periodLabel

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.stockita_logo_horizontal),
                        contentDescription = "Stockita Logo",
                        modifier = Modifier.height(36.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Interactive Omzet Section
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Title Omzet
                                Text(
                                    text = "Omzet",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color(0xFF6B7280)
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                // Nominal Besar
                                Text(
                                    text = formatRupiah(displayedAmount),
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937)
                                )
                                Spacer(modifier = Modifier.height(2.dp))

                                // Tiny teks periode / tanggal scrub realtime
                                Text(
                                    text = displayedLabel,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (scrubbedPoint != null) Color(0xFFFF6900) else Color(0xFF9CA3AF),
                                    fontWeight = if (scrubbedPoint != null) FontWeight.SemiBold else FontWeight.Normal
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Grafik Garis Stock Market dengan Gradiasi
                                StockMarketChart(
                                    points = chartDataSet.points,
                                    onScrub = { point -> scrubbedPoint = point },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Button filter 1D 1W 1M 3M YTD 1Y 3Y 5Y
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    TimeRangeFilter.entries.forEach { filter ->
                                        val isSelected = filter == selectedFilter
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                selectedFilter = filter
                                                scrubbedPoint = null
                                            },
                                            label = {
                                                Text(
                                                    text = filter.code,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFFFF6900),
                                                selectedLabelColor = Color.White,
                                                containerColor = Color(0xFFF7F7F8),
                                                labelColor = Color(0xFF6B7280)
                                            ),
                                            border = null,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2. Ringkasan Laba Bersih
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Laba Bersih (Hari Ini)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = formatRupiah(uiState.labaHariIni),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.labaHariIni >= 0) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    // 3. Stok Menipis
                    item {
                        Text("Stok Menipis", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    if (uiState.stokMenipis.isEmpty()) {
                        item { Text("Semua stok aman.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    } else {
                        items(uiState.stokMenipis.size) { index ->
                            val material = uiState.stokMenipis[index]
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(material.name, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onErrorContainer)
                                        Text("Sisa: ${material.stock} ${material.unit} (Min: ${material.minStock})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                                    }
                                }
                            }
                        }
                    }

                    // 4. Tugas Aktif
                    item {
                        Text("Tugas Aktif", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    if (uiState.tugasHariIni.isEmpty()) {
                        item { Text("Tidak ada tugas aktif.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    } else {
                        items(uiState.tugasHariIni.size) { index ->
                            val task = uiState.tugasHariIni[index]
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(task.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                    if (!task.note.isNullOrBlank()) {
                                        Text(task.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
