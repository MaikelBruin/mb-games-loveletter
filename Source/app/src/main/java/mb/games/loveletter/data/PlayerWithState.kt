package mb.games.loveletter.data

import androidx.room.Embedded
import androidx.room.Relation

data class PlayerWithState(
    @Embedded val player: Player,
    @Relation(
        parentColumn = "id",
        entityColumn = "playerId"
    )
    val playerState: PlayerState
)

object DummyPlayerWithState {
    val playerWithState = PlayerWithState(
        player = DummyPlayer.player,
        playerState = DummyPlayerState.playerState
    )
}
