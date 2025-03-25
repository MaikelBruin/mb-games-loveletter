package mb.games.loveletter.data

class PlayerStateRepository(private val playerStateDao: PlayerStateDao) {

    suspend fun insertPlayerState(playerState: PlayerGameState) {
        playerStateDao.insertPlayerState(playerState)
    }

    suspend fun updatePlayerState(playerState: PlayerGameState) {
        playerStateDao.updatePlayerState(playerState)
    }

    suspend fun getPlayerState(playerId: Long): PlayerGameState {
        return playerStateDao.getPlayerState(playerId)
    }
}