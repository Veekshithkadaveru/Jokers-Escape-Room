package app.krafted.jokersescaperoom.data.model

data class CardState(
    val cardId: String,
    val title: String,
    val subtitle: String,
    val symbol: String,
    val background: String,
    val accentColor: String,
    val puzzleType: String,
    val intro: String,
    val isCurseBroken: Boolean = false,
    val bestTimeMs: Long? = null
)
