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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import app.krafted.jokersescaperoom.R
import app.krafted.jokersescaperoom.Routes
import app.krafted.jokersescaperoom.data.model.CardState
import app.krafted.jokersescaperoom.ui.puzzle.SectionDivider
import app.krafted.jokersescaperoom.ui.puzzle.SparksParticleSystem
import app.krafted.jokersescaperoom.ui.puzzle.symbolDrawables
import app.krafted.jokersescaperoom.ui.puzzle.textShadow
import app.krafted.jokersescaperoom.ui.puzzle.toBackgroundDrawable
import app.krafted.jokersescaperoom.ui.puzzle.toComposeColor
import app.krafted.jokersescaperoom.ui.puzzle.toSymbolIndex
import app.krafted.jokersescaperoom.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

private val homeGold = Color(0xFFFFD700)
private val homeGoldDark = Color(0xFFB8860B)

private fun puzzleTypeLabel(type: String) = when (type) {
    "SYMBOL_SEQUENCE" -> "MEMORY"
    "CODE_CRACKER" -> "CIPHER"
    "PATTERN_MIRROR" -> "MIRROR"
    "TAP_ANSWER" -> "WORDS"
    "ODD_ONE_OUT" -> "SPOT IT"
    "COLOUR_SEQUENCE" -> "SIMON"
    "SPEED_ROUND" -> "SPEED"
    else -> ""
}

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val cardStates by viewModel.cardStates.collectAsState()
    val brokenCount by viewModel.brokenCount.collectAsState()
    val isVictory by viewModel.isVictory.collectAsState()

    LaunchedEffect(isVictory) {
        if (isVictory) {
            navController.navigate(Routes.VICTORY) { popUpTo(0) { inclusive = true } }
        }
    }

    var headerVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(80); headerVisible = true }
    val headerAlpha by animateFloatAsState(
        targetValue = if (headerVisible) 1f else 0f,
        animationSpec = tween(550),
        label = "header_alpha"
    )
    val headerOffset by animateFloatAsState(
        targetValue = if (headerVisible) 0f else -22f,
        animationSpec = tween(550, easing = FastOutSlowInEasing),
        label = "header_offset"
    )

    val shimmer = rememberInfiniteTransition(label = "title_shimmer")
    val shimmerPos by shimmer.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmer_pos"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.ag3_back_1),
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
                            0f to Color(0xBB000000),
                            0.45f to Color(0xDD000000),
                            1f to Color(0xF5000000)
                        )
                    )
                )
        )

        SparksParticleSystem(accentColor = homeGold, particleCount = 20)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(18.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = headerAlpha; translationY = headerOffset }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xFF1A1A1A),
                                            Color(0xFF080808)
                                        )
                                    )
                                )
                                .border(
                                    1.5.dp,
                                    Brush.verticalGradient(
                                        listOf(
                                            homeGold,
                                            homeGoldDark.copy(0.3f)
                                        )
                                    ),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_joker_icon),
                                contentDescription = null,
                                modifier = Modifier.size(38.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "THE JOKER'S",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 11.sp,
                                letterSpacing = 3.sp,
                                fontWeight = FontWeight.Medium
                            )
                            // Shimmer title
                            Text(
                                text = "CURSED CARDS",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                style = TextStyle(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            homeGoldDark,
                                            homeGold,
                                            Color.White,
                                            homeGold,
                                            homeGoldDark
                                        ),
                                        start = Offset(shimmerPos * 1000f - 500f, 0f),
                                        end = Offset(shimmerPos * 1000f, 0f)
                                    ),
                                    shadow = Shadow(
                                        color = homeGold.copy(0.4f),
                                        offset = Offset(0f, 3f),
                                        blurRadius = 12f
                                    )
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(homeGold.copy(alpha = 0.08f))
                            .border(
                                1.5.dp,
                                Brush.verticalGradient(
                                    listOf(
                                        homeGold.copy(0.5f),
                                        homeGold.copy(0.1f)
                                    )
                                ),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$brokenCount",
                                color = homeGold,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                style = textShadow
                            )
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height(1.dp)
                                    .background(homeGold.copy(0.3f))
                            )
                            Text(
                                text = "OF 7",
                                color = homeGold.copy(0.5f),
                                fontSize = 8.sp,
                                letterSpacing = 1.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(7) { i ->
                        val filled = i < brokenCount
                        val segAlpha by animateFloatAsState(
                            targetValue = if (filled) 1f else 0.15f,
                            animationSpec = tween(350, delayMillis = i * 70),
                            label = "seg_$i"
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(if (filled) 6.dp else 4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (filled)
                                        Brush.horizontalGradient(listOf(homeGoldDark, homeGold))
                                    else
                                        Brush.horizontalGradient(
                                            listOf(
                                                homeGold.copy(segAlpha),
                                                homeGold.copy(segAlpha)
                                            )
                                        )
                                )
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))
                SectionDivider(accentColor = homeGold)
                Spacer(Modifier.height(14.dp))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                val regularCards = cardStates.take(6)
                val finalCard = cardStates.getOrNull(6)

                itemsIndexed(regularCards) { index, card ->
                    CursedCardItem(
                        card = card,
                        cardNumber = index + 1,
                        enterDelay = 180 + index * 75,
                        isFinal = false,
                        isLocked = false,
                        onClick = { navController.navigate(Routes.cardIntro(card.cardId)) }
                    )
                }

                finalCard?.let { card ->
                    item(span = { GridItemSpan(2) }) {
                        CursedCardItem(
                            card = card,
                            cardNumber = 7,
                            enterDelay = 180 + 6 * 75,
                            isFinal = true,
                            isLocked = brokenCount < 6,
                            onClick = { navController.navigate(Routes.cardIntro(card.cardId)) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(listOf(homeGold.copy(0.12f), Color(0xFF050505)))
                    )
                    .border(
                        1.5.dp,
                        Brush.verticalGradient(listOf(homeGold.copy(0.5f), homeGold.copy(0.1f))),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { navController.navigate(Routes.LEADERBOARD) },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(Modifier
                        .size(4.dp)
                        .rotate(45f)
                        .background(homeGold.copy(0.7f)))
                    Text(
                        text = "THE JOKER'S LEDGER",
                        color = homeGold.copy(0.8f),
                        fontSize = 13.sp,
                        letterSpacing = 2.5.sp,
                        fontWeight = FontWeight.Bold,
                        style = textShadow
                    )
                    Box(Modifier
                        .size(4.dp)
                        .rotate(45f)
                        .background(homeGold.copy(0.7f)))
                }
            }

            Spacer(Modifier.height(14.dp))
        }
    }
}

@Composable
private fun CursedCardItem(
    card: CardState,
    cardNumber: Int,
    enterDelay: Int,
    isFinal: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(enterDelay.toLong())
        visible = true
    }
    val cardAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(420),
        label = "c_alpha_${card.cardId}"
    )
    val cardOffset by animateFloatAsState(
        targetValue = if (visible) 0f else 28f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 280f),
        label = "c_offset_${card.cardId}"
    )

    val accentColor = remember(card.accentColor) {
        try {
            card.accentColor.toComposeColor()
        } catch (e: Exception) {
            Color(0xFFB71C1C)
        }
    }
    val bgDrawable = remember(card.background) { card.background.toBackgroundDrawable() }
    val symbolIndex = remember(card.symbol) { card.symbol.toSymbolIndex() }

    val pulse = rememberInfiniteTransition(label = "pulse_${card.cardId}")
    val pulseAlpha by pulse.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "p_${card.cardId}"
    )

    val greyscaleFilter = remember {
        ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
    }
    val typeLabel = puzzleTypeLabel(card.puzzleType)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(if (isFinal) 2.7f else 0.72f)
            .graphicsLayer { alpha = cardAlpha; translationY = cardOffset }
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isFinal && !card.isCurseBroken && !isLocked) 2.5.dp else 2.dp,
                brush = when {
                    card.isCurseBroken -> Brush.verticalGradient(
                        listOf(
                            Color(0xFF2A2A2A),
                            Color(0xFF111111)
                        )
                    )

                    isLocked -> Brush.verticalGradient(listOf(Color(0xFF333333), Color(0xFF1A1A1A)))
                    isFinal -> Brush.verticalGradient(
                        listOf(
                            homeGold.copy(pulseAlpha),
                            homeGold.copy(0.1f)
                        )
                    )

                    else -> Brush.verticalGradient(
                        listOf(
                            accentColor.copy(pulseAlpha),
                            accentColor.copy(0.08f)
                        )
                    )
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = !card.isCurseBroken && !isLocked) { onClick() }
    ) {
        Image(
            painter = painterResource(bgDrawable),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            colorFilter = if (card.isCurseBroken || isLocked) greyscaleFilter else null
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        when {
                            card.isCurseBroken -> listOf(Color(0x88000000), Color(0xEE000000))
                            isLocked -> listOf(Color(0xAA000000), Color(0xF2000000))
                            else -> listOf(Color(0x22000000), Color(0xDD000000))
                        }
                    )
                )
        )

        Image(
            painter = painterResource(symbolDrawables[symbolIndex]),
            contentDescription = null,
            modifier = Modifier
                .size(if (isFinal) 100.dp else 80.dp)
                .align(Alignment.Center),
            alpha = when {
                card.isCurseBroken -> 0.06f
                isLocked -> 0.08f
                else -> 0.28f
            },
            contentScale = ContentScale.Fit
        )

        if (!card.isCurseBroken && !isLocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(
                                (if (isFinal) homeGold else accentColor).copy(alpha = pulseAlpha * 0.18f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Color.White.copy(if (card.isCurseBroken) 0.04f else 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(0xAA000000))
                        .border(
                            1.dp,
                            (if (isFinal) homeGold else accentColor).copy(0.45f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$cardNumber",
                        color = (if (isFinal) homeGold else accentColor).copy(if (card.isCurseBroken) 0.3f else 1f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                when {
                    card.isCurseBroken -> CardBadge(text = "✓  BROKEN", color = Color(0xFF4CAF50))
                    isLocked -> CardBadge(text = "🔒  LOCKED", color = Color(0xFF888888))
                    isFinal -> CardBadge(text = "FINAL", color = homeGold)
                    else -> CardBadge(text = typeLabel, color = accentColor)
                }
            }

            if (isFinal) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = card.title.uppercase(),
                            color = if (card.isCurseBroken) Color.White.copy(0.35f) else homeGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            style = textShadow
                        )
                        Text(
                            text = card.subtitle,
                            color = (if (card.isCurseBroken) Color.White else homeGold).copy(0.55f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                    }
                    if (!card.isCurseBroken && !isLocked) {
                        EnterPill(color = homeGold)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = card.title,
                            color = when {
                                card.isCurseBroken -> Color.White.copy(0.35f)
                                isLocked -> Color.White.copy(0.35f)
                                else -> Color.White
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.3.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = textShadow
                        )
                        Text(
                            text = card.subtitle,
                            color = if (card.isCurseBroken || isLocked) Color.White.copy(0.25f) else Color.White.copy(
                                0.75f
                            ),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.3.sp,
                            maxLines = 1
                        )
                    }
                    if (!card.isCurseBroken && !isLocked) {
                        EnterPill(color = accentColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun CardBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.25f))
            .border(1.dp, color.copy(alpha = 0.75f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun EnterPill(color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.5.dp, color.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "›",
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
        )
    }
}
