package mb.games.loveletter.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameSession(session: GameSession): Long

    @Query("SELECT * FROM game_sessions WHERE id = :sessionId")
    fun getGameSession(sessionId: Int): Flow<GameSession>

    @Query("SELECT * FROM game_sessions")
    fun getAllGameSessions(): Flow<List<GameSession>>

    @Query("DELETE FROM game_sessions")
    suspend fun clearGameSessions()

    @Update
    suspend fun updateGameSession(game: GameSession)

    @Delete
    suspend fun deleteGameSession(game: GameSession)
}
