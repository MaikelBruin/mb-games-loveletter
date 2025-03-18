package mb.games.loveletter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_sessions")
data class GameSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val currentTurn: Int = 0,
    val playerIds: List<Long>,
    val deck: List<Int>
)
