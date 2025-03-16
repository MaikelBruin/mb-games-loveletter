package mb.games.loveletter.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player)

    @Query("SELECT * FROM players")
    fun getAllPlayers(): Flow<List<Player>>

    @Query("SELECT * FROM players WHERE id=:id")
    fun getPlayerById(id: Int): Flow<Player>

    @Update
    suspend fun updatePlayer(player: Player)

    @Delete
    suspend fun deletePlayer(player: Player)

    @Query("SELECT * FROM players WHERE isHuman == 1")
    suspend fun getAllHumanPlayers(): List<Player>

    @Query("SELECT * FROM players WHERE isHuman == 0")
    suspend fun getAllBots(): List<Player>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameSession(session: GameSession)

    @Query("SELECT * FROM game_sessions WHERE id = :sessionId")
    fun getGameSession(sessionId: Int): Flow<GameSession>

    @Query("SELECT * FROM game_sessions")
    fun getAllGameSessions(): Flow<List<GameSession>>

    @Query("DELETE FROM game_sessions")
    suspend fun clearGameSessions()
}
