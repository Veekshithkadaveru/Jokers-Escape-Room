package app.krafted.jokersescaperoom.ui.puzzle

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
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
import app.krafted.jokersescaperoom.viewmodel.PuzzleViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SpeedRoundScreen(
    cardId: String,
    accentColor: Color,
    navController: NavController,
    viewModel: PuzzleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val card by viewModel.cardInfo.collectAsState()
    val scope = rememberCoroutineScope()

    var correctFlashIndex by remember { mutableStateOf(-1) }
    var wrongFlashIndex by remember { mutableStateOf(-1) }

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
    val timerDurationMs = remember(card?.difficulty) {
        card?.difficulty?.firstOrNull()?.timerMs?.toLong() ?: 2000L
    }

    LaunchedEffect(cardId) { viewModel.loadPuzzle(cardId) }
    LaunchedEffect(uiState.phase) {
        if (uiState.phase == PuzzlePhase.SUCCESS) {
            navController.navigate(Routes.curseBreak(cardId)) {
                popUpTo(Routes.puzzle(cardId)) { inclusive = true }
            }
        }
    }
    LaunchedEffect(uiState.currentRound) {
        correctFlashIndex = -1
        wrongFlashIndex = -1
    }

    // Countdown timer
    LaunchedEffect(uiState.currentRound, uiState.phase) {
        if (uiState.phase != PuzzlePhase.INPUT) return@LaunchedEffect
        var startFrame = -1L
        while (true) {
            val frameTime = withFrameMillis { it }
            if (startFrame < 0L) startFrame = frameTime
            val elapsed = frameTime - startFrame
            val remaining = (1f - elapsed.toFloat() / timerDurationMs).coerceIn(0f, 1f)
            viewModel.updateTimeRemaining(remaining)
            if (remaining <= 0f) {
                viewModel.onTimerExpired()
                break
            }
        }
    }

    fun handleTap(index: Int) {
        if (uiState.phase != PuzzlePhase.INPUT) return
        if (index == uiState.oddIndex) {
            correctFlashIndex = index
            scope.launch { delay(300); correctFlashIndex = -1 }
        } else {
            wrongFlashIndex = index
            scope.launch { delay(300); wrongFlashIndex = -1 }
        }
        viewModel.tapSpeedRoundSymbol(index)
    }

    val cols = if (uiState.symbolCount == 8) 4 else 3

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

            RoundProgressBar(
                current = uiState.currentRound,
                total = uiState.totalRounds,
                accentColor = accentColor
            )

            Spacer(Modifier.height(14.dp))
            SectionDivider(accentColor = accentColor)
            Spacer(Modifier.height(12.dp))

            // Countdown timer bar
            TimerBar(
                timeRemaining = uiState.timeRemaining,
                accentColor = accentColor
            )

            Spacer(Modifier.height(14.dp))

            Text(
                text = "FIND THE ODD ONE OUT",
                color = accentColor.copy(alpha = 0.7f),
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium,
                style = textShadow
            )

            Spacer(Modifier.weight(1f))

            // Symbol grid
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                val rows = (0 until uiState.symbolCount).chunked(cols)
                rows.forEach { rowIndices ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowIndices.forEach { cellIdx ->
                            SpeedSymbolCard(
                                cellIndex = cellIdx,
                                isOdd = cellIdx == uiState.oddIndex,
                                differenceType = uiState.differenceType,
                                accentColor = accentColor,
                                symbolResId = symbolDrawables[symbolIndex],
                                isCorrectFlash = cellIdx == correctFlashIndex,
                                isWrongFlash = cellIdx == wrongFlashIndex,
                                modifier = Modifier.weight(1f),
                                onTap = { handleTap(cellIdx) }
                            )
                        }
                        repeat(cols - rowIndices.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(16.dp))
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
private fun TimerBar(
    timeRemaining: Float,
    accentColor: Color
) {
    val timerColor by animateColorAsState(
        targetValue = when {
            timeRemaining > 0.5f -> accentColor
            timeRemaining > 0.25f -> Color(0xFFFF8F00)
            else -> Color(0xFFF44336)
        },
        animationSpec = tween(200),
        label = "timer_color"
    )
    val widthFraction by animateFloatAsState(
        targetValue = timeRemaining,
        animationSpec = tween(80, easing = LinearEasing),
        label = "timer_width"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1A1A1A))
                    .border(1.dp, timerColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(widthFraction.coerceIn(0f, 1f))
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    timerColor.copy(alpha = 0.9f),
                                    timerColor
                                )
                            )
                        )
                )
                // Timer bar inner glow
                Box(
                    modifier = Modifier
                        .fillMaxWidth(widthFraction.coerceIn(0f, 1f))
                        .height(3.dp)
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                )
            }
            // Time pulse indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(timerColor.copy(alpha = if (timeRemaining > 0f) 1f else 0f))
            )
        }
    }
}

@Composable
private fun SpeedSymbolCard(
    cellIndex: Int,
    isOdd: Boolean,
    differenceType: String,
    accentColor: Color,
    symbolResId: Int,
    isCorrectFlash: Boolean,
    isWrongFlash: Boolean,
    modifier: Modifier = Modifier,
    onTap: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            isCorrectFlash -> Color(0xFF4CAF50).copy(alpha = 0.3f)
            isWrongFlash -> Color(0xFFF44336).copy(alpha = 0.25f)
            else -> Color(0xFF141414)
        },
        animationSpec = tween(150),
        label = "speed_bg_$cellIndex"
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            isCorrectFlash -> Color(0xFF4CAF50)
            isWrongFlash -> Color(0xFFF44336)
            else -> accentColor.copy(alpha = 0.18f)
        },
        animationSpec = tween(150),
        label = "speed_border_$cellIndex"
    )
    val scaleVal by animateFloatAsState(
        targetValue = if (isCorrectFlash) 1.12f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 500f),
        label = "speed_scale_$cellIndex"
    )

    val imageRotation = if (isOdd && differenceType == "ROTATION") 180f else 0f
    val imageScale = when {
        isOdd && differenceType == "SUBTLE_SIZE" -> 0.60f
        else -> 0.75f
    }
    val overlayColor = when {
        differenceType == "COLOUR" && !isOdd -> accentColor.copy(alpha = 0.25f)
        differenceType == "COLOUR" && isOdd -> Color.White.copy(alpha = 0.20f)
        else -> Color.Transparent
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .scale(scaleVal)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onTap() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, Color.White.copy(alpha = 0.07f), Color.Transparent)
                    )
                )
        )

        Image(
            painter = painterResource(symbolResId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(imageScale)
                .rotate(imageRotation),
            contentScale = ContentScale.Fit,
            alpha = 0.88f
        )

        if (differenceType == "COLOUR") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(overlayColor)
            )
        }
    }
}
