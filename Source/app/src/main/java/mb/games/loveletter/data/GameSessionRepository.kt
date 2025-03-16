package mb.games.loveletter.data

import kotlinx.coroutines.flow.Flow

class GameSessionRepository(private val gameSessionDao: GameSessionDao) {

    suspend fun addPlayer(player: Player) {
        gameSessionDao.insertPlayer(player)
    }

    suspend fun getPlayers(): Flow<List<Player>> = gameSessionDao.getAllPlayers()

}