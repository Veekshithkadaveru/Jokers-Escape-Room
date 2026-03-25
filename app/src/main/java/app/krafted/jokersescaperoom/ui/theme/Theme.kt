package app.krafted.jokersescaperoom.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val JokerDarkColorScheme = darkColorScheme(
    primary = JokerRed,
    onPrimary = JokerWhite,
    secondary = JokerPurple,
    onSecondary = JokerWhite,
    tertiary = JokerCrimson,
    onTertiary = JokerWhite,
    background = JokerBlack,
    onBackground = JokerWhite,
    surface = JokerDarkSurface,
    onSurface = JokerWhite,
    surfaceVariant = JokerCardSurface,
    onSurfaceVariant = JokerGrey,
    error = JokerRed,
    onError = JokerWhite
)

@Composable
fun JokersEscapeRoomTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = JokerDarkColorScheme,
        typography = Typography,
        content = content
    )
}
