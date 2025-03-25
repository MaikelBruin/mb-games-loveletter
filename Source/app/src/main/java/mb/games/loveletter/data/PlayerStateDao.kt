package mb.games.loveletter.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerStateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerState(playerState: PlayerState)

    @Update
    suspend fun updatePlayerState(playerState: PlayerState)

    @Query("SELECT * FROM player_states WHERE gameSessionId = :gameSessionId")
    suspend fun getActivePlayers(gameSessionId: Long): List<PlayerState>

    @Query("SELECT * FROM player_states WHERE playerId = :playerId")
    suspend fun getPlayerState(playerId: Long): PlayerState

}
