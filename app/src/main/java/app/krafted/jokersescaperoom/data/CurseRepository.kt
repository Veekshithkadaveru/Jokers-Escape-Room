package app.krafted.jokersescaperoom.data

import android.content.Context
import app.krafted.jokersescaperoom.data.model.CursedCard
import app.krafted.jokersescaperoom.data.model.CursesResponse
import com.google.gson.Gson

class CurseRepository(private val context: Context) {

    private var cached: List<CursedCard>? = null

    fun getCurses(): List<CursedCard> {
        cached?.let { return it }
        val json = context.assets.open("curses.json").bufferedReader().use { it.readText() }
        val result = Gson().fromJson(json, CursesResponse::class.java).cards
        cached = result
        return result
    }

    fun getCard(cardId: String): CursedCard? {
        return getCurses().find { it.id == cardId }
    }
}
