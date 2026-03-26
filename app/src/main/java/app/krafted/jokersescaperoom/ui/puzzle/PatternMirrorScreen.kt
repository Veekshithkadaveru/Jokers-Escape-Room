package app.krafted.jokersescaperoom.ui.puzzle

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
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
fun PatternMirrorScreen(
    cardId: String,
    accentColor: Color,
    navController: NavController,
    viewModel: PuzzleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val card by viewModel.cardInfo.collectAsState()
    val scope = rememberCoroutineScope()

    var wrongTileIndices by remember { mutableStateOf(emptySet<Int>()) }

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
    LaunchedEffect(uiState.gridPattern) { wrongTileIndices = emptySet() }

    fun handleConfirm() {
        if (uiState.playerGrid == uiState.gridPattern) {
            viewModel.submitMirror()
            return
        }
        val wrong = uiState.playerGrid
            .zip(uiState.gridPattern)
            .mapIndexedNotNull { i, (p, g) -> if (p != g) i else null }
            .toSet()
        wrongTileIndices = wrong
        scope.launch {
            delay(700)
            wrongTileIndices = emptySet()
            viewModel.submitMirror()
        }
    }

    val gridSize = uiState.gridSize.takeIf { it > 0 } ?: 3

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

            Text(
                text = "MIRROR THE JOKER'S PATTERN",
                color = accentColor.copy(alpha = 0.75f),
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Medium,
                style = textShadow
            )

            Spacer(Modifier.height(16.dp))
            SectionDivider(accentColor = accentColor)
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GridPanel(
                    label = "JOKER'S PATTERN",
                    accentColor = accentColor,
                    isActive = false,
                    modifier = Modifier.weight(1f)
                ) {
                    PatternGrid(
                        gridSize = gridSize,
                        tiles = uiState.gridPattern,
                        accentColor = accentColor,
                        isInteractive = false,
                        wrongTileIndices = emptySet(),
                        onTileTap = {}
                    )
                }

                GridPanel(
                    label = "YOUR MIRROR",
                    accentColor = accentColor,
                    isActive = true,
                    modifier = Modifier.weight(1f)
                ) {
                    PatternGrid(
                        gridSize = gridSize,
                        tiles = uiState.playerGrid,
                        accentColor = accentColor,
                        isInteractive = true,
                        wrongTileIndices = wrongTileIndices,
                        onTileTap = { viewModel.togglePlayerTile(it) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            PuzzleConfirmButton(
                text = "CONFIRM MIRROR",
                accentColor = accentColor,
                onClick = { handleConfirm() }
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

@Composable
private fun GridPanel(
    label: String,
    accentColor: Color,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xD90D0D0D), Color(0xF2050505))
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        accentColor.copy(alpha = if (isActive) 0.5f else 0.15f),
                        accentColor.copy(alpha = 0.05f)
                    )
                ),
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = accentColor.copy(alpha = if (isActive) 0.9f else 0.55f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            style = textShadow
        )
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(accentColor.copy(alpha = 0.1f))
        )
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun PatternGrid(
    gridSize: Int,
    tiles: List<Boolean>,
    accentColor: Color,
    isInteractive: Boolean,
    wrongTileIndices: Set<Int>,
    onTileTap: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    var tappedIndex by remember { mutableStateOf(-1) }

    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        (0 until gridSize).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                (0 until gridSize).forEach { col ->
                    val index = row * gridSize + col
                    val isLit = tiles.getOrElse(index) { false }
                    val isWrong = index in wrongTileIndices
                    val isTapped = index == tappedIndex

                    val bgColor by animateColorAsState(
                        targetValue = when {
                            !isInteractive && isLit -> accentColor.copy(alpha = 0.88f)
                            !isInteractive -> Color(0x33FFFFFF)
                            isWrong -> Color(0xFFF44336).copy(alpha = 0.7f)
                            isLit -> accentColor.copy(alpha = 0.85f)
                            else -> Color(0x22FFFFFF)
                        },
                        animationSpec = tween(220),
                        label = "tile_bg_$index"
                    )

                    val tileScale by animateFloatAsState(
                        targetValue = if (isTapped) 0.80f else 1f,
                        animationSpec = spring(dampingRatio = 0.3f, stiffness = 900f),
                        label = "tile_scale_$index"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .scale(tileScale)
                            .clip(RoundedCornerShape(6.dp))
                            .background(bgColor)
                            .then(
                                if (isLit || isWrong) {
                                    Modifier.border(
                                        1.5.dp,
                                        if (isWrong) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.35f),
                                        RoundedCornerShape(6.dp)
                                    )
                                } else {
                                    Modifier.border(
                                        1.dp,
                                        accentColor.copy(alpha = 0.12f),
                                        RoundedCornerShape(6.dp)
                                    )
                                }
                            )
                            .then(
                                if (isInteractive) Modifier.clickable {
                                    tappedIndex = index
                                    scope.launch {
                                        delay(180)
                                        tappedIndex = -1
                                    }
                                    onTileTap(index)
                                } else Modifier
                            )
                    )
                }
            }
        }
    }
}
