package app.krafted.jokersescaperoom.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_progress")
data class CardProgress(
    @PrimaryKey val cardId: String,
    val isCurseBroken: Boolean = false,
    val bestTimeMs: Long? = null,
    val totalAttempts: Int = 0,
    val totalResets: Int = 0,
    val brokenAt: Long? = null
)
