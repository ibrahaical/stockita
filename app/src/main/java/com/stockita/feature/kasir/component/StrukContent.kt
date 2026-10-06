package com.stockita.feature.kasir.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StrukContent(
    modifier: Modifier = Modifier,
    transactionId: String = "TRX-1001",
    items: List<Pair<String, Long>> = listOf(
        "Kopi Susu" to 15000L,
        "Roti Bakar" to 12000L,
        "Air Mineral" to 5000L
    ),
    subtotal: Long = 32000L,
    discount: Long = 2000L,
    total: Long = 30000L,
    paymentMethod: String = "TUNAI",
    dateMillis: Long = System.currentTimeMillis()
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }
    
    val dateFormatter = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id", "ID"))
    val dateString = dateFormatter.format(Date(dateMillis))

    Column(
        modifier = modifier
            .background(Color.White)
            .padding(24.dp)
            .width(300.dp) // Simulated thermal printer width
    ) {
        Text(
            text = "STOCKITA TOKO",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Monospace,
            color = Color.Black
        )
        Text(
            text = "Jl. Contoh Alamat No. 123",
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Monospace,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = transactionId, fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = Color.Black)
            Text(text = dateString, fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = Color.Black)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.Black, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        items.forEach { (itemName, price) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = itemName, fontSize = 14.sp, fontFamily = FontFamily.Monospace, color = Color.Black)
                Text(text = formatter.format(price), fontSize = 14.sp, fontFamily = FontFamily.Monospace, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Subtotal", fontSize = 14.sp, fontFamily = FontFamily.Monospace, color = Color.Black)
            Text(text = formatter.format(subtotal), fontSize = 14.sp, fontFamily = FontFamily.Monospace, color = Color.Black)
        }
        
        if (discount > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Diskon", fontSize = 14.sp, fontFamily = FontFamily.Monospace, color = Color.Black)
                Text(text = "-${formatter.format(discount)}", fontSize = 14.sp, fontFamily = FontFamily.Monospace, color = Color.Black)
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "TOTAL", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color.Black)
            Text(text = formatter.format(total), fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color.Black)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Pembayaran", fontSize = 14.sp, fontFamily = FontFamily.Monospace, color = Color.Black)
            Text(text = paymentMethod, fontSize = 14.sp, fontFamily = FontFamily.Monospace, color = Color.Black)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Terima Kasih!",
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Monospace,
            color = Color.Black
        )
    }
}
