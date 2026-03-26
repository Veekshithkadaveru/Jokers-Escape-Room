package app.krafted.jokersescaperoom.ui.puzzle

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import app.krafted.jokersescaperoom.Routes
import app.krafted.jokersescaperoom.data.model.PuzzlePhase
import app.krafted.jokersescaperoom.ui.components.FailResetOverlay
import app.krafted.jokersescaperoom.ui.theme.JokerWhite
import app.krafted.jokersescaperoom.viewmodel.PuzzleViewModel
import kotlinx.coroutines.launch

@Composable
fun CodeCrackerScreen(
    cardId: String,
    accentColor: Color,
    navController: NavController,
    viewModel: PuzzleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val card by viewModel.cardInfo.collectAsState()
    val scope = rememberCoroutineScope()
    val shakeOffset = remember { Animatable(0f) }

    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentVisible = true }
    val enterAlpha by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = tween(550, delayMillis = 100),
        label = "enter_alpha"
    )
    val enterOffset by animateFloatAsState(
        targetValue = if (contentVisible) 0f else 28f,
        animationSpec = tween(550, delayMillis = 100, easing = FastOutSlowInEasing),
        label = "enter_offset"
    )

    val symbolIndex = card?.symbol?.toSymbolIndex() ?: 0
    val bgDrawable = remember(card?.background) { card?.background.toBackgroundDrawable() }
    val riddle = card?.riddles?.firstOrNull()

    LaunchedEffect(cardId) { viewModel.loadPuzzle(cardId) }
    LaunchedEffect(uiState.phase) {
        if (uiState.phase == PuzzlePhase.SUCCESS) {
            navController.navigate(Routes.curseBreak(cardId)) {
                popUpTo(Routes.puzzle(cardId)) { inclusive = true }
            }
        }
    }

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
                            0f to Color(0x77000000),
                            0.3f to Color(0xCC000000),
                            1f to Color(0xF0000000)
                        )
                    )
                )
        )

        SparksParticleSystem(accentColor = accentColor)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .graphicsLayer {
                    alpha = enterAlpha
                    translationY = enterOffset
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PuzzleHeader(
                title = card?.title ?: "",
                subtitle = card?.subtitle ?: "",
                symbolRes = symbolDrawables[symbolIndex],
                accentColor = accentColor,
                attemptsRemaining = uiState.attemptsRemaining
            )

            Spacer(Modifier.height(20.dp))

            riddle?.let { r ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xCC0D0D0D), Color(0xEE050505))
                            )
                        )
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                listOf(accentColor.copy(alpha = 0.4f), Color.Transparent, accentColor.copy(alpha = 0.4f))
                            ),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .fillMaxHeight()
                                .background(accentColor)
                        )
                        Text(
                            text = r.riddle,
                            color = JokerWhite.copy(alpha = 0.95f),
                            fontSize = 15.sp,
                            lineHeight = 24.sp,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
                            style = textShadow
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            SectionDivider(accentColor = accentColor)
            Spacer(Modifier.height(20.dp))

            Text(
                text = "ENTER THE CODE",
                color = accentColor.copy(alpha = 0.75f),
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium,
                style = textShadow
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { translationX = shakeOffset.value },
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.dialValues.forEachIndexed { index, value ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF151515), Color(0xFF080808))
                                )
                            )
                            .border(1.dp, accentColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextButton(
                            onClick = { viewModel.incrementDial(index) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("▲", color = accentColor.copy(alpha = 0.8f), fontSize = 16.sp)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(accentColor.copy(alpha = 0.12f))
                        )

                        AnimatedContent(
                            targetState = value,
                            transitionSpec = {
                                val isUp = targetState > initialState || (initialState == 9 && targetState == 0)
                                if (isUp) {
                                    (slideInVertically { -it } + fadeIn(tween(110))) togetherWith
                                        (slideOutVertically { it } + fadeOut(tween(90)))
                                } else {
                                    (slideInVertically { it } + fadeIn(tween(110))) togetherWith
                                        (slideOutVertically { -it } + fadeOut(tween(90)))
                                }
                            },
                            label = "dial_$index"
                        ) { displayValue ->
                            Text(
                                text = displayValue.toString(),
                                color = accentColor,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(accentColor.copy(alpha = 0.12f))
                        )

                        TextButton(
                            onClick = { viewModel.decrementDial(index) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("▼", color = accentColor.copy(alpha = 0.8f), fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            PuzzleConfirmButton(
                text = "CONFIRM CODE",
                accentColor = accentColor,
                onClick = {
                    val isCorrect = uiState.dialValues.joinToString("") { it.toString() } == riddle?.answer
                    if (!isCorrect) {
                        scope.launch {
                            shakeOffset.animateTo(14f, tween(55))
                            shakeOffset.animateTo(-14f, tween(55))
                            shakeOffset.animateTo(9f, tween(45))
                            shakeOffset.animateTo(-9f, tween(45))
                            shakeOffset.animateTo(0f, tween(35))
                        }
                    }
                    viewModel.submitCode()
                }
            )

            Spacer(Modifier.weight(1f))
        }

        if (uiState.isFailed || uiState.isReset) {
            FailResetOverlay(
                isReset = uiState.isReset,
                failQuote = card?.failQuote ?: "",
                resetQuote = card?.resetQuote ?: "",
                attemptsRemaining = uiState.attemptsRemaining,
                accentColor = accentColor,
                onRetry = { viewModel.retryPuzzle() },
                onReturnHome = {
                    viewModel.resetCard()
                    navController.navigate(Routes.HOME) { popUpTo(0) { inclusive = true } }
                }
            )
        }
    }
}
