package app.krafted.jokersescaperoom.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun JokerMonologue(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current.copy(color = Color(0xFFEEEEEE), fontSize = 16.sp),
    delayMs: Long = 25L,
    onComplete: () -> Unit = {}
) {
    var displayed by remember(text) { mutableStateOf("") }

    LaunchedEffect(text) {
        displayed = ""
        text.forEach { char ->
            displayed += char
            delay(delayMs)
        }
        onComplete()
    }

    Text(
        text = displayed,
        style = style,
        modifier = modifier
    )
}
