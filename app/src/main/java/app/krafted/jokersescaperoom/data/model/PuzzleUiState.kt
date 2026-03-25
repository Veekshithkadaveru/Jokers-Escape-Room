package app.krafted.jokersescaperoom.data.model

data class PuzzleUiState(
    val cardId: String = "",
    val puzzleType: PuzzleType = PuzzleType.SYMBOL_SEQUENCE,
    val attemptsRemaining: Int = 3,
    val phase: PuzzlePhase = PuzzlePhase.SHOWING,
    val isComplete: Boolean = false,
    val isFailed: Boolean = false,
    val isReset: Boolean = false,

    // Card 1 + Card 7 Step 1 — symbol sequence
    val sequence: List<Int> = emptyList(),
    val playerInput: List<Int> = emptyList(),
    val currentShowIndex: Int = 0,

    // Card 3 + Card 7 Step 2 — pattern mirror
    val gridPattern: List<Boolean> = emptyList(),
    val playerGrid: List<Boolean> = emptyList(),
    val gridSize: Int = 3,

    // Card 2 — code cracker
    val dialValues: List<Int> = listOf(0, 0, 0, 0),

    // Card 4 — word unscramble
    val scrambledLetters: List<Char> = emptyList(),
    val playerLetters: List<Char?> = emptyList(),

    // Card 5 — odd one out
    val currentRound: Int = 1,
    val totalRounds: Int = 5,
    val oddSymbolIndex: Int = -1,

    // Card 6 + Card 7 Step 3 — colour sequence
    val colourSequence: List<Int> = emptyList(),
    val playerColourInput: List<Int> = emptyList(),
    val activeColourIndex: Int? = null,

    // Card 7 — final boss
    val finalBossStep: Int = 1
)
