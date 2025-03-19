package mb.games.loveletter.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _currentTurn = mutableStateOf<Long?>(null)
    val currentTurn: State<Long?> = _currentTurn

    private val _currentGameSession = mutableStateOf<GameSession?>(null)
    val currentGameSession: State<GameSession?> = _currentGameSession

    private var _deck = MutableStateFlow<Deck?>(null)
    val deck: StateFlow<Deck?> = _deck.asStateFlow()

    private val _humanPlayer = MutableStateFlow<Player?>(null)
    val humanPlayer: StateFlow<Player?> = _humanPlayer.asStateFlow()

    private val _playerState = MutableStateFlow<PlayerState?>(null)
    val playerState: StateFlow<PlayerState?> = _playerState.asStateFlow()

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
            _currentGameSession.value = gameSessionRepository.getActiveGameSession()
            getHumanPlayerForActiveGame()
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

    fun getAPlayerById(id: Long): Flow<Player> {
        return playerRepository.getPlayerById(id)
    }

    private fun getHumanPlayerForActiveGame() {
        viewModelScope.launch(Dispatchers.IO) {
            val gameSessionId = _currentGameSession.value!!.id
            val player = playerStateRepository.getHumanPlayer(gameSessionId)
            _humanPlayer.value = player
            player?.let {
                // Once the first human player is found, fetch their player state
                loadPlayerState(it.id)
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
    fun addGameSession(gameSession: GameSession) {
        viewModelScope.launch(Dispatchers.IO) {
            val gameSessionId = gameSessionRepository.addGameSession(game = gameSession)
            _currentGameSession.value = gameSessionRepository.getGameSession(gameSessionId)
        }
    }

    fun updateGameSession(gameSession: GameSession) {
        viewModelScope.launch(Dispatchers.IO) {
            gameSessionRepository.updateGameSession(game = gameSession)
        }
    }

    suspend fun getGameSession(id: Long): GameSession {
        return gameSessionRepository.getGameSession(id)
    }

    fun deleteGameSession(gameSession: GameSession) {
        viewModelScope.launch(Dispatchers.IO) {
            gameSessionRepository.deleteGameSession(game = gameSession)
            getAllGameSessions = gameSessionRepository.getGameSessions()
        }
    }

    fun startNewGame(playerIds: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            val deck = Deck.createNewDeck()
            _deck.value = deck
            val deckIds = deck.getCards().map { it.id } //TODO: remove deck state from db?

            val turnOrder = playerIds.shuffled()
            val gameSession = GameSession(
                playerIds = playerIds,
                turnOrder = turnOrder,
                deck = deckIds.toMutableList(),
                tokensToWin = getNumberOfTokensToWin(playerIds.size),
                isActive = true
            )

            val gameId = gameSessionRepository.addGameSession(gameSession)
            _currentGameSession.value = gameSessionRepository.getGameSession(gameId)

            _currentTurn.value = turnOrder.first()

            // Create empty player states
            playerIds.forEach { playerId ->
                val playerState = PlayerState(
                    gameSessionId = gameId,
                    playerId = playerId,
                    hand = emptyList<Int>().toMutableList(),
                    discardPile = emptyList<Int>().toMutableList()
                )
                playerStateRepository.insertPlayerState(playerState)
            }

            val hands = deck.deal(playerIds)

            for ((playerId, card) in hands) {
                playerStateRepository.updatePlayerHand(playerId, listOf(card.id))
            }
        }
    }

    //player state
    private fun loadPlayerState(playerId: Long) {
        viewModelScope.launch {
            _playerState.value = playerStateRepository.getPlayerState(playerId)
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
