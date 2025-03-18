package mb.games.loveletter.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerStateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerState(playerState: PlayerState)

}
