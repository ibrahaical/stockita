package com.stockita.feature.tugas.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stockita.feature.tugas.model.TaskCategory
import com.stockita.ui.theme.Primary700
import com.stockita.ui.theme.SurfaceColor
import com.stockita.ui.theme.SurfaceMuted
import com.stockita.ui.theme.TextTertiary

/**
 * Segmented Control Filter Tugas (design.md §8.6 & §12.2):
 * - Wadah: SurfaceMuted r-full tinggi 38dp, padding 3dp
 * - Segmen Aktif: SurfaceColor + shadow-sm + teks Primary700 11sp/600
 * - Segmen Nonaktif: Transparan + teks TextTertiary
 */
@Composable
fun TaskSegmentedControl(
    selectedCategory: TaskCategory,
    todayCount: Int,
    scheduledCount: Int,
    doneCount: Int,
    allCount: Int,
    onCategorySelected: (TaskCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val segments = listOf(
        TaskCategory.TODAY to todayCount,
        TaskCategory.SCHEDULED to scheduledCount,
        TaskCategory.DONE to doneCount,
        TaskCategory.ALL to allCount
    )

    Surface(
        shape = CircleShape,
        color = SurfaceMuted,
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            segments.forEach { (category, count) ->
                val isSelected = selectedCategory == category

                Surface(
                    onClick = { onCategorySelected(category) },
                    shape = CircleShape,
                    color = if (isSelected) SurfaceColor else Color.Transparent,
                    shadowElevation = if (isSelected) 2.dp else 0.dp,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "${category.label}${if (count > 0) " ($count)" else ""}",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (isSelected) Primary700 else TextTertiary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
