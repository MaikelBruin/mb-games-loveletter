package mb.games.loveletter.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "player_states",
    foreignKeys = [ForeignKey(
        entity = Player::class,
        parentColumns = ["id"],
        childColumns = ["playerId"]
    ), ForeignKey(
        entity = GameSession::class, parentColumns = ["id"], childColumns = ["gameSessionId"]
    )]
)
data class PlayerState(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameSessionId: Long,
    val playerId: Long,
    val hand: List<Int> = emptyList(),
    val discardPile: List<Int> = emptyList()
)