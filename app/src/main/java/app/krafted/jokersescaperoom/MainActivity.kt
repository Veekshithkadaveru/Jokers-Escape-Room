package app.krafted.jokersescaperoom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.krafted.jokersescaperoom.data.CurseRepository
import app.krafted.jokersescaperoom.ui.puzzle.CodeCrackerScreen
import app.krafted.jokersescaperoom.ui.puzzle.PatternMirrorScreen
import app.krafted.jokersescaperoom.ui.puzzle.SequenceScreen
import app.krafted.jokersescaperoom.ui.puzzle.toComposeColor
import app.krafted.jokersescaperoom.ui.theme.JokersEscapeRoomTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JokersEscapeRoomTheme {
                JokerNavHost()
            }
        }
    }
}

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val CARD_INTRO = "card_intro/{cardId}"
    const val PUZZLE = "puzzle/{cardId}"
    const val CURSE_BREAK = "curse_break/{cardId}"
    const val VICTORY = "victory"
    const val LEADERBOARD = "leaderboard"

    fun cardIntro(cardId: String) = "card_intro/$cardId"
    fun puzzle(cardId: String) = "puzzle/$cardId"
    fun curseBreak(cardId: String) = "curse_break/$cardId"
}

@Composable
fun JokerNavHost() {
    val navController = rememberNavController()
    val darkFadeIn = fadeIn(androidx.compose.animation.core.tween(350))
    val darkFadeOut = fadeOut(androidx.compose.animation.core.tween(350))

    NavHost(
        navController = navController,
        startDestination = Routes.puzzle("DARK_JESTER"),
        enterTransition = { darkFadeIn },
        exitTransition = { darkFadeOut },
        popEnterTransition = { darkFadeIn },
        popExitTransition = { darkFadeOut }
    ) {
        composable(Routes.SPLASH) {
            PlaceholderScreen("Splash — Coming Phase B1")
        }
        composable(Routes.HOME) {
            PlaceholderScreen("Home — Coming Phase B2")
        }
        composable(
            route = Routes.CARD_INTRO,
            arguments = listOf(navArgument("cardId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId") ?: ""
            PlaceholderScreen("Card Intro: $cardId — Coming Phase B3")
        }
        composable(
            route = Routes.PUZZLE,
            arguments = listOf(navArgument("cardId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId") ?: ""
            val context = LocalContext.current
            val card = remember(cardId) { CurseRepository(context).getCard(cardId) }
            val puzzleType = card?.puzzleType ?: ""
            val accentColor = card?.accentColor?.toComposeColor() ?: Color(0xFFB71C1C)
            when (puzzleType) {
                "SYMBOL_SEQUENCE" -> SequenceScreen(cardId, accentColor, navController)
                "CODE_CRACKER" -> CodeCrackerScreen(cardId, accentColor, navController)
                "PATTERN_MIRROR" -> PatternMirrorScreen(cardId, accentColor, navController)
                else -> PlaceholderScreen("Coming: $puzzleType")
            }
        }
        composable(
            route = Routes.CURSE_BREAK,
            arguments = listOf(navArgument("cardId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId") ?: ""
            PlaceholderScreen("Curse Break: $cardId — Coming Phase E1")
        }
        composable(Routes.VICTORY) {
            PlaceholderScreen("Victory — Coming Phase E2")
        }
        composable(Routes.LEADERBOARD) {
            PlaceholderScreen("Leaderboard — Coming Phase E3")
        }
    }
}

@Composable
private fun PlaceholderScreen(label: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color(0xFFB71C1C),
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
