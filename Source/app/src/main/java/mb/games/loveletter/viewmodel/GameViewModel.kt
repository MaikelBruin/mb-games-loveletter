package mb.games.loveletter.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import mb.games.loveletter.Graph
import mb.games.loveletter.data.GameSession
import mb.games.loveletter.data.GameSessionRepository
import mb.games.loveletter.data.PlayerRepository
import mb.games.loveletter.data.Player

class GameViewModel(
    private val playerRepository: PlayerRepository = Graph.playerRepository,
    private val gameSessionRepository: GameSessionRepository = Graph.gameSessionRepository
) : ViewModel() {

    var playerNameState by mutableStateOf("")
    var isHumanState by mutableStateOf(false)

    fun onPlayerNameChanged(newName: String) {
        playerNameState = newName
    }

    fun onPlayerIsHumanChanged(isHuman: Boolean) {
        isHumanState = isHuman
    }

    lateinit var getAllPlayers: Flow<List<Player>>
    lateinit var getAllGameSessions: Flow<List<GameSession>>

    init {
        viewModelScope.launch {
            getAllPlayers = playerRepository.getPlayers()
            getAllGameSessions = gameSessionRepository.getGameSessions()
        }
    }

    //players
    fun addPlayer(player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            playerRepository.addPlayer(player = player)
        }
    }

    fun updatePlayer(player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            playerRepository.updatePlayer(player = player)
        }
    }

    fun getAPlayerById(id: Int): Flow<Player> {
        return playerRepository.getPlayerById(id)
    }

    fun deletePlayer(player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            playerRepository.deletePlayer(player = player)
            getAllPlayers = playerRepository.getPlayers()
        }
    }

    //game sessions
    fun addGameSession(gameSession: GameSession) {
        viewModelScope.launch(Dispatchers.IO) {
            gameSessionRepository.addGameSession(game = gameSession)
        }
    }

    fun updateGameSession(gameSession: GameSession) {
        viewModelScope.launch(Dispatchers.IO) {
            gameSessionRepository.updateGameSession(game = gameSession)
        }
    }

    fun getGameSession(id: Int): Flow<GameSession> {
        return gameSessionRepository.getGameSession(id)
    }

    fun deleteGameSession(gameSession: GameSession) {
        viewModelScope.launch(Dispatchers.IO) {
            gameSessionRepository.deleteGameSession(game = gameSession)
            getAllGameSessions = gameSessionRepository.getGameSessions()
        }
    }
}
