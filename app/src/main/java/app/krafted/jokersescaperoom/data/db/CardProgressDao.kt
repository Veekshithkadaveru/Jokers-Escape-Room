package app.krafted.jokersescaperoom.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CardProgressDao {

    @Query("SELECT * FROM card_progress")
    fun getAllFlow(): Flow<List<CardProgress>>

    @Query("SELECT * FROM card_progress")
    suspend fun getAll(): List<CardProgress>

    @Query("SELECT * FROM card_progress WHERE cardId = :cardId")
    suspend fun getById(cardId: String): CardProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: CardProgress)

    @Query("""
        UPDATE card_progress
        SET isCurseBroken = 1,
            bestTimeMs = CASE WHEN bestTimeMs IS NULL OR :timeMs < bestTimeMs THEN :timeMs ELSE bestTimeMs END,
            brokenAt = :brokenAt
        WHERE cardId = :cardId
    """)
    suspend fun markBroken(cardId: String, timeMs: Long, brokenAt: Long)

    @Query("UPDATE card_progress SET totalAttempts = totalAttempts + 1 WHERE cardId = :cardId")
    suspend fun incrementAttempts(cardId: String)

    @Query("UPDATE card_progress SET totalResets = totalResets + 1 WHERE cardId = :cardId")
    suspend fun incrementResets(cardId: String)

    @Query("SELECT COUNT(*) FROM card_progress WHERE isCurseBroken = 1")
    fun brokenCountFlow(): Flow<Int>
}
