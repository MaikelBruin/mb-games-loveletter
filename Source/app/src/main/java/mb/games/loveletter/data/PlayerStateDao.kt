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

    @Query("UPDATE player_states SET isAlive = 0 WHERE gameSessionId = :gameSessionId AND playerId = :playerId")
    suspend fun eliminatePlayer(gameSessionId: Long, playerId: Long)

    @Query("SELECT * FROM player_states WHERE gameSessionId = :gameSessionId AND isAlive = 1")
    suspend fun getActivePlayers(gameSessionId: Long): List<PlayerState>

}
