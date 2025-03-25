package mb.games.loveletter.data

class PlayerStateRepository(private val playerStateDao: PlayerStateDao) {

    suspend fun insertPlayerState(playerState: PlayerState) {
        playerStateDao.insertPlayerState(playerState)
    }

    suspend fun updatePlayerState(playerState: PlayerState) {
        playerStateDao.updatePlayerState(playerState)
    }

    suspend fun getPlayerState(playerId: Long): PlayerState {
        return playerStateDao.getPlayerState(playerId)
    }
}