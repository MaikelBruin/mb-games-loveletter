package mb.games.loveletter.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player)

    @Query("SELECT * FROM players")
    fun getAllPlayers(): Flow<List<Player>>

    @Query("SELECT * FROM players WHERE id=:id")
    fun getPlayerById(id: Long): Flow<Player>

    @Query("SELECT * FROM players WHERE id=:id")
    suspend fun getPlayerByIdSuspend(id: Long): Player

    @Update
    suspend fun updatePlayer(player: Player)

    @Delete
    suspend fun deletePlayer(player: Player)

    @Transaction
    @Query("SELECT * FROM players WHERE gameSessionId =:gameSessionId LIMIT 1")
    fun getFirstHumanPlayerForGameSession(gameSessionId: Long): Flow<PlayerWithGameState>

    @Transaction
    @Query("SELECT * FROM players WHERE isHuman = 1 LIMIT 1")
    suspend fun getFirstHumanPlayerWithState(): PlayerWithGameState

    @Transaction
    @Query("SELECT * FROM players WHERE id =:playerId")
    suspend fun getPlayerWithState(playerId: Long): PlayerWithGameState

    @Transaction
    @Query("""
        SELECT * FROM players 
        WHERE gameSessionId = :gameSessionId
    """)
    fun getActivePlayersWithState(gameSessionId: Long): Flow<List<PlayerWithGameState>>

}
