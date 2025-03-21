package mb.games.loveletter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_states")
data class PlayerState(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameSessionId: Long,
    val playerId: Long,
    val hand: MutableList<Int> = emptyList<Int>().toMutableList(),
    val discardPile: MutableList<Int> = emptyList<Int>().toMutableList(),
    var isAlive: Boolean = true,
    var favorTokens: Long = 0
)

object DummyPlayerState {
    val playerState: PlayerState = PlayerState(
        gameSessionId = 0,
        playerId = 0
    )
}