package mb.games.loveletter.data

import kotlinx.coroutines.flow.Flow

class GameSessionRepository(private val gameSessionDao: GameSessionDao) {

    suspend fun addPlayer(player: Player) {
        gameSessionDao.insertPlayer(player)
    }

    fun getPlayers(): Flow<List<Player>> = gameSessionDao.getAllPlayers()

    fun getPlayerById(id: Int): Flow<Player> {
        return gameSessionDao.getPlayerById(id)
    }

    suspend fun updatePlayer(player: Player) {
        gameSessionDao.updatePlayer(player)
    }

    suspend fun deletePlayer(player: Player) {
        gameSessionDao.deletePlayer(player)
    }

}