package mb.games.loveletter.data

import androidx.room.Embedded
import androidx.room.Relation

data class GameSessionWithPlayers(
    @Embedded val gameSession: GameSession,
    @Relation(
        parentColumn = "id",
        entityColumn = "gameSessionId"
    )
    val players: List<Player>
)
