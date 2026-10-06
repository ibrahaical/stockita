package com.stockita.feature.resep

import androidx.compose.foundation.clickable
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
fun ResepScreen(
    viewModel: ResepViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (uiState.selectedProduct != null) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Bahan ke Resep")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.error != null) {
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
            } else {
                // Dropdown or list of products to select
                Text("Pilih Produk (Mode: RECIPE):", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                    items(uiState.products, key = { it.id }) { product ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable {
                                viewModel.selectProduct(product.id)
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (uiState.selectedProduct?.id == product.id) 
                                    MaterialTheme.colorScheme.primaryContainer 
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(product.name, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.selectedProduct != null) {
                    Text("Resep untuk: ${uiState.selectedProduct!!.name}", style = MaterialTheme.typography.titleLarge)
                    Text("Total HPP: Rp ${uiState.totalHpp}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.recipeItems.isEmpty()) {
                        Text("Resep masih kosong.")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(uiState.recipeItems, key = { it.recipeItemId }) { item ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(item.materialName, style = MaterialTheme.typography.bodyLarge)
                                        Text("${item.qty} ${item.unit} @ Rp ${item.lastCost} = Rp ${(item.qty * item.lastCost).toLong()}", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text("Silakan pilih produk di atas untuk melihat resep.")
                }
            }
        }

        if (showAddDialog) {
            AddRecipeItemDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { materialId, qty ->
                    viewModel.addRecipeItem(materialId, qty)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AddRecipeItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long, Double) -> Unit
) {
    var materialIdStr by remember { mutableStateOf("") }
    var qtyStr by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Bahan ke Resep") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = materialIdStr,
                    onValueChange = { materialIdStr = it },
                    label = { Text("ID Bahan Baku") }
                )
                OutlinedTextField(
                    value = qtyStr,
                    onValueChange = { qtyStr = it },
                    label = { Text("Kuantitas") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val mId = materialIdStr.toLongOrNull() ?: 0L
                    val qty = qtyStr.toDoubleOrNull() ?: 0.0
                    onConfirm(mId, qty)
                },
                enabled = materialIdStr.isNotBlank() && qtyStr.isNotBlank()
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
