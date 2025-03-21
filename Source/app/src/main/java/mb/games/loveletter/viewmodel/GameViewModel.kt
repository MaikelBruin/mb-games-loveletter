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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mb.games.loveletter.Graph
import mb.games.loveletter.data.Deck
import mb.games.loveletter.data.GameSession
import mb.games.loveletter.data.GameSessionRepository
import mb.games.loveletter.data.PlayerRepository
import mb.games.loveletter.data.Player
import mb.games.loveletter.data.PlayerState
import mb.games.loveletter.data.PlayerStateRepository

class GameViewModel(
    private val playerRepository: PlayerRepository = Graph.playerRepository,
    private val playerStateRepository: PlayerStateRepository = Graph.playerStateRepository,
    private val gameSessionRepository: GameSessionRepository = Graph.gameSessionRepository
) : ViewModel() {

    private val _currentGameSession = mutableStateOf<GameSession?>(null)
    val currentGameSession: State<GameSession?> = _currentGameSession

    private val _currentTurn = mutableLongStateOf(0)
    val currentTurn: State<Long> = _currentTurn

    private val _currentPlayer = mutableStateOf<Player?>(null)
    val currentPlayer: State<Player?> = _currentPlayer

    private var _deck = MutableStateFlow<Deck?>(null)
    val deck: StateFlow<Deck?> = _deck.asStateFlow()

    private val _humanPlayer = MutableStateFlow<Player?>(null)
    val humanPlayer: StateFlow<Player?> = _humanPlayer

    private val _humanPlayerState = MutableStateFlow<PlayerState?>(null)
    val humanPlayerState: StateFlow<PlayerState?> = _humanPlayerState.asStateFlow()

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

    init {
        viewModelScope.launch {
            val players = playerRepository.getPlayers()
            getAllPlayers = players
            if (players.first().isEmpty()) {
                insertDefaultPlayers()
            }

            getAllGameSessions = gameSessionRepository.getGameSessions()
            _currentGameSession.value = gameSessionRepository.getActiveGameSession()
            if (currentGameSession.value != null) {
                loadActiveGameState(currentGameSession.value!!)
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

    private suspend fun loadActiveGameState(gameSession: GameSession) {
        _currentTurn.longValue = gameSession.turnOrder.first()
        _currentPlayer.value = playerRepository.getPlayerByIdSuspend(currentTurn.value)
        val humanPlayer = playerRepository.getHumanPlayer(gameSession.id)
        if (humanPlayer != null) {
            _humanPlayer.value = humanPlayer
            _humanPlayerState.value = playerStateRepository.getPlayerState(humanPlayer.id)
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
            val deck = Deck.createNewDeck()
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

            val hands = deck.deal(playerIds)
            for ((playerId, card) in hands) {
                playerStateRepository.updatePlayerHand(playerId, listOf(card.id))
            }

            //update states
            _currentGameSession.value = gameSession
            _currentTurn.longValue = turnOrder.first()
            _deck.value = deck
            loadActiveGameState(gameSession)
        }
    }

    //player state
    private fun getPlayerState(playerId: Long) {
        viewModelScope.launch {
            _humanPlayerState.value = playerStateRepository.getPlayerState(playerId)
        }
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
