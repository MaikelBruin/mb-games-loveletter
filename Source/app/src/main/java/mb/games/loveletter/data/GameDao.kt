package mb.games.loveletter.data

import androidx.room.*

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player)

    @Query("SELECT * FROM players")
    suspend fun getAllPlayers(): List<Player>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameSession(session: GameSession)

    @Query("SELECT * FROM game_sessions WHERE id = :sessionId")
    suspend fun getGameSession(sessionId: Int): GameSession?

    @Query("DELETE FROM game_sessions")
    suspend fun clearGameSessions()
}
