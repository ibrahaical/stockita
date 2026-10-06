package com.stockita.feature.dashboard.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.stockita.feature.dashboard.model.ChartPoint
import kotlin.math.roundToInt

@Composable
fun StockMarketChart(
    points: List<ChartPoint>,
    onScrub: (ChartPoint?) -> Unit,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFFFF6900)
) {
    var scrubIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(scrubIndex, points) {
        if (scrubIndex != null && scrubIndex in points.indices) {
            onScrub(points[scrubIndex!!])
        } else {
            onScrub(null)
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .pointerInput(points) {
                if (points.isEmpty()) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val w = size.width.toFloat()
                    val total = points.size
                    if (total > 1 && w > 0f) {
                        val initialIdx = ((down.position.x / w) * (total - 1))
                            .roundToInt()
                            .coerceIn(0, total - 1)
                        scrubIndex = initialIdx
                    }

                    do {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: break
                        if (change.pressed) {
                            change.consume()
                            if (total > 1 && w > 0f) {
                                val currentIdx = ((change.position.x / w) * (total - 1))
                                    .roundToInt()
                                    .coerceIn(0, total - 1)
                                scrubIndex = currentIdx
                            }
                        }
                    } while (event.changes.any { it.pressed })

                    // Lifted finger
                    scrubIndex = null
                }
            }
    ) {
        if (points.isEmpty()) return@Canvas

        val w = size.width
        val h = size.height
        val paddingTop = 16.dp.toPx()
        val paddingBottom = 20.dp.toPx()
        val usableHeight = h - paddingTop - paddingBottom

        val minVal = points.minOf { it.amount }
        val maxVal = points.maxOf { it.amount }
        val diff = (maxVal - minVal).toFloat()

        // Calculate point coordinates
        val coords = points.mapIndexed { index, point ->
            val x = if (points.size > 1) {
                index.toFloat() / (points.size - 1) * w
            } else {
                w / 2f
            }
            val y = if (diff > 0f) {
                paddingTop + usableHeight * (1f - (point.amount - minVal).toFloat() / diff)
            } else {
                paddingTop + usableHeight * 0.75f // flat line when no difference
            }
            Offset(x, y)
        }

        // Build smooth cubic Bezier path
        val linePath = Path().apply {
            moveTo(coords.first().x, coords.first().y)
            for (i in 1 until coords.size) {
                val prev = coords[i - 1]
                val curr = coords[i]
                val midX = prev.x + (curr.x - prev.x) / 2f
                cubicTo(
                    midX, prev.y,
                    midX, curr.y,
                    curr.x, curr.y
                )
            }
        }

        // Build gradient fill path under the line
        val fillPath = Path().apply {
            addPath(linePath)
            lineTo(coords.last().x, h)
            lineTo(coords.first().x, h)
            close()
        }

        // 1. Draw gradient fill (stock market glow)
        val gradientBrush = Brush.verticalGradient(
            colors = listOf(
                lineColor.copy(alpha = 0.40f),
                lineColor.copy(alpha = 0.15f),
                lineColor.copy(alpha = 0.0f)
            ),
            startY = paddingTop,
            endY = h
        )
        drawPath(fillPath, brush = gradientBrush)

        // 2. Draw smooth stroke line
        drawPath(
            linePath,
            color = lineColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 3. Draw active scrubber if user is touching/holding
        scrubIndex?.let { index ->
            if (index in coords.indices) {
                val target = coords[index]

                // Vertical dashed indicator
                drawLine(
                    color = Color(0xFFD1D5DB),
                    start = Offset(target.x, 0f),
                    end = Offset(target.x, h),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f))
                )

                // Outer halo glow
                drawCircle(
                    color = lineColor.copy(alpha = 0.25f),
                    radius = 11.dp.toPx(),
                    center = target
                )

                // Inner circle
                drawCircle(
                    color = lineColor,
                    radius = 5.dp.toPx(),
                    center = target
                )

                // Center white dot
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = target
                )
            }
        }
    }
}
