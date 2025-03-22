package mb.games.loveletter.data

import kotlinx.coroutines.flow.Flow

class PlayerStateRepository(private val playerStateDao: PlayerStateDao) {

    suspend fun insertPlayerState(playerState: PlayerState) {
        playerStateDao.insertPlayerState(playerState)
    }

    suspend fun updatePlayerHand(playerId: Long, hand: List<Int>) {
        playerStateDao.updateHand(playerId, hand)
    }

    fun getPlayerState(playerId: Long): Flow<PlayerState> {
        return playerStateDao.getPlayerState(playerId)
    }
}