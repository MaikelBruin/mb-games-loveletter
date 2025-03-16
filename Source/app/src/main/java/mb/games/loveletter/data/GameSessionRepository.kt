package mb.games.loveletter.data

import kotlinx.coroutines.flow.Flow

class GameSessionRepository(private val gameSessionDao: GameSessionDao) {

    suspend fun addGameSession(game: GameSession) {
        gameSessionDao.insertGameSession(game)
    }

    fun getGameSessions(): Flow<List<GameSession>> = gameSessionDao.getAllGameSessions()

    fun getGameSession(id: Int): Flow<GameSession> {
        return gameSessionDao.getGameSession(id)
    }

    suspend fun deleteGameSessions() {
        gameSessionDao.clearGameSessions()
    }

}