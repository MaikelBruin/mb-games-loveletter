package mb.games.loveletter

import android.content.Context
import androidx.room.Room
import mb.games.loveletter.data.GameDatabase
import mb.games.loveletter.data.GameSessionRepository

object Graph {
    lateinit var database: GameDatabase

    val gameSessionRepository by lazy{
        GameSessionRepository(gameSessionDao = database.gameDao())
    }

    fun provide(context: Context){
        database = Room.databaseBuilder(context, GameDatabase::class.java, "game.db").build()
    }

}