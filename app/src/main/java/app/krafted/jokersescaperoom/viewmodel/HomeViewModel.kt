package app.krafted.jokersescaperoom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.jokersescaperoom.data.CurseRepository
import app.krafted.jokersescaperoom.data.db.AppDatabase
import app.krafted.jokersescaperoom.data.db.CardProgress
import app.krafted.jokersescaperoom.data.model.CardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CurseRepository(application)
    private val dao = AppDatabase.getInstance(application).cardProgressDao()

    private val _cardStates = MutableStateFlow<List<CardState>>(emptyList())
    val cardStates: StateFlow<List<CardState>> = _cardStates.asStateFlow()

    private val _brokenCount = MutableStateFlow(0)
    val brokenCount: StateFlow<Int> = _brokenCount.asStateFlow()

    private val _isVictory = MutableStateFlow(false)
    val isVictory: StateFlow<Boolean> = _isVictory.asStateFlow()

    init {
        viewModelScope.launch {
            // Ensure every card has a Room row on first launch
            val curses = repository.getCurses()
            curses.forEach { card ->
                if (dao.getById(card.id) == null) {
                    dao.upsert(CardProgress(cardId = card.id))
                }
            }

            // Observe Room for real-time updates
            dao.getAllFlow().collectLatest { progressList ->
                val progressMap = progressList.associateBy { it.cardId }
                _cardStates.value = curses.map { card ->
                    val progress = progressMap[card.id]
                    CardState(
                        cardId = card.id,
                        title = card.title,
                        subtitle = card.subtitle,
                        symbol = card.symbol,
                        background = card.background,
                        accentColor = card.accentColor,
                        puzzleType = card.puzzleType,
                        intro = card.intro,
                        isCurseBroken = progress?.isCurseBroken ?: false,
                        bestTimeMs = progress?.bestTimeMs
                    )
                }
                val broken = progressList.count { it.isCurseBroken }
                _brokenCount.value = broken
                _isVictory.value = broken == 7
            }
        }
    }
}
