package com.stockita.feature.tugas.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.stockita.ui.theme.BorderStrong
import com.stockita.ui.theme.Primary600

/**
 * Komponen Toggle Switch reusable sesuai design.md §8.2:
 * - Ukuran track: 40×24 dp, radius: r-full (CircleShape)
 * - Status Aktif: Primary600 (#6C4FD3)
 * - Status Nonaktif: BorderStrong (#D1D5DB)
 * - Knob: 18 px bulat putih dengan shadow halus
 * - Animasi transisi 150 ms ease-out
 */
@Composable
fun StockitaToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = checked, label = "ToggleTransition")
    
    // Pergeseran posisi knob (offset horizontal)
    val knobOffset by transition.animateDp(
        transitionSpec = { tween(durationMillis = 150) },
        label = "KnobOffset"
    ) { isChecked ->
        if (isChecked) 18.dp else 2.dp
    }

    // Animasi warna track dari border-strong ke primary-600
    val trackColor by transition.animateColor(
        transitionSpec = { tween(durationMillis = 150) },
        label = "TrackColor"
    ) { isChecked ->
        if (isChecked) Primary600 else BorderStrong
    }

    Box(
        modifier = modifier
            .size(width = 40.dp, height = 24.dp)
            .clip(CircleShape)
            .background(trackColor)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .padding(start = knobOffset)
                .size(18.dp)
                .shadow(elevation = 1.dp, shape = CircleShape)
                .background(Color.White, CircleShape)
        )
    }
}
