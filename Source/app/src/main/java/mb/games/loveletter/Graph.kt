package mb.games.loveletter

import android.content.Context
import androidx.room.Room
import mb.games.loveletter.data.GameDatabase
import mb.games.loveletter.data.GameSessionRepository
import mb.games.loveletter.data.PlayerRepository
import mb.games.loveletter.data.PlayerStateRepository

object Graph {
    lateinit var database: GameDatabase

    val playerRepository by lazy {
        PlayerRepository(playerDao = database.playerDao())
    }

    val playerStateRepository by lazy {
        PlayerStateRepository(playerStateDao = database.playerStateDao())
    }

    val gameSessionRepository by lazy {
        GameSessionRepository(gameSessionDao = database.gameSessionDao())
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, GameDatabase::class.java, "game.db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
//        clearDatabase()
    }

    fun clearDatabase() {
        database.clearAllTables()
    }

}