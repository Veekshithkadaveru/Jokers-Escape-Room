package app.krafted.jokersescaperoom.ui.puzzle

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
fun ColourSequenceScreen(
    cardId: String,
    accentColor: Color,
    navController: NavController,
    viewModel: PuzzleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val card by viewModel.cardInfo.collectAsState()
    val scope = rememberCoroutineScope()

    var tapFlashIndex by remember { mutableStateOf(-1) }

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
    val colours = remember(card?.colours) {
        card?.colours?.map { it.toComposeColor() } ?: emptyList()
    }

    LaunchedEffect(cardId) { viewModel.loadPuzzle(cardId) }
    LaunchedEffect(uiState.phase) {
        if (uiState.phase == PuzzlePhase.SUCCESS) {
            navController.navigate(Routes.curseBreak(cardId)) {
                popUpTo(Routes.puzzle(cardId)) { inclusive = true }
            }
        }
    }

    val isShowing = uiState.phase == PuzzlePhase.SHOWING
    val progressFilled = uiState.playerColourInput.size
    val progressTotal = uiState.colourSequence.size

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

            // Sequence level indicator
            SequenceLevelBadge(
                currentLength = uiState.colourSequence.size,
                accentColor = accentColor
            )

            Spacer(Modifier.height(14.dp))
            SectionDivider(accentColor = accentColor)
            Spacer(Modifier.height(12.dp))

            // Phase label
            val phasePulse = rememberInfiniteTransition(label = "phase_pulse")
            val phaseAlpha by phasePulse.animateFloat(
                initialValue = 0.6f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1100, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "phase_alpha"
            )
            Text(
                text = if (isShowing) "WATCH THE SEQUENCE" else "REPEAT THE SEQUENCE",
                color = accentColor.copy(alpha = phaseAlpha),
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                style = textShadow
            )

            Spacer(Modifier.weight(1f))

            // Colour pads — 4 + 3 layout
            if (colours.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val row1 = (0 until minOf(4, colours.size))
                    val row2 = (4 until colours.size)

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        row1.forEach { idx ->
                            val padColor = colours[idx]
                            ColourPad(
                                colour = padColor,
                                isActive = uiState.activeColourIndex == idx,
                                isTapFlash = tapFlashIndex == idx,
                                isInteractive = !isShowing,
                                modifier = Modifier.weight(1f),
                                onTap = {
                                    tapFlashIndex = idx
                                    scope.launch { delay(200); tapFlashIndex = -1 }
                                    viewModel.tapColour(idx)
                                }
                            )
                        }
                    }

                    if (row2.any()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                        ) {
                            row2.forEach { idx ->
                                val padColor = colours[idx]
                                ColourPad(
                                    colour = padColor,
                                    isActive = uiState.activeColourIndex == idx,
                                    isTapFlash = tapFlashIndex == idx,
                                    isInteractive = !isShowing,
                                    modifier = Modifier.weight(1f),
                                    onTap = {
                                        tapFlashIndex = idx
                                        scope.launch { delay(200); tapFlashIndex = -1 }
                                        viewModel.tapColour(idx)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Input progress bar (only during INPUT phase)
            if (!isShowing && progressTotal > 0) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "$progressFilled / $progressTotal",
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
                        repeat(progressTotal) { i ->
                            val filled = i < progressFilled
                            val isCurrent = i == progressFilled
                            val segAlpha by animateFloatAsState(
                                targetValue = when {
                                    filled -> 1f
                                    isCurrent -> 0.5f
                                    else -> 0.18f
                                },
                                animationSpec = tween(160),
                                label = "colour_seg_$i"
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
private fun SequenceLevelBadge(
    currentLength: Int,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(accentColor.copy(alpha = 0.1f))
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
            Text(
                text = "LEVEL $currentLength",
                color = accentColor,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                style = textShadow
            )
        }
    }
}

@Composable
private fun ColourPad(
    colour: Color,
    isActive: Boolean,
    isTapFlash: Boolean,
    isInteractive: Boolean,
    modifier: Modifier = Modifier,
    onTap: () -> Unit
) {
    val infinite = rememberInfiniteTransition(label = "pad_glow")
    val glowPulse by infinite.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pad_pulse"
    )

    val scaleVal by animateFloatAsState(
        targetValue = when {
            isActive -> 1.12f
            isTapFlash -> 0.92f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = 0.38f, stiffness = 600f),
        label = "pad_scale"
    )

    val padAlpha by animateFloatAsState(
        targetValue = when {
            isActive -> 1f
            isTapFlash -> 1f
            isInteractive -> 0.8f
            else -> 0.45f
        },
        animationSpec = tween(200),
        label = "pad_alpha"
    )

    Box(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow when active
        if (isActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize(1.35f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(
                                colour.copy(alpha = glowPulse * 0.5f),
                                colour.copy(alpha = glowPulse * 0.1f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(scaleVal)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            colour.copy(alpha = padAlpha),
                            colour.copy(alpha = padAlpha * 0.6f)
                        )
                    )
                )
                .border(
                    width = if (isActive) 2.dp else 1.dp,
                    color = if (isActive) Color.White.copy(alpha = glowPulse * 0.6f)
                    else colour.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(14.dp)
                )
                .then(
                    if (isInteractive) Modifier.clickable { onTap() } else Modifier
                )
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
                                Color.White.copy(alpha = if (isActive) 0.4f else 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}
