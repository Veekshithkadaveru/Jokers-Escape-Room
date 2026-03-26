package app.krafted.jokersescaperoom.ui.endgame

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersescaperoom.Routes
import app.krafted.jokersescaperoom.data.CurseRepository
import app.krafted.jokersescaperoom.data.db.AppDatabase
import app.krafted.jokersescaperoom.ui.components.JokerMonologue
import app.krafted.jokersescaperoom.ui.puzzle.PuzzleConfirmButton
import app.krafted.jokersescaperoom.ui.puzzle.SparksParticleSystem
import app.krafted.jokersescaperoom.ui.puzzle.symbolDrawables
import app.krafted.jokersescaperoom.ui.puzzle.textShadow
import app.krafted.jokersescaperoom.ui.puzzle.toBackgroundDrawable
import app.krafted.jokersescaperoom.ui.puzzle.toComposeColor
import app.krafted.jokersescaperoom.ui.puzzle.toSymbolIndex
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CurseBreakScreen(
    cardId: String,
    accentColor: Color,
    navController: NavController
) {
    val context = LocalContext.current
    val card = remember(cardId) { CurseRepository(context).getCard(cardId) }
    val dao = remember { AppDatabase.getInstance(context).cardProgressDao() }
    val scope = rememberCoroutineScope()

    val symbolIndex = card?.symbol?.toSymbolIndex() ?: 0
    val bgDrawable = remember(card?.background) { card?.background.toBackgroundDrawable() }

    // Animation states
    val shakeOffset = remember { Animatable(0f) }
    val flashAlpha = remember { Animatable(0f) }
    val bannerOffset = remember { Animatable(-160f) }
    val bannerAlpha = remember { Animatable(0f) }
    val cardScale = remember { Animatable(0.7f) }
    val cardAlpha = remember { Animatable(0f) }

    var showQuote by remember { mutableStateOf(false) }
    var quoteComplete by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    // Entrance + sequence
    LaunchedEffect(Unit) {
        // Card entrance
        launch {
            cardAlpha.animateTo(1f, tween(400))
        }
        cardScale.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 400f))
        delay(200)

        // 1. Shake
        shakeOffset.animateTo(18f, spring(dampingRatio = 0.1f, stiffness = 900f))
        shakeOffset.animateTo(-14f, tween(60))
        shakeOffset.animateTo(10f, tween(55))
        shakeOffset.animateTo(-7f, tween(50))
        shakeOffset.animateTo(0f, spring(dampingRatio = 0.5f))
        delay(100)

        // 2. Flash
        flashAlpha.animateTo(0.85f, tween(120))
        flashAlpha.animateTo(0f, tween(350))
        delay(50)

        // 3. Banner
        launch { bannerAlpha.animateTo(1f, tween(300)) }
        bannerOffset.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = 500f))
        delay(500)

        // 4. Quote
        showQuote = true
    }

    LaunchedEffect(quoteComplete) {
        if (quoteComplete) {
            delay(300)
            showButton = true
        }
    }

    val buttonAlpha by animateFloatAsState(
        targetValue = if (showButton) 1f else 0f,
        animationSpec = tween(400),
        label = "btn_alpha"
    )

    fun navigateNext() {
        scope.launch {
            val broken = dao.getAll().count { it.isCurseBroken }
            if (broken >= 7) {
                navController.navigate(Routes.VICTORY) {
                    popUpTo(0) { inclusive = true }
                }
            } else {
                navController.navigate(Routes.HOME) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(bgDrawable),
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
                            0f to Color(0xAA000000),
                            0.4f to Color(0xCC000000),
                            1f to Color(0xF5000000)
                        )
                    )
                )
        )

        SparksParticleSystem(accentColor = accentColor)

        // Full-screen flash
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(accentColor.copy(alpha = flashAlpha.value))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))

            // Symbol card with shake + scale entrance
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.graphicsLayer {
                    translationX = shakeOffset.value
                    scaleX = cardScale.value
                    scaleY = cardScale.value
                    alpha = cardAlpha.value
                }
            ) {
                // Outer radial glow
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    accentColor.copy(alpha = 0.5f),
                                    accentColor.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                // Symbol container
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1A1A1A), Color(0xFF060606))
                            )
                        )
                        .border(
                            2.dp,
                            Brush.verticalGradient(
                                listOf(accentColor, accentColor.copy(alpha = 0.2f))
                            ),
                            RoundedCornerShape(22.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(symbolDrawables[symbolIndex]),
                        contentDescription = null,
                        modifier = Modifier.size(70.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(Modifier.height(36.dp))

            // "CURSE BROKEN" banner
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    translationY = bannerOffset.value
                    alpha = bannerAlpha.value
                }
            ) {
                Text(
                    text = "CURSE BROKEN",
                    color = accentColor,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center,
                    style = textShadow
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = card?.title?.uppercase() ?: "",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    style = textShadow
                )
                Spacer(Modifier.height(16.dp))
                // Accent divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(1.5.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    accentColor,
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            Spacer(Modifier.height(28.dp))

            // Joker quote
            if (showQuote) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xBB0D0D0D))
                        .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(20.dp)
                ) {
                    JokerMonologue(
                        text = card?.successQuote ?: "",
                        style = androidx.compose.material3.LocalTextStyle.current.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 15.sp,
                            lineHeight = 24.sp
                        ),
                        onComplete = { quoteComplete = true }
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Return button
            Box(modifier = Modifier.graphicsLayer { alpha = buttonAlpha }) {
                PuzzleConfirmButton(
                    text = "RETURN TO THE LAIR",
                    accentColor = accentColor,
                    onClick = { navigateNext() },
                    enabled = showButton
                )
            }
        }
    }
}
