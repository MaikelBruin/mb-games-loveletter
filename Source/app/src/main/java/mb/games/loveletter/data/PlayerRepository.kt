package mb.games.loveletter.data

import kotlinx.coroutines.flow.Flow

class PlayerRepository(private val playerDao: PlayerDao) {

    suspend fun addPlayer(player: Player) {
        playerDao.insertPlayer(player)
    }

    fun getPlayers(): Flow<List<Player>> = playerDao.getAllPlayers()

    fun getPlayerById(id: Int): Flow<Player> {
        return playerDao.getPlayerById(id)
    }

    suspend fun updatePlayer(player: Player) {
        playerDao.updatePlayer(player)
    }

    suspend fun deletePlayer(player: Player) {
        playerDao.deletePlayer(player)
    }

}