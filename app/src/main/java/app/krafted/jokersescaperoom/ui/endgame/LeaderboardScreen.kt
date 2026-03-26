package app.krafted.jokersescaperoom.ui.endgame

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import app.krafted.jokersescaperoom.R
import app.krafted.jokersescaperoom.ui.puzzle.SparksParticleSystem
import app.krafted.jokersescaperoom.ui.puzzle.SectionDivider
import app.krafted.jokersescaperoom.ui.puzzle.symbolDrawables
import app.krafted.jokersescaperoom.ui.puzzle.textShadow
import app.krafted.jokersescaperoom.ui.puzzle.toComposeColor
import app.krafted.jokersescaperoom.viewmodel.LeaderboardViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

private val goldColor = Color(0xFFFFD700)

@Composable
fun LeaderboardScreen(
    navController: NavController,
    viewModel: LeaderboardViewModel = viewModel()
) {
    val entries by viewModel.entries.collectAsState()

    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentVisible = true }
    val enterAlpha by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = tween(500, delayMillis = 80),
        label = "enter_alpha"
    )
    val enterOffset by animateFloatAsState(
        targetValue = if (contentVisible) 0f else 24f,
        animationSpec = tween(500, delayMillis = 80, easing = FastOutSlowInEasing),
        label = "enter_offset"
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
                            0f to Color(0x88000000),
                            0.3f to Color(0xCC000000),
                            1f to Color(0xF5000000)
                        )
                    )
                )
        )

        SparksParticleSystem(accentColor = goldColor, particleCount = 18)

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
            // Header
            Text(
                text = "THE JOKER'S LEDGER",
                color = goldColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center,
                style = textShadow
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "RECORDS OF THE ESCAPED",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 9.sp,
                letterSpacing = 2.5.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(18.dp))
            SectionDivider(accentColor = goldColor)
            Spacer(Modifier.height(18.dp))

            // Column headers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#",
                    color = goldColor.copy(alpha = 0.5f),
                    fontSize = 9.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(28.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "CARD",
                    color = goldColor.copy(alpha = 0.5f),
                    fontSize = 9.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "BEST TIME",
                    color = goldColor.copy(alpha = 0.5f),
                    fontSize = 9.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(80.dp),
                    textAlign = TextAlign.End
                )
            }

            Spacer(Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(entries) { index, entry ->
                    LeaderboardRow(
                        rank = index + 1,
                        entry = entry,
                        enterDelay = index * 60
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(
    rank: Int,
    entry: app.krafted.jokersescaperoom.viewmodel.LeaderboardEntry,
    enterDelay: Int
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(enterDelay.toLong())
        visible = true
    }
    val rowAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(350),
        label = "row_alpha_$rank"
    )
    val rowOffset by animateFloatAsState(
        targetValue = if (visible) 0f else 16f,
        animationSpec = tween(350, easing = FastOutSlowInEasing),
        label = "row_offset_$rank"
    )

    val accentColor = remember(entry.accentColor) {
        try { entry.accentColor.toComposeColor() } catch (e: Exception) { goldColor }
    }
    val isBroken = entry.bestTimeMs != null
    val rowBg = if (isBroken) accentColor.copy(alpha = 0.07f) else Color(0xFF0D0D0D)
    val borderColor = if (isBroken) accentColor.copy(alpha = 0.3f) else Color(0xFF1E1E1E)
    val symbolIdx = entry.symbolName.let {
        it.lastOrNull { c -> c.isDigit() }?.digitToInt()?.minus(1)?.coerceIn(0, 6) ?: 0
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = rowAlpha
                translationY = rowOffset
            }
            .clip(RoundedCornerShape(12.dp))
            .background(rowBg)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Rank
            Text(
                text = if (isBroken) "$rank" else "—",
                color = if (isBroken) goldColor else Color(0xFF444444),
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.width(28.dp),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.width(10.dp))

            // Symbol icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF111111))
                    .border(1.dp, accentColor.copy(alpha = if (isBroken) 0.35f else 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(symbolDrawables[symbolIdx]),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    alpha = if (isBroken) 1f else 0.3f,
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.width(12.dp))

            // Card info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.title.uppercase(),
                    color = if (isBroken) Color.White.copy(alpha = 0.9f) else Color(0xFF444444),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    style = if (isBroken) textShadow else androidx.compose.ui.text.TextStyle.Default
                )
                if (entry.brokenAt != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = formatDate(entry.brokenAt),
                        color = accentColor.copy(alpha = 0.55f),
                        fontSize = 9.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Best time
            if (isBroken && entry.bestTimeMs != null) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatTime(entry.bestTimeMs),
                        color = goldColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        style = textShadow
                    )
                    Text(
                        text = "BEST",
                        color = goldColor.copy(alpha = 0.4f),
                        fontSize = 8.sp,
                        letterSpacing = 1.sp
                    )
                }
            } else {
                Text(
                    text = "LOCKED",
                    color = Color(0xFF333333),
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    val tenths = (ms % 1000) / 100
    return if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}.${tenths}s"
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("d MMM, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
