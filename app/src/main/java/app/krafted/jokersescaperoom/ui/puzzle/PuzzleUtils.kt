package app.krafted.jokersescaperoom.ui.puzzle

import androidx.compose.ui.graphics.Color
import app.krafted.jokersescaperoom.R

val backgroundDrawables = mapOf(
    "ag3_back_1" to R.drawable.ag3_back_1,
    "ag3_back_2" to R.drawable.ag3_back_2,
    "ag3_back_3" to R.drawable.ag3_back_3,
    "ag3_back_4" to R.drawable.ag3_back_4,
    "ag3_back_5" to R.drawable.ag3_back_5
)

fun String?.toBackgroundDrawable(): Int =
    backgroundDrawables[this] ?: R.drawable.ag3_back_1

val symbolDrawables = listOf(
    R.drawable.ag3_sym_1,
    R.drawable.ag3_sym_2,
    R.drawable.ag3_sym_3,
    R.drawable.ag3_sym_4,
    R.drawable.ag3_sym_5,
    R.drawable.ag3_sym_6,
    R.drawable.ag3_sym_7
)

fun String.toSymbolIndex(): Int =
    lastOrNull { it.isDigit() }?.digitToInt()?.minus(1)?.coerceIn(0, 6) ?: 0

fun String.toComposeColor(): Color {
    val argb = android.graphics.Color.parseColor(this)
    return Color(
        red = android.graphics.Color.red(argb) / 255f,
        green = android.graphics.Color.green(argb) / 255f,
        blue = android.graphics.Color.blue(argb) / 255f
    )
}
