package com.stockita.feature.tugas.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stockita.ui.theme.*

/**
 * Header Layar Tugas (Pola B sesuai design.md §7.3 & §8.8):
 * - Judul modul 20sp bold
 * - Subteks statistik: completedCount / totalCount & persentase
 * - Tombol tambah cepat 38dp bulat
 * - Linear Progress bar: tinggi 6dp, r-full, track Primary100, fill Primary600
 */
@Composable
fun TaskHeader(
    completedCount: Int,
    totalCount: Int,
    progressPercentage: Int,
    onQuickAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SurfaceColor,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tugas & To-Do",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$completedCount dari $totalCount selesai • $progressPercentage%",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Normal
                    )
                }

                // Tombol Tambah Cepat di App Bar
                Surface(
                    onClick = onQuickAddClick,
                    shape = CircleShape,
                    color = Primary50,
                    border = BorderStroke(1.dp, Primary200),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Tugas Cepat",
                            tint = Primary700,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar linear (§8.8: tinggi 6, r-full, track primary-100, isi primary-600)
            LinearProgressIndicator(
                progress = {
                    if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = Primary600,
                trackColor = Primary100
            )
        }
    }
}
