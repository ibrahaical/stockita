package com.stockita.feature.tugas.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stockita.ui.theme.*

/**
 * Dialog Konfirmasi Hapus Tugas (design.md §8.5):
 * - Lebar: min(320px, 100% - 48px), radius r-xl (24px)
 * - Ikon bulat 48px DangerBg dengan ikon DeleteOutline
 * - Tombol bertumpuk: Destructive di atas, Secondary Batal di bawah
 */
@Composable
fun DeleteConfirmationDialog(
    taskTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceColor,
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 0.dp,
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(DangerBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = Danger,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        title = {
            Text(
                text = "Hapus Tugas",
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 17.sp
            )
        },
        text = {
            Text(
                text = "Apakah Anda yakin ingin menghapus tugas \"$taskTitle\"? Tindakan ini tidak dapat dibatalkan.",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tombol Destructive Pill (§8.1 & §8.5)
                Surface(
                    onClick = onConfirm,
                    shape = CircleShape,
                    color = DangerBg,
                    border = BorderStroke(1.dp, Danger.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Hapus Tugas",
                            color = DangerText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Tombol Batal Pill
                Surface(
                    onClick = onDismiss,
                    shape = CircleShape,
                    color = SurfaceMuted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Batal",
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        },
        dismissButton = {}
    )
}
