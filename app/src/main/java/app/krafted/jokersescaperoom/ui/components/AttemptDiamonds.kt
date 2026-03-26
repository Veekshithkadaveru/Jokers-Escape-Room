package app.krafted.jokersescaperoom.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun AttemptDiamonds(
    attemptsRemaining: Int,
    maxAttempts: Int = 3,
    accentColor: Color = Color(0xFFB71C1C),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        repeat(maxAttempts) { index ->
            val isActive = index < attemptsRemaining

            val scale by animateFloatAsState(
                targetValue = if (isActive) 1f else 0f,
                animationSpec = tween(durationMillis = 300),
                label = "diamond_scale_$index"
            )
            val alpha by animateFloatAsState(
                targetValue = if (isActive) 1f else 0.25f,
                animationSpec = tween(durationMillis = 300),
                label = "diamond_alpha_$index"
            )

            // Rotated square = diamond shape
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .rotate(45f)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
                    .then(
                        if (isActive) {
                            Modifier.background(accentColor)
                        } else {
                            Modifier.border(1.dp, accentColor.copy(alpha = 0.4f))
                        }
                    )
            )
        }
    }
}
