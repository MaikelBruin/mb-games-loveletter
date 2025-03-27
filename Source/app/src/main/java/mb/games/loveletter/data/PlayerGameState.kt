package mb.games.loveletter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_states")
data class PlayerGameState(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameSessionId: Long,
    val playerId: Long,
    var favorTokens: Int = 0
)

object DummyPlayerState {
    val playerState: PlayerGameState = PlayerGameState(
        gameSessionId = 0,
        playerId = 0
    )
}