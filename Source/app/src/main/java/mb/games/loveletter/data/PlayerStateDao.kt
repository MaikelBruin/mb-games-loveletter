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

    @Query("SELECT * FROM player_states WHERE playerId = :playerId")
    suspend fun getPlayerState(playerId: Long): PlayerState

    @Query("UPDATE player_states SET hand = :hand WHERE playerId = :playerId")
    suspend fun updateHand(playerId: Long, hand: List<Int>)

    @Query(
        """
        SELECT players.* FROM players
        INNER JOIN player_states ON players.id = player_states.playerId
        WHERE player_states.gameSessionId = :gameSessionId
        AND players.isHuman = 1
        AND player_states.isAlive = 1
        LIMIT 1
    """
    )
    suspend fun getFirstHumanPlayer(gameSessionId: Long): Player?

}
