package com.stockita.feature.kasir

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasirScreen(
    viewModel: KasirViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val checkoutResult by viewModel.checkoutResult.collectAsState()
    val context = LocalContext.current

    var showCartDialog by remember { mutableStateOf(false) }

    LaunchedEffect(checkoutResult) {
        checkoutResult?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearCheckoutResult()
            showCartDialog = false
        }
    }

    Scaffold(
        floatingActionButton = {
            if (cart.isNotEmpty()) {
                val totalQty = cart.sumOf { it.qty }.toInt()
                val totalPrice = cart.sumOf { it.product.price * it.qty }.toLong()
                ExtendedFloatingActionButton(
                    onClick = { showCartDialog = true }
                ) {
                    Text("Keranjang ($totalQty) - Rp $totalPrice")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().statusBarsPadding()) {
            when (val state = uiState) {
                is KasirUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(32.dp))
                }
                is KasirUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                }
                is KasirUiState.Success -> {
                    if (state.products.isEmpty()) {
                        Text("Belum ada produk untuk dijual.", modifier = Modifier.padding(16.dp))
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.products, key = { it.id }) { product ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        viewModel.addToCart(product)
                                    }
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(product.name, style = MaterialTheme.typography.titleMedium)
                                        Text("Rp ${product.price}", style = MaterialTheme.typography.bodyMedium)
                                        Text(product.stockMode, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCartDialog) {
        CartDialog(
            cartItems = cart,
            onDismiss = { showCartDialog = false },
            onUpdateQty = { product, qty -> viewModel.updateCartQty(product, qty) },
            onCheckout = { paymentMethod -> viewModel.checkout(paymentMethod) }
        )
    }
}

@Composable
fun CartDialog(
    cartItems: List<CartItem>,
    onDismiss: () -> Unit,
    onUpdateQty: (com.stockita.core.database.entity.ProductEntity, Double) -> Unit,
    onCheckout: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Keranjang Belanja") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(cartItems) { item ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.product.name, style = MaterialTheme.typography.bodyLarge)
                            Text("Rp ${item.product.price} x ${item.qty}", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(onClick = { onUpdateQty(item.product, item.qty - 1) }) { Text("-") }
                            Text(item.qty.toInt().toString())
                            TextButton(onClick = { onUpdateQty(item.product, item.qty + 1) }) { Text("+") }
                        }
                    }
                }
                item {
                    val subtotal = cartItems.sumOf { it.product.price * it.qty }.toLong()
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Total: Rp $subtotal", style = MaterialTheme.typography.titleMedium)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onCheckout("CASH") },
                enabled = cartItems.isNotEmpty()
            ) {
                Text("Bayar (CASH)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}
