package app.krafted.jokersescaperoom.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TimerBar(
    timeRemaining: Float,
    modifier: Modifier = Modifier
) {
    val barColor = if (timeRemaining < 0.5f) Color(0xFFEF5350) else Color(0xFFFF9800)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(Color(0xFF2A2A2A))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = timeRemaining.coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(barColor)
        )
    }
}
