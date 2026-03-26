package app.krafted.jokersescaperoom.data.model

data class CursesResponse(
    val cards: List<CursedCard>
)

data class CursedCard(
    val id: String,
    val title: String,
    val subtitle: String,
    val symbol: String,
    val background: String,
    val accentColor: String,
    val puzzleType: String,
    val intro: String,
    val successQuote: String,
    val failQuote: String,
    val resetQuote: String,
    // Cards 1, 3, 5, 6, 7 — difficulty tiers
    val difficulty: List<DifficultyConfig>? = null,
    // Card 2 — riddles per level
    val riddles: List<RiddleConfig>? = null,
    // Card 4 — tap-answer configs
    val tapAnswer: List<TapAnswerConfig>? = null,
    // Card 6 — colour hex list
    val colours: List<String>? = null
)

data class DifficultyConfig(
    val level: String,
    // Card 1
    val sequenceLength: Int? = null,
    val displayTimeMs: Int? = null,
    // Card 3
    val gridSize: Int? = null,
    val litTiles: Int? = null,
    // Cards 5 + 7
    val rounds: Int? = null,
    val symbolCount: Int? = null,
    val differenceType: String? = null,
    val timerMs: Int? = null,
    // Card 6
    val startLength: Int? = null,
    val maxLength: Int? = null,
    val flashTimeMs: Int? = null
)

data class RiddleConfig(
    val level: String,
    val riddle: String,
    val hint: String,
    val answer: String
)

data class TapAnswerConfig(
    val clue: String,
    val answerSymbols: List<Int>,
    val symbolLetters: List<String>
)
