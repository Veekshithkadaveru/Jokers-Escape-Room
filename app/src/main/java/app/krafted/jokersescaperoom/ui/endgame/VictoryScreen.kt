package app.krafted.jokersescaperoom.ui.endgame

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import app.krafted.jokersescaperoom.R
import app.krafted.jokersescaperoom.Routes
import app.krafted.jokersescaperoom.ui.components.JokerMonologue
import app.krafted.jokersescaperoom.ui.puzzle.PuzzleConfirmButton
import app.krafted.jokersescaperoom.ui.puzzle.SparksParticleSystem
import app.krafted.jokersescaperoom.ui.puzzle.textShadow
import kotlinx.coroutines.delay

private val goldColor = Color(0xFFFFD700)
private val goldDark = Color(0xFFB8860B)

@Composable
fun VictoryScreen(navController: NavController) {
    val portraitScale = remember { Animatable(0.75f) }
    val portraitAlpha = remember { Animatable(0f) }
    val titleOffset = remember { Animatable(40f) }
    val titleAlpha = remember { Animatable(0f) }

    var showQuote by remember { mutableStateOf(false) }
    var quoteComplete by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Portrait entrance
        portraitAlpha.animateTo(1f, tween(600))
        portraitScale.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = 300f))
        delay(200)

        // Title slide up
        titleAlpha.animateTo(1f, tween(500))
        titleOffset.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
        delay(400)

        showQuote = true
    }

    LaunchedEffect(quoteComplete) {
        if (quoteComplete) {
            delay(400)
            showButtons = true
        }
    }

    val buttonsAlpha by animateFloatAsState(
        targetValue = if (showButtons) 1f else 0f,
        animationSpec = tween(500),
        label = "btns_alpha"
    )

    // Gold shimmer
    val shimmer = rememberInfiniteTransition(label = "gold_shimmer")
    val shimmerPos by shimmer.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_pos"
    )

    // Portrait glow pulse
    val glowPulse = rememberInfiniteTransition(label = "victory_glow")
    val glowAlpha by glowPulse.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Background — use back_2 for a different atmospheric feel on victory
        Image(
            painter = painterResource(R.drawable.ag3_back_2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color(0x88000000),
                            0.35f to Color(0xCC000000),
                            1f to Color(0xF8000000)
                        )
                    )
                )
        )

        SparksParticleSystem(accentColor = goldColor, particleCount = 35)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.5f))

            // Joker portrait with glow
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.graphicsLayer {
                    scaleX = portraitScale.value
                    scaleY = portraitScale.value
                    alpha = portraitAlpha.value
                }
            ) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    goldColor.copy(alpha = glowAlpha * 0.45f),
                                    goldColor.copy(alpha = glowAlpha * 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                // Portrait image
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            2.dp,
                            Brush.verticalGradient(
                                listOf(goldColor, goldDark.copy(alpha = 0.4f))
                            ),
                            RoundedCornerShape(20.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(R.drawable.joker_portrait),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay on portrait bottom
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xCC000000))
                                )
                            )
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // Title block
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    translationY = titleOffset.value
                    alpha = titleAlpha.value
                }
            ) {
                Text(
                    text = "YOU ESCAPED",
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 13.sp,
                    letterSpacing = 5.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    style = textShadow
                )
                Spacer(Modifier.height(6.dp))

                val shimmerBrush = Brush.linearGradient(
                    colors = listOf(goldDark, goldColor, Color.White, goldColor, goldDark),
                    start = Offset(shimmerPos * 1200f - 600f, 0f),
                    end = Offset(shimmerPos * 1200f, 0f)
                )
                Text(
                    text = "THE CARNIVAL",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        brush = shimmerBrush,
                        shadow = Shadow(
                            color = goldColor.copy(alpha = 0.6f),
                            offset = Offset(0f, 4f),
                            blurRadius = 16f
                        )
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Gold divider with dots
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, goldColor.copy(alpha = 0.6f))
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(goldColor)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(goldColor.copy(alpha = 0.6f), Color.Transparent)
                                )
                            )
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "ALL 7 CURSES BROKEN",
                    color = goldColor.copy(alpha = 0.6f),
                    fontSize = 9.sp,
                    letterSpacing = 2.5.sp,
                    fontWeight = FontWeight.Bold,
                    style = textShadow
                )
            }

            Spacer(Modifier.height(28.dp))

            // Joker farewell quote
            if (showQuote) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xBB0A0A0A))
                        .border(1.dp, goldColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(20.dp)
                ) {
                    JokerMonologue(
                        text = "Free. You are free. Leave my carnival. But know this — the Joker never forgets a face.",
                        style = androidx.compose.material3.LocalTextStyle.current.copy(
                            color = Color.White.copy(alpha = 0.88f),
                            fontSize = 15.sp,
                            lineHeight = 24.sp
                        ),
                        delayMs = 30L,
                        onComplete = { quoteComplete = true }
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Buttons
            Box(modifier = Modifier.graphicsLayer { alpha = buttonsAlpha }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PuzzleConfirmButton(
                        text = "VIEW THE LEDGER",
                        accentColor = goldColor,
                        onClick = { navController.navigate(Routes.LEADERBOARD) },
                        enabled = showButtons
                    )
                    Spacer(Modifier.height(10.dp))
                    PuzzleConfirmButton(
                        text = "PLAY AGAIN",
                        accentColor = goldDark,
                        onClick = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        enabled = showButtons
                    )
                }
            }
        }
    }
}
