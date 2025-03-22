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
import mb.games.loveletter.data.PlayerWithState

class GameViewModel(
    private val playerRepository: PlayerRepository = Graph.playerRepository,
    private val playerStateRepository: PlayerStateRepository = Graph.playerStateRepository,
    private val gameSessionRepository: GameSessionRepository = Graph.gameSessionRepository
) : ViewModel() {

    //STATES
    private val _currentTurn = mutableLongStateOf(0)
    val currentTurn: State<Long> = _currentTurn

    private val _currentPlayer = mutableStateOf<Player?>(null)
    val currentPlayer: State<Player?> = _currentPlayer

    var playerNameState by mutableStateOf("")
    var isHumanState by mutableStateOf(false)

    //STATE FLOWS
    private val _currentPlayerWithState = MutableStateFlow<PlayerWithState?>(null)
    val currentPlayerWithState: StateFlow<PlayerWithState?> = _currentPlayerWithState.asStateFlow()

    private val _activeGameSession = MutableStateFlow<GameSession?>(null)
    val activeGameSession: StateFlow<GameSession?> = _activeGameSession.asStateFlow()

    private val _humanPlayerWithState = MutableStateFlow<PlayerWithState?>(null)
    val humanPlayerWithState: StateFlow<PlayerWithState?> = _humanPlayerWithState.asStateFlow()

    private val _deck = MutableStateFlow(Deck.createNewDeck())
    val deck: StateFlow<Deck> = _deck.asStateFlow()

    fun onPlayerNameChanged(newName: String) {
        playerNameState = newName
    }

    fun onPlayerIsHumanChanged(isHuman: Boolean) {
        isHumanState = isHuman
    }

    fun onCurrentTurnChanged(currentTurn: Long) {
        _currentTurn.longValue = currentTurn
    }

    fun onCurrentPlayerChanged(player: Player) {
        _currentPlayer.value = player
    }

    fun onHumanPlayerWithStateChanged(playerWithState: PlayerWithState) {
        _humanPlayerWithState.value = playerWithState
    }

    fun onCurrentPlayerWithStateChanged(playerWithState: PlayerWithState) {
        _currentPlayerWithState.value = playerWithState
    }

    lateinit var getAllPlayers: Flow<List<Player>>
    private lateinit var getAllGameSessions: Flow<List<GameSession>>

    init {
        viewModelScope.launch {
            getAllPlayers = playerRepository.getPlayers()
            getAllGameSessions = gameSessionRepository.getGameSessions()
            if (getAllPlayers.first().isEmpty()) {
                insertDefaultPlayers()
            }

            _activeGameSession.value = gameSessionRepository.getActiveGameSessionSuspend()
            if (activeGameSession.value != null) {
                loadCurrentGameState()
            }
        }
    }

    //FLOW FUNCTIONS
    fun getAPlayerById(id: Long): Flow<Player> {
        return playerRepository.getPlayerById(id)
    }

    //VIEW MODEL SCOPE FUNCTIONS
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

    fun deletePlayer(player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            playerRepository.deletePlayer(player = player)
            getAllPlayers = playerRepository.getPlayers()
        }
    }

    //player with state
    fun onStartTurn() {
        viewModelScope.launch {
            val card = _deck.value.drawCard()
            if (card == null) {
                onEndRound()
            } else {
                _currentPlayerWithState.value.let { currentPlayerWithState ->
                    val updatedState = currentPlayerWithState!!.copy()
                    updatedState.playerState.hand.add(card.id)
                    onCurrentPlayerWithStateChanged(updatedState)
                    if (currentPlayerWithState.player.isHuman) {
                        onHumanPlayerWithStateChanged(updatedState)
                        println("Play a card")
                    } else {
                        println("Computer should play a card")
                    }
                    println("Number of cards in my hand: '${humanPlayerWithState.value!!.playerState.hand.size}'")
                }

            }
        }
    }

    private fun onEndRound() {
        println("Round ended!")
    }

    //Game sessions
    fun onStartNewGame(playerIds: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            _deck.value = Deck.createNewDeck()
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

            //update state
            _activeGameSession.value = gameSession
            loadCurrentGameState()
        }
    }

    //SUSPEND FUNCTIONS
    //players
    private suspend fun insertDefaultPlayers() {
        val defaultPlayers = listOf(
            Player(name = "MB", isHuman = true), Player(name = "Bot 1"), Player(name = "Bot 2")
        )
        defaultPlayers.forEach { player ->
            playerRepository.addPlayer(player)
        }
    }

    //game sessions
    private suspend fun loadCurrentGameState() {
        val currentTurnId = activeGameSession.value!!.turnOrder.first()
        val currentPlayer = playerRepository.getPlayerByIdSuspend(currentTurnId)
        val currentPlayerWithState = playerRepository.getPlayerWithState(currentTurnId)
        val humanPlayerState = playerRepository.getHumanPlayerWithState()

        onCurrentTurnChanged(currentTurnId)
        onCurrentPlayerChanged(currentPlayer)
        onHumanPlayerWithStateChanged(humanPlayerState)
        onCurrentPlayerWithStateChanged(currentPlayerWithState)
        onStartTurn()
    }

    //UTILITY FUNCTIONS
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