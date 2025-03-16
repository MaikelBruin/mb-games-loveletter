package mb.games.loveletter.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import mb.games.loveletter.data.GameSessionRepository
import mb.games.loveletter.data.Player

class GameSessionViewModel(private val gameSessionRepository: GameSessionRepository) : ViewModel() {

    var playerNameState by mutableStateOf("")
    var isHumanState by mutableStateOf(false)
    var currentTurnState by mutableIntStateOf(0)

    fun onPlayerNameChanged(newName: String) {
        playerNameState = newName
    }

    fun onPlayerIsHumanChanged(isHuman: Boolean) {
        isHumanState = isHuman
    }

    fun onCurrentTurnChanged(currentTurn: Int) {
        currentTurnState = currentTurn
    }

    lateinit var getAllPlayers: Flow<List<Player>>

    init {
        viewModelScope.launch {
            getAllPlayers = gameSessionRepository.getPlayers()
        }
    }

    fun addPlayer(player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            gameSessionRepository.addPlayer(player = player)
        }
    }

    fun updatePlayer(player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            gameSessionRepository.updatePlayer(player = player)
        }
    }

    fun getAPlayerById(id: Int): Flow<Player> {
        return gameSessionRepository.getPlayerById(id)
    }

    fun deletePlayer(player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            gameSessionRepository.deletePlayer(player = player)
            getAllPlayers = gameSessionRepository.getPlayers()
        }
    }
}
