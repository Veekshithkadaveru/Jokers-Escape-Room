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
    // Card 1, 3, 5, 6 — difficulty tiers
    val difficulty: List<DifficultyConfig>? = null,
    // Card 2 — riddles per level
    val riddles: List<RiddleConfig>? = null,
    // Card 4 — word pool
    val words: List<WordConfig>? = null,
    // Card 6 — colour hex list
    val colours: List<String>? = null,
    // Card 7 — 3-step config
    val steps: List<FinalBossStep>? = null
)

data class DifficultyConfig(
    val level: String,
    // Card 1
    val sequenceLength: Int? = null,
    val displayTimeMs: Int? = null,
    // Card 3
    val gridSize: Int? = null,
    val litTiles: Int? = null,
    // Card 5
    val rounds: Int? = null,
    val symbolCount: Int? = null,
    val differenceType: String? = null,
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

data class WordConfig(
    val level: String,
    val scrambled: String,
    val answer: String,
    val clue: String
)

data class FinalBossStep(
    val step: Int,
    val puzzleType: String,
    val sequenceLength: Int? = null,
    val gridSize: Int? = null,
    val litTiles: Int? = null,
    val startLength: Int? = null
)
