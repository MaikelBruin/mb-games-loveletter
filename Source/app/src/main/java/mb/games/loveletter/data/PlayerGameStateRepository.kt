package mb.games.loveletter.data

class PlayerGameStateRepository(private val playerStateDao: PlayerGameStateDao) {

    suspend fun insertPlayerState(playerState: PlayerGameState) {
        playerStateDao.insertPlayerGameState(playerState)
    }

    suspend fun updatePlayerState(playerState: PlayerGameState) {
        playerStateDao.updatePlayerGameState(playerState)
    }

    suspend fun getPlayerState(playerId: Long): PlayerGameState {
        return playerStateDao.getPlayerGameState(playerId)
    }
}