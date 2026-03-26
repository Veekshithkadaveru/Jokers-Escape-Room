package app.krafted.jokersescaperoom.ui.puzzle

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
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

@Composable
fun TapAnswerScreen(
    cardId: String,
    accentColor: Color,
    navController: NavController,
    viewModel: PuzzleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val card by viewModel.cardInfo.collectAsState()

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

            Spacer(Modifier.height(16.dp))

            // Clue box
            if (uiState.tapClue.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(listOf(Color(0xCC0D0D0D), Color(0xEE050505)))
                        )
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                listOf(
                                    accentColor.copy(alpha = 0.4f),
                                    Color.Transparent,
                                    accentColor.copy(alpha = 0.4f)
                                )
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
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = "CLUE",
                                color = accentColor.copy(alpha = 0.6f),
                                fontSize = 9.sp,
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Bold,
                                style = textShadow
                            )
                            Spacer(Modifier.height(5.dp))
                            Text(
                                text = uiState.tapClue,
                                color = JokerWhite.copy(alpha = 0.95f),
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                style = textShadow
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            SectionDivider(accentColor = accentColor)
            Spacer(Modifier.height(12.dp))

            Text(
                text = "TAP THE SYMBOLS TO SPELL THE ANSWER",
                color = accentColor.copy(alpha = 0.7f),
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium,
                style = textShadow
            )

            Spacer(Modifier.weight(1f))

            // 2+3+2 diamond grid
            val rows = listOf(listOf(0, 1), listOf(2, 3, 4), listOf(5, 6))
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                rows.forEach { rowIndices ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowIndices.forEach { idx ->
                            val letter = uiState.symbolLetters.getOrElse(idx) { "" }
                            val tapCount = uiState.playerTaps.count { it == idx }
                            val neededCount = uiState.answerSymbols.count { it == idx }
                            LetterSymbolCard(
                                symbolIdx = idx,
                                letter = letter,
                                accentColor = accentColor,
                                isUsedUp = neededCount > 0 && tapCount >= neededCount,
                                onTap = { viewModel.tapAnswerSymbol(idx) }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Answer slots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                repeat(uiState.answerSymbols.size) { i ->
                    val tappedLetter = if (i < uiState.playerTaps.size) {
                        uiState.symbolLetters.getOrElse(uiState.playerTaps[i]) { "_" }
                    } else null
                    AnswerSlot(letter = tappedLetter, accentColor = accentColor)
                }
            }

            Spacer(Modifier.height(10.dp))

            // Clear / backspace
            if (uiState.playerTaps.isNotEmpty()) {
                TextButton(onClick = { viewModel.clearTapAnswer() }) {
                    Text(
                        text = "⌫  CLEAR",
                        color = accentColor.copy(alpha = 0.45f),
                        fontSize = 11.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Spacer(Modifier.height(36.dp))
            }

            Spacer(Modifier.height(8.dp))
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

@Composable
private fun LetterSymbolCard(
    symbolIdx: Int,
    letter: String,
    accentColor: Color,
    isUsedUp: Boolean,
    onTap: () -> Unit
) {
    val cardAlpha by animateFloatAsState(
        targetValue = if (isUsedUp) 0.28f else 1f,
        animationSpec = tween(220),
        label = "card_alpha_$symbolIdx"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.graphicsLayer { alpha = cardAlpha }
    ) {
        Box(
            modifier = Modifier
                .size(78.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1C1C1C), Color(0xFF060606))
                    )
                )
                .border(
                    1.5.dp,
                    Brush.verticalGradient(
                        listOf(
                            accentColor.copy(alpha = if (isUsedUp) 0.15f else 0.65f),
                            accentColor.copy(alpha = 0.05f)
                        )
                    ),
                    RoundedCornerShape(14.dp)
                )
                .clickable(enabled = !isUsedUp) { onTap() },
            contentAlignment = Alignment.Center
        ) {
            // Inner top highlight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.5.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.10f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Image(
                painter = painterResource(symbolDrawables[symbolIdx]),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(bottom = 14.dp),
                contentScale = ContentScale.Fit,
                alpha = 0.85f
            )

            // Letter badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xDD000000))
                        )
                    )
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    color = accentColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    style = textShadow
                )
            }
        }
    }
}

@Composable
private fun AnswerSlot(
    letter: String?,
    accentColor: Color
) {
    val filled = letter != null
    Box(
        modifier = Modifier
            .size(width = 50.dp, height = 56.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (filled) accentColor.copy(alpha = 0.12f) else Color(0xFF111111)
            )
            .border(
                1.5.dp,
                if (filled) accentColor.copy(alpha = 0.65f) else Color(0xFF2A2A2A),
                RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (filled) {
            AnimatedContent(
                targetState = letter,
                transitionSpec = {
                    (slideInVertically { it } + fadeIn(tween(140))) togetherWith
                        (slideOutVertically { -it } + fadeOut(tween(100)))
                },
                label = "slot_letter"
            ) { l ->
                Text(
                    text = l ?: "",
                    color = accentColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    style = textShadow
                )
            }
        } else {
            Text(
                text = "_",
                color = Color(0xFF333333),
                fontSize = 20.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}
