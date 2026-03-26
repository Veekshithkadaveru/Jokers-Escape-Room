package app.krafted.jokersescaperoom.data.model

data class PuzzleUiState(
    val cardId: String = "",
    val puzzleType: PuzzleType = PuzzleType.SYMBOL_SEQUENCE,
    val attemptsRemaining: Int = 3,
    val phase: PuzzlePhase = PuzzlePhase.SHOWING,
    val isComplete: Boolean = false,
    val isFailed: Boolean = false,
    val isReset: Boolean = false,

    // Card 1 + Card 6 — sequence flash + player replay
    val sequence: List<Int> = emptyList(),
    val playerInput: List<Int> = emptyList(),
    val currentShowIndex: Int = -1,

    // Card 3 — pattern mirror
    val gridPattern: List<Boolean> = emptyList(),
    val playerGrid: List<Boolean> = emptyList(),
    val gridSize: Int = 3,

    // Card 2 — code cracker
    val dialValues: List<Int> = listOf(0, 0, 0, 0),

    // Card 4 — tap the answer
    val symbolLetters: List<String> = emptyList(),
    val answerSymbols: List<Int> = emptyList(),
    val playerTaps: List<Int> = emptyList(),
    val tapClue: String = "",

    // Cards 5 + 7 — odd one out / speed round
    val currentRound: Int = 1,
    val totalRounds: Int = 5,
    val oddIndex: Int = -1,
    val symbolCount: Int = 6,
    val differenceType: String = "COLOUR",

    // Card 6 — colour sequence
    val colourSequence: List<Int> = emptyList(),
    val playerColourInput: List<Int> = emptyList(),
    val activeColourIndex: Int? = null,

    // Card 7 — speed round timer (0.0 = expired, 1.0 = full)
    val timeRemaining: Float = 1f
)
