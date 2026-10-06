package com.stockita.feature.stok

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MutasiScreen(
    viewModel: MutasiViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Mutasi")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is MutasiUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                }
                is MutasiUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                is MutasiUiState.Success -> {
                    if (state.movements.isEmpty()) {
                        Text(
                            text = "Belum ada riwayat mutasi stok",
                            modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.movements, key = { it.id }) { movement ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "${movement.reason} - ${movement.targetType} ID: ${movement.targetId}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "Qty: ${if (movement.qty > 0) "+" else ""}${movement.qty}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = if (movement.qty > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                        )
                                        Text(
                                            text = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date(movement.createdAt)),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddMutasiDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { type, id, qty, reason ->
                    viewModel.adjustStock(type, id, qty, reason)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AddMutasiDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Long, Double, String) -> Unit
) {
    var targetType by remember { mutableStateOf("MATERIAL") } // "MATERIAL" or "PRODUCT"
    var targetIdStr by remember { mutableStateOf("") }
    var qtyStr by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("PURCHASE") } // "PURCHASE", "ADJUST", "WASTE"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mutasi Stok") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Should ideally use dropdowns for these
                OutlinedTextField(
                    value = targetType,
                    onValueChange = { targetType = it },
                    label = { Text("Tipe (MATERIAL / PRODUCT)") }
                )
                OutlinedTextField(
                    value = targetIdStr,
                    onValueChange = { targetIdStr = it },
                    label = { Text("ID Target") }
                )
                OutlinedTextField(
                    value = qtyStr,
                    onValueChange = { qtyStr = it },
                    label = { Text("Qty (+/-)") }
                )
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Alasan (PURCHASE/ADJUST/WASTE)") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val targetId = targetIdStr.toLongOrNull() ?: 0L
                    val qty = qtyStr.toDoubleOrNull() ?: 0.0
                    onConfirm(targetType, targetId, qty, reason)
                },
                enabled = targetIdStr.isNotBlank() && qtyStr.isNotBlank()
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
