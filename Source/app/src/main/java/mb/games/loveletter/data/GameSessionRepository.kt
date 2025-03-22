package mb.games.loveletter.data

import kotlinx.coroutines.flow.Flow

class GameSessionRepository(private val gameSessionDao: GameSessionDao) {

    suspend fun addGameSession(game: GameSession): Long {
        return gameSessionDao.insertGameSession(game)
    }

    fun getGameSessions(): Flow<List<GameSession>> = gameSessionDao.getAllGameSessions()

    suspend fun getGameSession(id: Long): GameSession {
        return gameSessionDao.getGameSession(id)
    }

    fun getActiveGameSession(): Flow<GameSession?> {
        return gameSessionDao.getActiveGameSession()
    }

    suspend fun updateGameSession(game: GameSession) {
        gameSessionDao.updateGameSession(game)
    }

    suspend fun deleteGameSession(game: GameSession) {
        gameSessionDao.deleteGameSession(game)
    }
}