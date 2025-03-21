package mb.games.loveletter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    var wins: Int = 0,
    var plays: Int = 0,
    val isHuman: Boolean = false,
    var gameSessionId: Long = 0
)
