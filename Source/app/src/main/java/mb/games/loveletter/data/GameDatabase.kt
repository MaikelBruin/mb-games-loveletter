package mb.games.loveletter.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Player::class, PlayerState::class, GameSession::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class GameDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun playerStateDao(): PlayerStateDao
    abstract fun gameSessionDao(): GameSessionDao
}
