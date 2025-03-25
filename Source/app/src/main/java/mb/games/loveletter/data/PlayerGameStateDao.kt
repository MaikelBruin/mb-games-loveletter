package mb.games.loveletter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlayerGameStateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerState(playerState: PlayerGameState)

    @Update
    suspend fun updatePlayerState(playerState: PlayerGameState)

    @Query("SELECT * FROM player_states WHERE gameSessionId = :gameSessionId")
    suspend fun getActivePlayers(gameSessionId: Long): List<PlayerGameState>

    @Query("SELECT * FROM player_states WHERE playerId = :playerId")
    suspend fun getPlayerState(playerId: Long): PlayerGameState

}
