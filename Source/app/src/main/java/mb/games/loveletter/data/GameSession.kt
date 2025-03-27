package mb.games.loveletter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_sessions")
data class GameSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    var currentRound: Int = 1,
    val playerIds: List<Long>,
    val turnOrder: List<Long>,
    val tokensToWin: Int,
    var isActive: Boolean = false
)

object DummyGameSession {
    val gameSession = GameSession(
        playerIds = listOf(0, 1, 2), turnOrder = listOf(2, 1, 0), tokensToWin = 1
    )
}
