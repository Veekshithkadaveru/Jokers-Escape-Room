package app.krafted.jokersescaperoom.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FailResetOverlay(
    isReset: Boolean,
    failQuote: String,
    resetQuote: String,
    attemptsRemaining: Int,
    accentColor: Color = Color(0xFFB71C1C),
    onRetry: () -> Unit,
    onReturnHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            JokerMonologue(
                text = if (isReset) resetQuote else failQuote,
                style = androidx.compose.material3.LocalTextStyle.current.copy(
                    color = Color(0xFFEEEEEE),
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isReset) {
                Text(
                    text = "Card reset. All progress lost.",
                    color = Color(0xFFEF5350),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onReturnHome,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A)),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "RETURN TO CARNIVAL",
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            } else {
                AttemptDiamonds(
                    attemptsRemaining = attemptsRemaining,
                    accentColor = accentColor
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A)),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "TRY AGAIN",
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}
