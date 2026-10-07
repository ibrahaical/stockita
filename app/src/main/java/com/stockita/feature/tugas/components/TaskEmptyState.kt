package com.stockita.feature.tugas.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stockita.ui.theme.Primary100
import com.stockita.ui.theme.Primary600
import com.stockita.ui.theme.TextPrimary
import com.stockita.ui.theme.TextTertiary

/**
 * Tampilan Kosong (Empty State) sesuai design.md §8.7:
 * - Lingkaran 120dp Primary100 + ikon CheckCircleOutline 48dp Primary600
 * - Judul subtitle (15sp bold)
 * - Deskripsi body-sm TextTertiary (maksimal 2 baris)
 * - Tombol primary kecil (§8.1: tinggi 36dp, r-full, Primary600, teks putih)
 */
@Composable
fun TaskEmptyState(
    isSearching: Boolean,
    onAddNewTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Primary100, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircleOutline,
                    contentDescription = null,
                    tint = Primary600,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isSearching) "Tugas Tidak Ditemukan" else "Belum Ada Tugas",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isSearching) {
                    "Coba gunakan kata kunci pencarian yang lain."
                } else {
                    "Tekan tombol di bawah untuk mencatat rencana atau pekerjaan Anda."
                },
                fontSize = 12.sp,
                color = TextTertiary,
                modifier = Modifier.padding(horizontal = 24.dp),
                lineHeight = 18.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol primary kecil (§8.7 & §8.1: tinggi 36, r-full, primary-600)
            Surface(
                onClick = onAddNewTaskClick,
                shape = CircleShape,
                color = Primary600,
                modifier = Modifier.height(36.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Tambah Tugas",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
