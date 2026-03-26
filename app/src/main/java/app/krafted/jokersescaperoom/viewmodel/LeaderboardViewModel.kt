package app.krafted.jokersescaperoom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.jokersescaperoom.data.CurseRepository
import app.krafted.jokersescaperoom.data.db.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class LeaderboardEntry(
    val cardId: String,
    val title: String,
    val symbolName: String,
    val accentColor: String,
    val bestTimeMs: Long?,
    val brokenAt: Long?
)

class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CurseRepository(application)
    private val dao = AppDatabase.getInstance(application).cardProgressDao()

    private val _entries = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val entries: StateFlow<List<LeaderboardEntry>> = _entries.asStateFlow()

    init {
        val cards = repository.getCurses()
        viewModelScope.launch {
            dao.getAllFlow().collectLatest { progressList ->
                val progressMap = progressList.associateBy { it.cardId }
                _entries.value = cards.map { card ->
                    val progress = progressMap[card.id]
                    LeaderboardEntry(
                        cardId = card.id,
                        title = card.title,
                        symbolName = card.symbol,
                        accentColor = card.accentColor,
                        bestTimeMs = progress?.bestTimeMs,
                        brokenAt = progress?.brokenAt
                    )
                }
            }
        }
    }
}
