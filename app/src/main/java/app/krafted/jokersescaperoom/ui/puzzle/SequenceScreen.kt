package app.krafted.jokersescaperoom.ui.puzzle

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
fun SequenceScreen(
    cardId: String,
    accentColor: Color,
    navController: NavController,
    viewModel: PuzzleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val card by viewModel.cardInfo.collectAsState()
    val scope = rememberCoroutineScope()

    var tapFlashIndex by remember { mutableStateOf(-1) }
    var tapFlashCorrect by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }
    var cellsVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        contentVisible = true
        delay(350)
        cellsVisible = true
    }

    val enterAlpha by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = tween(500, delayMillis = 80),
        label = "enter_alpha"
    )
    val enterOffset by animateFloatAsState(
        targetValue = if (contentVisible) 0f else 28f,
        animationSpec = tween(500, delayMillis = 80, easing = FastOutSlowInEasing),
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

    fun handleSymbolTap(index: Int) {
        if (uiState.phase == PuzzlePhase.INPUT) {
            val expected = uiState.sequence.getOrNull(uiState.playerInput.size)
            tapFlashIndex = index
            tapFlashCorrect = (index == expected)
            scope.launch {
                delay(280)
                tapFlashIndex = -1
            }
            viewModel.onSymbolTapped(index)
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
                            0f to Color(0x66000000),
                            0.25f to Color(0xBB000000),
                            1f to Color(0xF2000000)
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

            AnimatedContent(
                targetState = if (uiState.phase == PuzzlePhase.SHOWING) "MEMORISE THE SEQUENCE" else "REPEAT THE SEQUENCE",
                transitionSpec = {
                    (fadeIn(tween(300)) + slideInVertically { it / 2 }) togetherWith
                        (fadeOut(tween(200)) + slideOutVertically { -it / 2 })
                },
                label = "phase_label"
            ) { label ->
                val labelPulse = rememberInfiniteTransition(label = "label_pulse")
                val labelAlpha by labelPulse.animateFloat(
                    initialValue = 0.6f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "label_alpha"
                )
                Text(
                    text = label,
                    color = accentColor.copy(alpha = labelAlpha),
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold,
                    style = textShadow
                )
            }

            Spacer(Modifier.height(16.dp))
            SectionDivider(accentColor = accentColor)
            Spacer(Modifier.weight(1f))

            // 2+3+2 diamond layout
            val rows = listOf(listOf(0, 1), listOf(2, 3, 4), listOf(5, 6))
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                rows.forEach { rowIndices ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowIndices.forEachIndexed { posInRow, symbolIdx ->
                            SymbolCell(
                                index = symbolIdx,
                                accentColor = accentColor,
                                currentShowIndex = uiState.currentShowIndex,
                                tapFlashIndex = tapFlashIndex,
                                tapFlashCorrect = tapFlashCorrect,
                                phase = uiState.phase,
                                visible = cellsVisible,
                                enterDelay = symbolIdx * 75,
                                onTap = { handleSymbolTap(symbolIdx) }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Bottom status area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.phase == PuzzlePhase.INPUT && uiState.sequence.isNotEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${uiState.playerInput.size} / ${uiState.sequence.size}",
                            color = accentColor.copy(alpha = 0.5f),
                            fontSize = 9.sp,
                            letterSpacing = 1.5.sp,
                            style = textShadow
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            uiState.sequence.forEachIndexed { i, _ ->
                                val filled = i < uiState.playerInput.size
                                val isCurrent = i == uiState.playerInput.size
                                val segAlpha by animateFloatAsState(
                                    targetValue = when {
                                        filled -> 1f
                                        isCurrent -> 0.55f
                                        else -> 0.18f
                                    },
                                    animationSpec = tween(180),
                                    label = "seg_$i"
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(accentColor.copy(alpha = segAlpha))
                                )
                            }
                        }
                    }
                } else if (uiState.phase == PuzzlePhase.SHOWING) {
                    WatchingIndicator(accentColor = accentColor)
                }
            }

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
private fun WatchingIndicator(accentColor: Color) {
    val pulse = rememberInfiniteTransition(label = "watching")
    val alpha by pulse.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "watch_alpha"
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.size(5.dp).background(accentColor.copy(alpha = alpha), CircleShape))
        Text(
            text = "WATCHING",
            color = accentColor.copy(alpha = alpha),
            fontSize = 10.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Medium,
            style = textShadow
        )
        Box(modifier = Modifier.size(5.dp).background(accentColor.copy(alpha = alpha), CircleShape))
    }
}

@Composable
private fun SymbolCell(
    index: Int,
    accentColor: Color,
    currentShowIndex: Int,
    tapFlashIndex: Int,
    tapFlashCorrect: Boolean,
    phase: PuzzlePhase,
    visible: Boolean,
    enterDelay: Int,
    onTap: () -> Unit
) {
    val isShowingActive = currentShowIndex == index && phase == PuzzlePhase.SHOWING
    val isFlashing = tapFlashIndex == index
    val isCorrect = isFlashing && tapFlashCorrect
    val isWrong = isFlashing && !tapFlashCorrect

    // Entrance animation
    val cellAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(380, delayMillis = enterDelay, easing = FastOutSlowInEasing),
        label = "cell_alpha_$index"
    )
    val cellOffsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 18f,
        animationSpec = tween(380, delayMillis = enterDelay, easing = FastOutSlowInEasing),
        label = "cell_offset_$index"
    )

    // Glow pulse for active symbol
    val infinite = rememberInfiniteTransition(label = "glow_$index")
    val glowPulse by infinite.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse_$index"
    )

    val borderTopColor by animateColorAsState(
        targetValue = when {
            isShowingActive -> accentColor
            isCorrect -> Color(0xFF4CAF50)
            isWrong -> Color(0xFFF44336)
            else -> Color(0xFF3A3A3A)
        },
        animationSpec = tween(150),
        label = "border_top_$index"
    )

    val bgTopColor by animateColorAsState(
        targetValue = when {
            isShowingActive -> accentColor.copy(alpha = 0.28f)
            isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.18f)
            isWrong -> Color(0xFFF44336).copy(alpha = 0.18f)
            else -> Color(0xFF1C1C1C)
        },
        animationSpec = tween(180),
        label = "bg_top_$index"
    )

    val scaleValue by animateFloatAsState(
        targetValue = if (isShowingActive || isCorrect) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 400f),
        label = "scale_$index"
    )

    val imageAlpha by animateFloatAsState(
        targetValue = when {
            isShowingActive -> 1f
            phase == PuzzlePhase.INPUT -> 0.9f
            else -> 0.6f
        },
        animationSpec = tween(200),
        label = "img_alpha_$index"
    )

    val elevationDp = if (isShowingActive) 20.dp else if (isCorrect || isWrong) 12.dp else 4.dp
    val shadowColor = when {
        isShowingActive -> accentColor.copy(alpha = 0.6f)
        isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.5f)
        isWrong -> Color(0xFFF44336).copy(alpha = 0.5f)
        else -> Color.Black.copy(alpha = 0.5f)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.graphicsLayer {
            alpha = cellAlpha
            translationY = cellOffsetY
        }
    ) {
        // Outer ambient glow when active
        if (isShowingActive) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                accentColor.copy(alpha = glowPulse * 0.55f),
                                accentColor.copy(alpha = glowPulse * 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )
            // Animated outer ring
            Box(
                modifier = Modifier
                    .size((98 + glowPulse * 8).dp)
                    .border(
                        1.5.dp,
                        accentColor.copy(alpha = glowPulse * 0.7f),
                        RoundedCornerShape(18.dp)
                    )
            )
        }

        // Card with shadow
        Box(
            modifier = Modifier
                .size(96.dp)
                .graphicsLayer {
                    scaleX = scaleValue
                    scaleY = scaleValue
                    shadowElevation = elevationDp.toPx()
                    shape = RoundedCornerShape(16.dp)
                    clip = true
                    ambientShadowColor = shadowColor
                    spotShadowColor = shadowColor
                }
                .background(
                    Brush.verticalGradient(
                        listOf(bgTopColor, Color(0xFF060606))
                    )
                )
                .border(
                    1.5.dp,
                    Brush.verticalGradient(
                        listOf(
                            borderTopColor.copy(alpha = 0.9f),
                            borderTopColor.copy(alpha = 0.15f)
                        )
                    ),
                    RoundedCornerShape(16.dp)
                )
                .clickable { onTap() },
            contentAlignment = Alignment.Center
        ) {
            // Inner top highlight (light catch)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.5.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = if (isShowingActive) 0.25f else 0.07f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Image(
                painter = painterResource(symbolDrawables[index]),
                contentDescription = null,
                modifier = Modifier.size(60.dp).padding(4.dp),
                contentScale = ContentScale.Fit,
                alpha = imageAlpha
            )
        }
    }
}
