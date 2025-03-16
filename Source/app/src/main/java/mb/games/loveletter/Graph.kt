package mb.games.loveletter

import android.content.Context
import androidx.room.Room
import mb.games.loveletter.data.GameDatabase
import mb.games.loveletter.data.GameSessionRepository
import mb.games.loveletter.data.PlayerRepository

object Graph {
    lateinit var database: GameDatabase

    val playerRepository by lazy {
        PlayerRepository(playerDao = database.playerDao())
    }

    val gameSessionRepository by lazy {
        GameSessionRepository(gameSessionDao = database.gameSessionDao())
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, GameDatabase::class.java, "game.db").build()
    }

}