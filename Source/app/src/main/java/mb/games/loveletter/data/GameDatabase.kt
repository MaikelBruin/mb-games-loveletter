package mb.games.loveletter.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Player::class, GameSession::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameDao(): GameSessionDao
}
