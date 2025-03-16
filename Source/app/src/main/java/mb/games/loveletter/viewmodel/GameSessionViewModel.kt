package mb.games.loveletter.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import mb.games.loveletter.data.GameSessionRepository
import mb.games.loveletter.data.Player

class GameSessionViewModel(private val gameSessionRepository: GameSessionRepository) : ViewModel() {

    var currentTurnState by mutableIntStateOf(0)

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
}
