package mb.games.loveletter.data

class PlayerStateRepository(private val playerStateDao: PlayerStateDao) {

    suspend fun insertPlayerState(playerState: PlayerState) {
        playerStateDao.insertPlayerState(playerState)
    }

    suspend fun updatePlayerHand(playerId: Long, hand: List<Int>) {
        playerStateDao.updateHand(playerId, hand)
    }

    suspend fun getHumanPlayer(gameSessionId: Long): Player? {
        return playerStateDao.getFirstHumanPlayer(gameSessionId)
    }

    suspend fun getPlayerState(playerId: Long): PlayerState {
        return playerStateDao.getPlayerState(playerId)
    }
}