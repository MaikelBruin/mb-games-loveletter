package mb.games.loveletter.data

import androidx.room.Embedded
import androidx.room.Relation

data class PlayerWithGameState(
    @Embedded val player: Player, @Relation(
        parentColumn = "id", entityColumn = "playerId"
    ) val playerGameState: PlayerGameState
)

object DummyPlayerWithGameState {
    val playerWithGameState = PlayerWithGameState(
        player = DummyPlayer.player, playerGameState = DummyPlayerState.playerState
    )
}
