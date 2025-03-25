package mb.games.loveletter.data

import kotlinx.coroutines.flow.Flow

class PlayerRepository(private val playerDao: PlayerDao) {

    suspend fun addPlayer(player: Player) {
        playerDao.insertPlayer(player)
    }

    fun getPlayers(): Flow<List<Player>> = playerDao.getAllPlayers()

    fun getActivePlayersWithState(gameSessionId: Long): Flow<List<PlayerWithGameState>> = playerDao.getActivePlayersWithState(gameSessionId)

    fun getPlayerById(id: Long): Flow<Player> {
        return playerDao.getPlayerById(id)
    }

    suspend fun updatePlayer(player: Player) {
        playerDao.updatePlayer(player)
    }

    suspend fun deletePlayer(player: Player) {
        playerDao.deletePlayer(player)
    }

    suspend fun getHumanPlayerWithState(): PlayerWithGameState {
        return playerDao.getFirstHumanPlayerWithState()
    }

    suspend fun getPlayerWithState(playerId: Long): PlayerWithGameState {
        return playerDao.getPlayerWithState(playerId)
    }

}