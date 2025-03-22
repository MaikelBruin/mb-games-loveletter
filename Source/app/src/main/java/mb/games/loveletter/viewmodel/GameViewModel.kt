package mb.games.loveletter.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import mb.games.loveletter.Graph
import mb.games.loveletter.data.Deck
import mb.games.loveletter.data.GameSession
import mb.games.loveletter.data.GameSessionRepository
import mb.games.loveletter.data.PlayerRepository
import mb.games.loveletter.data.Player
import mb.games.loveletter.data.PlayerState
import mb.games.loveletter.data.PlayerStateRepository
import mb.games.loveletter.data.PlayerWithState

class GameViewModel(
    private val playerRepository: PlayerRepository = Graph.playerRepository,
    private val playerStateRepository: PlayerStateRepository = Graph.playerStateRepository,
    private val gameSessionRepository: GameSessionRepository = Graph.gameSessionRepository
) : ViewModel() {

    private val _currentTurn = mutableLongStateOf(0)
    val currentTurn: State<Long> = _currentTurn

    private val _currentPlayer = mutableStateOf<Player?>(null)
    val currentPlayer: State<Player?> = _currentPlayer

    private val _currentGameSession = MutableStateFlow<GameSession?>(null)
    val currentGameSession: StateFlow<GameSession?> = _currentGameSession

    private val _deck = MutableStateFlow(Deck.createNewDeck())
    val deck: StateFlow<Deck> = _deck.asStateFlow()

    var playerNameState by mutableStateOf("")
    var isHumanState by mutableStateOf(false)

    fun onPlayerNameChanged(newName: String) {
        playerNameState = newName
    }

    fun onPlayerIsHumanChanged(isHuman: Boolean) {
        isHumanState = isHuman
    }

    lateinit var getAllPlayers: Flow<List<Player>>
    private lateinit var getAllGameSessions: Flow<List<GameSession>>
    lateinit var getHumanPlayerWithState: Flow<PlayerWithState>

    init {
        viewModelScope.launch {
            getAllPlayers = playerRepository.getPlayers()
            if (getAllPlayers.first().isEmpty()) {
                insertDefaultPlayers()
            }

            getAllGameSessions = gameSessionRepository.getGameSessions()
            getHumanPlayerWithState = playerRepository.getHumanPlayerWithState()
            gameSessionRepository.getActiveGameSession().collect { data ->
                _currentGameSession.value = data
            }
            if (currentGameSession.value != null) {
                loadCurrentGameState()
            }
        }
    }

    //players
    private suspend fun insertDefaultPlayers() {
        val defaultPlayers = listOf(
            Player(name = "MB", isHuman = true), Player(name = "Bot 1"), Player(name = "Bot 2")
        )
        defaultPlayers.forEach { player ->
            playerRepository.addPlayer(player)
        }
    }

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

    fun getAPlayerById(id: Long): Flow<Player> {
        return playerRepository.getPlayerById(id)
    }

    private suspend fun loadCurrentGameState() {
        viewModelScope.launch {
            currentGameSession.collect { gameSession ->
                if (gameSession != null) {
                    _currentTurn.longValue = gameSession.turnOrder.first()
                    _currentPlayer.value = playerRepository.getPlayerByIdSuspend(currentTurn.value)
                }
            }
        }
    }

    fun deletePlayer(player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            playerRepository.deletePlayer(player = player)
            getAllPlayers = playerRepository.getPlayers()
        }
    }

    //game sessions
    fun updateGameSession(gameSession: GameSession) {
        viewModelScope.launch(Dispatchers.IO) {
            gameSessionRepository.updateGameSession(game = gameSession)
        }
    }

    suspend fun getGameSession(id: Long): GameSession {
        return gameSessionRepository.getGameSession(id)
    }

    fun onStartNewGame(playerIds: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            val turnOrder = playerIds.shuffled()
            val gameSession = GameSession(
                playerIds = playerIds,
                turnOrder = turnOrder,
                tokensToWin = getNumberOfTokensToWin(playerIds.size),
                isActive = true
            )

            // Create empty player states
            playerIds.forEach { playerId ->
                val playerState = PlayerState(
                    gameSessionId = gameSession.id, playerId = playerId
                )
                playerStateRepository.insertPlayerState(playerState)
            }

            val hands = deck.value.deal(playerIds)
            for ((playerId, card) in hands) {
                playerStateRepository.updatePlayerHand(playerId, listOf(card.id))
            }

            //update states
            _currentTurn.longValue = turnOrder.first()
            loadCurrentGameState()
        }
    }

    //player state
    fun getHumanPlayerWithState(): Flow<PlayerWithState> {
        return playerRepository.getHumanPlayerWithState()
    }

    private fun getNumberOfTokensToWin(playerCount: Int): Int {
        return when (playerCount) {
            2 -> 6
            3 -> 5
            4 -> 4
            5 -> 3
            else -> 6
        }
    }
}