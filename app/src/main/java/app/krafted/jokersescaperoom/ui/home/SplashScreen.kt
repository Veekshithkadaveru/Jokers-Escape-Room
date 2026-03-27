package app.krafted.jokersescaperoom.ui.home

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import app.krafted.jokersescaperoom.ui.puzzle.SparksParticleSystem
import kotlinx.coroutines.delay

private val splashGold = Color(0xFFFFD700)
private val splashGoldDark = Color(0xFFB8860B)

@Composable
fun SplashScreen(navController: NavController) {

    // Step 1 — bg + portrait fade in
    var bgVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        bgVisible = true
    }
    val bgAlpha by animateFloatAsState(
        targetValue = if (bgVisible) 1f else 0f,
        animationSpec = tween(800),
        label = "bg_alpha"
    )

    // Step 2 — icon springs in
    var iconReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(400)
        iconReady = true
    }
    val iconScale by animateFloatAsState(
        targetValue = if (iconReady) 1f else 0.4f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 260f),
        label = "icon_scale"
    )
    val iconAlpha by animateFloatAsState(
        targetValue = if (iconReady) 1f else 0f,
        animationSpec = tween(500),
        label = "icon_alpha"
    )

    // Step 3 — text slides up
    var textReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(900)
        textReady = true
    }
    val textAlpha by animateFloatAsState(
        targetValue = if (textReady) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "text_alpha"
    )
    val textOffset by animateFloatAsState(
        targetValue = if (textReady) 0f else 20f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "text_offset"
    )

    // Shimmer
    val shimmer = rememberInfiniteTransition(label = "splash_shimmer")
    val shimmerPos by shimmer.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2800, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmer_pos"
    )

    // Glow pulse on the icon border
    val glow = rememberInfiniteTransition(label = "splash_glow")
    val glowAlpha by glow.animateFloat(
        initialValue = 0.35f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    // Auto-navigate to HOME
    LaunchedEffect(Unit) {
        delay(3000)
        navController.navigate(Routes.HOME) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Background image
        Image(
            painter = painterResource(R.drawable.ag3_back_1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().graphicsLayer { alpha = bgAlpha },
            contentScale = ContentScale.Crop
        )

        // Heavy dark overlay so everything reads clearly
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color(0xCC000000),
                            0.5f to Color(0xE5000000),
                            1f to Color(0xF8000000)
                        )
                    )
                )
        )

        // Joker portrait — large faint background watermark
        Image(
            painter = painterResource(R.drawable.joker_portrait),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = bgAlpha * 0.09f },
            contentScale = ContentScale.Crop
        )

        SparksParticleSystem(accentColor = splashGold, particleCount = 22)

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))

            // Icon with glow ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                    alpha = iconAlpha
                }
            ) {
                // Outer radial glow
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    splashGold.copy(alpha = glowAlpha * 0.22f),
                                    splashGold.copy(alpha = glowAlpha * 0.06f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                // Pulsing ring
                Box(
                    modifier = Modifier
                        .size((130 + glowAlpha * 14).dp)
                        .border(
                            1.dp,
                            splashGold.copy(alpha = glowAlpha * 0.45f),
                            RoundedCornerShape(28.dp)
                        )
                )
                // Icon container
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1C1C1C), Color(0xFF080808))
                            )
                        )
                        .border(
                            2.dp,
                            Brush.verticalGradient(
                                listOf(splashGold, splashGold.copy(alpha = 0.2f))
                            ),
                            RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Inner top highlight
                    Box(
                        modifier = Modifier
                            .size(110.dp, 1.5.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.White.copy(0.18f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_joker_icon),
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(Modifier.height(36.dp))

            // Title + subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    alpha = textAlpha
                    translationY = textOffset
                }
            ) {
                Text(
                    text = "JOKER'S",
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 13.sp,
                    letterSpacing = 5.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "ESCAPE ROOM",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(splashGoldDark, splashGold, Color.White, splashGold, splashGoldDark),
                            start = Offset(shimmerPos * 1200f - 600f, 0f),
                            end = Offset(shimmerPos * 1200f, 0f)
                        ),
                        shadow = Shadow(
                            color = splashGold.copy(0.5f),
                            offset = Offset(0f, 4f),
                            blurRadius = 18f
                        )
                    )
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "BREAK THE CURSE",
                    color = splashGold.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.weight(1.4f))

            // Bottom tagline
            Text(
                text = "IF YOU DARE",
                color = Color.White.copy(alpha = 0.15f),
                fontSize = 9.sp,
                letterSpacing = 3.sp,
                modifier = Modifier.graphicsLayer { alpha = textAlpha }
            )
            Spacer(Modifier.height(48.dp))
        }
    }
}
