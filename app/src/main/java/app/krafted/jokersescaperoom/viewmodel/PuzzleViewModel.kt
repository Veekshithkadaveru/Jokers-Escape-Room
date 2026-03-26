package app.krafted.jokersescaperoom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.jokersescaperoom.data.CurseRepository
import app.krafted.jokersescaperoom.data.db.AppDatabase
import app.krafted.jokersescaperoom.data.model.CursedCard
import app.krafted.jokersescaperoom.data.model.PuzzlePhase
import app.krafted.jokersescaperoom.data.model.PuzzleType
import app.krafted.jokersescaperoom.data.model.PuzzleUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PuzzleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CurseRepository(application)
    private val dao = AppDatabase.getInstance(application).cardProgressDao()

    private val _uiState = MutableStateFlow(PuzzleUiState())
    val uiState: StateFlow<PuzzleUiState> = _uiState.asStateFlow()

    private var currentCard: CursedCard? = null
    private var startTimeMs: Long = 0L
    private var showingJob: Job? = null

    // ─── Load ────────────────────────────────────────────────────────────────

    fun loadPuzzle(cardId: String, preserveAttempts: Int = 3) {
        val card = repository.getCard(cardId) ?: return
        currentCard = card
        startTimeMs = System.currentTimeMillis()

        val puzzleType = PuzzleType.valueOf(card.puzzleType)
        _uiState.value = buildInitialState(card, puzzleType)
            .copy(attemptsRemaining = preserveAttempts)

        when (puzzleType) {
            PuzzleType.SYMBOL_SEQUENCE -> startSequenceShow()
            PuzzleType.COLOUR_SEQUENCE -> startColourSequenceShow()
            else -> { /* INPUT phase from start */ }
        }
    }

    // ─── Retry / Reset ───────────────────────────────────────────────────────

    fun retryPuzzle() {
        val current = _uiState.value
        loadPuzzle(current.cardId, preserveAttempts = current.attemptsRemaining)
    }

    fun resetCard() {
        val cardId = _uiState.value.cardId
        viewModelScope.launch {
            dao.resetCard(cardId)
        }
        // Navigation (popBackStack) handled by the screen
    }

    // ─── Initial State ───────────────────────────────────────────────────────

    private fun buildInitialState(card: CursedCard, puzzleType: PuzzleType): PuzzleUiState {
        return when (puzzleType) {

            PuzzleType.SYMBOL_SEQUENCE -> {
                val seqLen = card.difficulty?.firstOrNull()?.sequenceLength ?: 4
                PuzzleUiState(
                    cardId = card.id,
                    puzzleType = puzzleType,
                    phase = PuzzlePhase.SHOWING,
                    sequence = List(seqLen) { (0..6).random() }
                )
            }

            PuzzleType.CODE_CRACKER -> {
                PuzzleUiState(
                    cardId = card.id,
                    puzzleType = puzzleType,
                    phase = PuzzlePhase.INPUT,
                    dialValues = listOf(0, 0, 0, 0)
                )
            }

            PuzzleType.PATTERN_MIRROR -> {
                val config = card.difficulty?.firstOrNull()
                val size = config?.gridSize ?: 3
                val lit = config?.litTiles ?: 4
                PuzzleUiState(
                    cardId = card.id,
                    puzzleType = puzzleType,
                    phase = PuzzlePhase.INPUT,
                    gridSize = size,
                    gridPattern = generatePattern(size * size, lit),
                    playerGrid = List(size * size) { false }
                )
            }

            PuzzleType.TAP_ANSWER -> {
                val config = card.tapAnswer?.randomOrNull()
                    ?: return PuzzleUiState(cardId = card.id, puzzleType = puzzleType)
                PuzzleUiState(
                    cardId = card.id,
                    puzzleType = puzzleType,
                    phase = PuzzlePhase.INPUT,
                    tapClue = config.clue,
                    answerSymbols = config.answerSymbols,
                    symbolLetters = config.symbolLetters,
                    playerTaps = emptyList()
                )
            }

            PuzzleType.ODD_ONE_OUT -> {
                val config = card.difficulty?.firstOrNull()
                PuzzleUiState(
                    cardId = card.id,
                    puzzleType = puzzleType,
                    phase = PuzzlePhase.INPUT,
                    totalRounds = config?.rounds ?: 5,
                    symbolCount = config?.symbolCount ?: 6,
                    differenceType = config?.differenceType ?: "COLOUR",
                    currentRound = 1,
                    oddIndex = (0 until (config?.symbolCount ?: 6)).random()
                )
            }

            PuzzleType.COLOUR_SEQUENCE -> {
                val config = card.difficulty?.firstOrNull()
                val startLen = config?.startLength ?: 3
                val colourCount = card.colours?.size ?: 7
                PuzzleUiState(
                    cardId = card.id,
                    puzzleType = puzzleType,
                    phase = PuzzlePhase.SHOWING,
                    colourSequence = List(startLen) { (0 until colourCount).random() }
                )
            }

            PuzzleType.SPEED_ROUND -> {
                val config = card.difficulty?.firstOrNull()
                PuzzleUiState(
                    cardId = card.id,
                    puzzleType = puzzleType,
                    phase = PuzzlePhase.INPUT,
                    totalRounds = config?.rounds ?: 7,
                    symbolCount = config?.symbolCount ?: 9,
                    differenceType = config?.differenceType ?: "COLOUR",
                    currentRound = 1,
                    oddIndex = (0 until (config?.symbolCount ?: 9)).random(),
                    timeRemaining = 1f
                )
            }
        }
    }

    // ─── Card 1 — Symbol Sequence ────────────────────────────────────────────

    private fun startSequenceShow() {
        val card = currentCard ?: return
        val displayTimeMs = card.difficulty?.firstOrNull()?.displayTimeMs?.toLong() ?: 800L

        showingJob?.cancel()
        showingJob = viewModelScope.launch {
            delay(600L)
            val sequence = _uiState.value.sequence
            sequence.forEachIndexed { index, _ ->
                _uiState.value = _uiState.value.copy(currentShowIndex = index)
                delay(displayTimeMs)
                _uiState.value = _uiState.value.copy(currentShowIndex = -1)
                delay(200L)
            }
            _uiState.value = _uiState.value.copy(
                phase = PuzzlePhase.INPUT,
                currentShowIndex = -1,
                playerInput = emptyList()
            )
        }
    }

    fun onSymbolTapped(symbolIndex: Int) {
        val state = _uiState.value
        if (state.phase != PuzzlePhase.INPUT) return

        val expected = state.sequence.getOrNull(state.playerInput.size) ?: return
        if (symbolIndex != expected) {
            onAttemptFailed()
            return
        }

        val newInput = state.playerInput + symbolIndex
        if (newInput.size == state.sequence.size) {
            _uiState.value = state.copy(playerInput = newInput, phase = PuzzlePhase.SUCCESS)
            onPuzzleSolved()
        } else {
            _uiState.value = state.copy(playerInput = newInput)
        }
    }

    // ─── Card 2 — Code Cracker ───────────────────────────────────────────────

    fun incrementDial(index: Int) {
        val dials = _uiState.value.dialValues.toMutableList()
        dials[index] = (dials[index] + 1) % 10
        _uiState.value = _uiState.value.copy(dialValues = dials)
    }

    fun decrementDial(index: Int) {
        val dials = _uiState.value.dialValues.toMutableList()
        dials[index] = (dials[index] + 9) % 10
        _uiState.value = _uiState.value.copy(dialValues = dials)
    }

    fun submitCode() {
        val state = _uiState.value
        val riddle = currentCard?.riddles?.firstOrNull() ?: return
        val input = state.dialValues.joinToString("") { it.toString() }

        if (input == riddle.answer) {
            _uiState.value = state.copy(phase = PuzzlePhase.SUCCESS)
            onPuzzleSolved()
        } else {
            onAttemptFailed()
        }
    }

    // ─── Card 3 — Pattern Mirror ─────────────────────────────────────────────

    fun togglePlayerTile(index: Int) {
        val grid = _uiState.value.playerGrid.toMutableList()
        grid[index] = !grid[index]
        _uiState.value = _uiState.value.copy(playerGrid = grid)
    }

    fun submitMirror() {
        val state = _uiState.value
        if (state.playerGrid == state.gridPattern) {
            _uiState.value = state.copy(phase = PuzzlePhase.SUCCESS)
            onPuzzleSolved()
        } else {
            onAttemptFailed()
        }
    }

    // ─── Card 4 — Tap the Answer ─────────────────────────────────────────────

    fun tapAnswerSymbol(symbolIndex: Int) {
        val state = _uiState.value
        if (state.phase != PuzzlePhase.INPUT) return

        val newTaps = state.playerTaps + symbolIndex

        if (newTaps.size < state.answerSymbols.size) {
            _uiState.value = state.copy(playerTaps = newTaps)
            return
        }

        if (newTaps == state.answerSymbols) {
            _uiState.value = state.copy(playerTaps = newTaps, phase = PuzzlePhase.SUCCESS)
            onPuzzleSolved()
        } else {
            onAttemptFailed()
        }
    }

    fun clearTapAnswer() {
        _uiState.value = _uiState.value.copy(playerTaps = emptyList())
    }

    // ─── Card 5 — Odd One Out ────────────────────────────────────────────────

    fun tapOddSymbol(tappedIndex: Int) {
        val state = _uiState.value
        if (state.phase != PuzzlePhase.INPUT) return

        if (tappedIndex == state.oddIndex) {
            if (state.currentRound >= state.totalRounds) {
                _uiState.value = state.copy(phase = PuzzlePhase.SUCCESS)
                onPuzzleSolved()
            } else {
                _uiState.value = state.copy(
                    currentRound = state.currentRound + 1,
                    oddIndex = (0 until state.symbolCount).random()
                )
            }
        } else {
            onAttemptFailed()
        }
    }

    // ─── Card 6 — Colour Sequence ────────────────────────────────────────────

    private fun startColourSequenceShow() {
        val card = currentCard ?: return
        val flashTimeMs = card.difficulty?.firstOrNull()?.flashTimeMs?.toLong() ?: 700L

        showingJob?.cancel()
        showingJob = viewModelScope.launch {
            delay(500L)
            val sequence = _uiState.value.colourSequence
            sequence.forEach { colourIdx ->
                _uiState.value = _uiState.value.copy(activeColourIndex = colourIdx)
                delay(flashTimeMs)
                _uiState.value = _uiState.value.copy(activeColourIndex = null)
                delay(250L)
            }
            _uiState.value = _uiState.value.copy(
                phase = PuzzlePhase.INPUT,
                activeColourIndex = null,
                playerColourInput = emptyList()
            )
        }
    }

    fun tapColour(colourIndex: Int) {
        val state = _uiState.value
        if (state.phase != PuzzlePhase.INPUT) return

        val expected = state.colourSequence.getOrNull(state.playerColourInput.size) ?: return

        if (colourIndex != expected) {
            // Wrong tap: reset sequence within the same attempt
            _uiState.value = state.copy(
                phase = PuzzlePhase.SHOWING,
                playerColourInput = emptyList()
            )
            startColourSequenceShow()
            onAttemptFailed()
            return
        }

        val newInput = state.playerColourInput + colourIndex

        if (newInput.size < state.colourSequence.size) {
            _uiState.value = state.copy(playerColourInput = newInput)
            return
        }

        val card = currentCard ?: return
        val config = card.difficulty?.firstOrNull()
        val maxLen = config?.maxLength ?: 5
        val colourCount = card.colours?.size ?: 7

        if (state.colourSequence.size >= maxLen) {
            _uiState.value = state.copy(playerColourInput = newInput, phase = PuzzlePhase.SUCCESS)
            onPuzzleSolved()
        } else {
            val grown = state.colourSequence + (0 until colourCount).random()
            _uiState.value = state.copy(
                colourSequence = grown,
                playerColourInput = emptyList(),
                phase = PuzzlePhase.SHOWING
            )
            startColourSequenceShow()
        }
    }

    // ─── Card 7 — Speed Round ────────────────────────────────────────────────

    fun onTimerExpired() {
        onAttemptFailed()
    }

    fun tapSpeedRoundSymbol(tappedIndex: Int) {
        tapOddSymbol(tappedIndex)
    }

    fun updateTimeRemaining(value: Float) {
        _uiState.value = _uiState.value.copy(timeRemaining = value)
    }

    // ─── 3-Strike System ────────────────────────────────────────────────────

    private fun onAttemptFailed() {
        val state = _uiState.value
        viewModelScope.launch {
            dao.incrementAttempts(state.cardId)
            val newAttempts = state.attemptsRemaining - 1

            if (newAttempts <= 0) {
                dao.incrementResets(state.cardId)
                _uiState.value = state.copy(
                    attemptsRemaining = 0,
                    phase = PuzzlePhase.FAIL,
                    isFailed = false,
                    isReset = true
                )
            } else {
                _uiState.value = state.copy(
                    attemptsRemaining = newAttempts,
                    phase = PuzzlePhase.FAIL,
                    isFailed = true,
                    isReset = false
                )
            }
        }
    }

    private fun onPuzzleSolved() {
        val state = _uiState.value
        val elapsedMs = System.currentTimeMillis() - startTimeMs
        viewModelScope.launch {
            dao.markBroken(
                cardId = state.cardId,
                timeMs = elapsedMs,
                brokenAt = System.currentTimeMillis()
            )
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private fun generatePattern(totalTiles: Int, litTiles: Int): List<Boolean> {
        val litIndices = (0 until totalTiles).shuffled().take(litTiles).toSet()
        return (0 until totalTiles).map { it in litIndices }
    }

    override fun onCleared() {
        showingJob?.cancel()
        super.onCleared()
    }
}
