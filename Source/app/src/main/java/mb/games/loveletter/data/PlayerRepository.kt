package mb.games.loveletter.data

import kotlinx.coroutines.flow.Flow

class PlayerRepository(private val playerDao: PlayerDao) {

    suspend fun addPlayer(player: Player) {
        playerDao.insertPlayer(player)
    }

    fun getPlayers(): Flow<List<Player>> = playerDao.getAllPlayers()

    fun getPlayerById(id: Long): Flow<Player> {
        return playerDao.getPlayerById(id)
    }

    suspend fun getPlayerByIdSuspend(id: Long): Player {
        return playerDao.getPlayerByIdSuspend(id)
    }

    suspend fun updatePlayer(player: Player) {
        playerDao.updatePlayer(player)
    }

    suspend fun deletePlayer(player: Player) {
        playerDao.deletePlayer(player)
    }

    suspend fun getHumanPlayerWithState(): PlayerWithState {
        return playerDao.getFirstHumanPlayerWithState()
    }

    suspend fun getPlayerWithState(playerId: Long): PlayerWithState {
        return playerDao.getPlayerWithState(playerId)
    }

}