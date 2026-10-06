package com.stockita.feature.produk

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdukScreen(
    viewModel: ProdukViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Produk")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is ProdukUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                }
                is ProdukUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                is ProdukUiState.Success -> {
                    if (state.products.isEmpty()) {
                        Text(
                            text = "Belum ada produk",
                            modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.products, key = { it.id }) { product ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                                        Text(text = "Rp ${product.price}", style = MaterialTheme.typography.bodyMedium)
                                        Text(text = "Mode: ${product.stockMode}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddProductDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, price, mode ->
                    viewModel.addProduct(name, price, mode)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Long, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var stockMode by remember { mutableStateOf("DIRECT") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Produk") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Produk") }
                )
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Harga (Rp)") }
                )
                // In real app, this should be a Dropdown. Using text field for brevity
                OutlinedTextField(
                    value = stockMode,
                    onValueChange = { stockMode = it },
                    label = { Text("Mode (NONE/DIRECT/RECIPE)") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val price = priceStr.toLongOrNull() ?: 0L
                    onConfirm(name, price, stockMode)
                },
                enabled = name.isNotBlank() && priceStr.isNotBlank()
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
