package mb.games.loveletter.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Player::class, PlayerGameState::class, GameSession::class], version = 10, exportSchema = true)
@TypeConverters(Converters::class)
abstract class GameDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun playerStateDao(): PlayerGameStateDao
    abstract fun gameSessionDao(): GameSessionDao
}
