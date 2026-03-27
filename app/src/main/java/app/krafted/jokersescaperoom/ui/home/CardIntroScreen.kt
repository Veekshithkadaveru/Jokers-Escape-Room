package app.krafted.jokersescaperoom.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.navigation.NavController
import app.krafted.jokersescaperoom.Routes
import app.krafted.jokersescaperoom.data.CurseRepository
import app.krafted.jokersescaperoom.ui.components.JokerMonologue
import app.krafted.jokersescaperoom.ui.puzzle.PuzzleConfirmButton
import app.krafted.jokersescaperoom.ui.puzzle.SectionDivider
import app.krafted.jokersescaperoom.ui.puzzle.SparksParticleSystem
import app.krafted.jokersescaperoom.ui.puzzle.symbolDrawables
import app.krafted.jokersescaperoom.ui.puzzle.textShadow
import app.krafted.jokersescaperoom.ui.puzzle.toBackgroundDrawable
import app.krafted.jokersescaperoom.ui.puzzle.toComposeColor
import app.krafted.jokersescaperoom.ui.puzzle.toSymbolIndex
import kotlinx.coroutines.delay

@Composable
fun CardIntroScreen(
    cardId: String,
    navController: NavController
) {
    val context = LocalContext.current
    val card = remember(cardId) { CurseRepository(context).getCard(cardId) }

    val accentColor = remember(card?.accentColor) {
        try {
            card?.accentColor?.toComposeColor() ?: Color(0xFFB71C1C)
        } catch (e: Exception) {
            Color(0xFFB71C1C)
        }
    }
    val bgDrawable = remember(card?.background) { card?.background.toBackgroundDrawable() }
    val symbolIndex = remember(card?.symbol) { card?.symbol?.toSymbolIndex() ?: 0 }

    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(120)
        contentVisible = true
    }
    val enterAlpha by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "enter_alpha"
    )
    val enterOffset by animateFloatAsState(
        targetValue = if (contentVisible) 0f else 32f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
        label = "enter_offset"
    )

    var symbolReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(250)
        symbolReady = true
    }
    val symbolScale by animateFloatAsState(
        targetValue = if (symbolReady) 1f else 0.6f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 350f),
        label = "symbol_scale"
    )
    val symbolAlpha by animateFloatAsState(
        targetValue = if (symbolReady) 1f else 0f,
        animationSpec = tween(400),
        label = "symbol_alpha"
    )

    var monologueVisible by remember { mutableStateOf(false) }
    var monologueComplete by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(700)
        monologueVisible = true
    }

    val glow = rememberInfiniteTransition(label = "intro_glow")
    val glowAlpha by glow.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
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
                            0f to Color(0x55000000),
                            0.3f to Color(0xBB000000),
                            1f to Color(0xF5000000)
                        )
                    )
                )
        )

        SparksParticleSystem(accentColor = accentColor, particleCount = 20)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .graphicsLayer {
                    alpha = enterAlpha
                    translationY = enterOffset
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.4f))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.graphicsLayer {
                    scaleX = symbolScale
                    scaleY = symbolScale
                    alpha = symbolAlpha
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    accentColor.copy(alpha = glowAlpha * 0.5f),
                                    accentColor.copy(alpha = glowAlpha * 0.12f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .size((100 + glowAlpha * 10).dp)
                        .border(
                            1.dp,
                            accentColor.copy(alpha = glowAlpha * 0.5f),
                            RoundedCornerShape(20.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(20.dp))
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
                            RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.5.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.White.copy(0.15f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Image(
                        painter = painterResource(symbolDrawables[symbolIndex]),
                        contentDescription = null,
                        modifier = Modifier.size(62.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = card?.title?.uppercase() ?: "",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                style = textShadow
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = card?.subtitle?.uppercase() ?: "",
                color = accentColor.copy(alpha = 0.7f),
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                style = textShadow
            )

            Spacer(Modifier.height(20.dp))
            SectionDivider(accentColor = accentColor)
            Spacer(Modifier.height(20.dp))

            if (monologueVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xBB0D0D0D))
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                listOf(accentColor.copy(0.35f), accentColor.copy(0.05f))
                            ),
                            RoundedCornerShape(14.dp)
                        )
                ) {
                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {

                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .fillMaxHeight()
                                .background(accentColor.copy(alpha = 0.6f))
                        )
                        Column(
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = 16.dp
                            )
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(accentColor)
                                )
                                Text(
                                    text = "  THE JOKER SPEAKS",
                                    color = accentColor.copy(alpha = 0.55f),
                                    fontSize = 8.sp,
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            JokerMonologue(
                                text = card?.intro ?: "",
                                style = androidx.compose.material3.LocalTextStyle.current.copy(
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 14.sp,
                                    lineHeight = 23.sp
                                ),
                                delayMs = 22L,
                                onComplete = { monologueComplete = true }
                            )
                        }
                    }
                }
            } else {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xBB0D0D0D))
                        .border(1.dp, accentColor.copy(0.12f), RoundedCornerShape(14.dp))
                )
            }

            Spacer(Modifier.weight(1f))

            PuzzleConfirmButton(
                text = "ATTEMPT THE CURSE",
                accentColor = accentColor,
                enabled = monologueComplete,
                onClick = {
                    navController.navigate(Routes.puzzle(cardId)) {
                        popUpTo(Routes.cardIntro(cardId)) { inclusive = true }
                    }
                }
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}
