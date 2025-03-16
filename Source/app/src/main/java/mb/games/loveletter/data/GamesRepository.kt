package mb.games.loveletter.data

import kotlinx.coroutines.flow.Flow

class GamesRepository(private val gameSessionDao: PlayerDao) {

    suspend fun addGameSession(game: GameSession) {
        gameSessionDao.insertGameSession(game)
    }

    suspend fun getGameSessions(): Flow<List<GameSession>> = gameSessionDao.getAllGameSessions()

    suspend fun getGameSession(id: Int): Flow<GameSession> {
        return gameSessionDao.getGameSession(id)
    }

    suspend fun deleteGameSessions() {
        gameSessionDao.clearGameSessions()
    }

}