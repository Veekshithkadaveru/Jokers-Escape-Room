package app.krafted.jokersescaperoom.ui.puzzle

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersescaperoom.ui.components.AttemptDiamonds
import kotlinx.coroutines.delay

internal val textShadow = TextStyle(
    shadow = Shadow(
        color = Color.Black.copy(alpha = 0.9f),
        offset = Offset(0f, 2f),
        blurRadius = 10f
    )
)

@Composable
fun SparksParticleSystem(
    accentColor: Color,
    modifier: Modifier = Modifier,
    particleCount: Int = 22
) {
    val particles = remember {
        List(particleCount) {
            SparkParticle(
                x = (0..100).random() / 100f,
                y = (0..100).random() / 100f,
                size = (2..6).random().toFloat(),
                speed = (5..15).random() / 1500f,
                alpha = (3..8).random() / 10f
            )
        }
    }

    var time by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { time = it }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { p ->
            p.y -= p.speed
            if (p.y < -0.1f) {
                p.y = 1.1f
                p.x = (0..100).random() / 100f
            }

            val xPos = p.x * size.width
            val yPos = p.y * size.height

            drawCircle(
                color = accentColor.copy(alpha = p.alpha * 0.4f),
                radius = p.size,
                center = Offset(xPos, yPos)
            )
        }
    }
}

private class SparkParticle(
    var x: Float,
    var y: Float,
    val size: Float,
    val speed: Float,
    val alpha: Float
)

@Composable
fun PuzzleHeader(
    title: String,
    subtitle: String = "",
    symbolRes: Int,
    accentColor: Color,
    attemptsRemaining: Int,
    modifier: Modifier = Modifier
) {
    val pulse = rememberInfiniteTransition(label = "sym_pulse")
    val glowAlpha by pulse.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sym_glow"
    )

    val shimmer by pulse.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "title_shimmer"
    )

    var headerReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        headerReady = true
    }

    val titleSpacing by animateFloatAsState(
        targetValue = if (headerReady) 2f else 6f,
        animationSpec = tween(650, easing = FastOutSlowInEasing),
        label = "title_spacing"
    )
    val symbolScale by animateFloatAsState(
        targetValue = if (headerReady) 1f else 0.65f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 350f),
        label = "symbol_scale"
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (headerReady) 1f else 0f,
        animationSpec = tween(500, delayMillis = 250),
        label = "subtitle_alpha"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.scale(symbolScale)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        accentColor.copy(alpha = glowAlpha * 0.45f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF1A1A1A), Color(0xFF080808))
                                )
                            )
                            .border(
                                1.dp,
                                Brush.verticalGradient(
                                    listOf(
                                        accentColor.copy(alpha = 0.8f),
                                        accentColor.copy(alpha = 0.2f)
                                    )
                                ),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(symbolRes),
                            contentDescription = null,
                            modifier = Modifier.size(38.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(Modifier.width(14.dp))

                Column {
                    val shimmerBrush = Brush.linearGradient(
                        colors = listOf(
                            accentColor,
                            Color.White,
                            accentColor
                        ),
                        start = Offset(shimmer * 1000f - 500f, 0f),
                        end = Offset(shimmer * 1000f, 0f)
                    )

                    Text(
                        text = title.uppercase(),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = titleSpacing.sp,
                        style = textShadow.copy(
                            brush = shimmerBrush
                        )
                    )
                    if (subtitle.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = subtitle.uppercase(),
                            color = Color.White.copy(alpha = subtitleAlpha * 0.85f),
                            fontSize = 11.sp,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.SemiBold,
                            style = textShadow
                        )
                    }
                }
            }

            AttemptDiamonds(
                attemptsRemaining = attemptsRemaining,
                accentColor = accentColor
            )
        }

        Spacer(Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(accentColor, accentColor.copy(alpha = 0.1f), Color.Transparent)
                    )
                )
        )
    }
}

@Composable
fun SectionDivider(accentColor: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(accentColor.copy(alpha = 0.15f))
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .size(5.dp)
                .rotate(45f)
                .background(accentColor.copy(alpha = 0.5f))
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(accentColor.copy(alpha = 0.15f))
        )
    }
}

@Composable
fun PuzzleConfirmButton(
    text: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val color = if (enabled) accentColor else accentColor.copy(alpha = 0.3f)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        accentColor.copy(alpha = if (enabled) 0.15f else 0.05f),
                        Color(0xFF050505)
                    )
                )
            )
            .then(
                if (enabled) {
                    val pulse = rememberInfiniteTransition(label = "btn_shimmer")
                    val shimmerMove by pulse.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(4000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "shimmer"
                    )
                    Modifier.background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            start = Offset(shimmerMove * 1000f - 500f, 0f),
                            end = Offset(shimmerMove * 1000f, 0f)
                        )
                    )
                } else Modifier
            )
            .border(
                1.5.dp,
                Brush.verticalGradient(
                    listOf(color, color.copy(alpha = 0.1f))
                ),
                RoundedCornerShape(4.dp)
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            fontSize = 13.sp,
            style = textShadow
        )
    }
}
