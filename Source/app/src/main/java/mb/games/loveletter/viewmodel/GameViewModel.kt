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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mb.games.loveletter.Graph
import mb.games.loveletter.data.CardType
import mb.games.loveletter.data.Cards
import mb.games.loveletter.data.Deck
import mb.games.loveletter.data.GameSession
import mb.games.loveletter.data.GameSessionRepository
import mb.games.loveletter.data.PlayerRepository
import mb.games.loveletter.data.Player
import mb.games.loveletter.data.PlayerRoundState
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
    private val currentTurn: State<Long> = _currentTurn

    private val _currentPlayer = mutableStateOf<Player?>(null)
    val currentPlayer: State<Player?> = _currentPlayer

    var playerNameState by mutableStateOf("")
    var isHumanState by mutableStateOf(false)

    //STATE FLOWS
    private val _currentPlayerWithState = MutableStateFlow<PlayerWithState?>(null)
    private val currentPlayerWithState: StateFlow<PlayerWithState?> =
        _currentPlayerWithState.asStateFlow()

    private val _activeGameSession = MutableStateFlow<GameSession?>(null)
    val activeGameSession: StateFlow<GameSession?> = _activeGameSession.asStateFlow()

    private val _humanPlayerWithState = MutableStateFlow<PlayerWithState?>(null)
    val humanPlayerWithState: StateFlow<PlayerWithState?> = _humanPlayerWithState.asStateFlow()

    private val _deck = MutableStateFlow(Deck.createNewDeck())
    val deck: StateFlow<Deck> = _deck.asStateFlow()

    private val _playersWithState = MutableStateFlow<List<PlayerWithState>>(emptyList())
    private val playersWithState: StateFlow<List<PlayerWithState>> = _playersWithState.asStateFlow()

    private val _playerRoundStates = MutableStateFlow<Map<Long, PlayerRoundState>>(emptyMap())
    private val playerRoundStates: StateFlow<Map<Long, PlayerRoundState>> = _playerRoundStates.asStateFlow()

    // Derived StateFlow for the human player's state
    val humanPlayerRoundState: StateFlow<PlayerRoundState?> =
        combine(_playerRoundStates, _humanPlayerWithState) { states, humanPlayerWithState ->
            humanPlayerWithState?.let { states[it.player.id] }  // Get human player's state if the ID exists
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun onPlayerNameChanged(newName: String) {
        playerNameState = newName
    }

    fun onPlayerIsHumanChanged(isHuman: Boolean) {
        isHumanState = isHuman
    }

    private fun onCurrentTurnChanged(currentTurn: Long) {
        _currentTurn.longValue = currentTurn
    }

    private fun onCurrentPlayerChanged(player: Player) {
        _currentPlayer.value = player
    }

    private fun onHumanPlayerWithStateChanged(playerWithState: PlayerWithState) {
        _humanPlayerWithState.value = playerWithState
    }

    private fun onCurrentPlayerWithStateChanged(playerWithState: PlayerWithState) {
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
    private fun loadActivePlayersWithState(gameSessionId: Long) {
        viewModelScope.launch {
            playerRepository.getActivePlayersWithState(gameSessionId)
                .collect { _playersWithState.value = it }
        }
    }

    private fun onStartNewRound(playerIds: List<Long>) {
        println("Starting new round...")
        _playerRoundStates.value = playerIds.associateWith {
            PlayerRoundState()
        }

        val hands = deck.value.deal(playerIds)
        for ((playerId, card) in hands) {
            dealCardToPlayer(playerId, card.id)
        }
    }

    private fun onStartTurn() {
        viewModelScope.launch {
            val card = _deck.value.drawCard()
            if (card == null) {
                onEndRound()
            } else {
                _currentPlayerWithState.value.let { currentPlayerWithState ->
                    println("Starting turn for '${_currentPlayer.value?.name}'...")
                    dealCardToPlayer(currentPlayerWithState!!.player.id, card.id)
                    if (currentPlayerWithState.player.isHuman) {
                        println("Play a card")
                    } else {
                        onPlayCard(Cards.fromId(getPlayerRoundState(currentPlayerWithState.player.id).hand.random()))
                    }
                }
            }
        }
    }

    /**
     * Should always be the current player
     */
    fun onPlayCard(card: Cards, targetPlayer: PlayerWithState? = null) {
        viewModelScope.launch {
            when (card.cardType) {
                CardType.Spy -> {
                    println("Playing card: spy...")
                }

                CardType.Guard -> {
                    println("Playing card: guard...")
                }

                CardType.Priest -> {
                    println("Playing card: priest...")
                }

                CardType.Baron -> {
                    println("Playing card: baron...")
                }

                CardType.Handmaid -> {
                    println("Playing card: handmaid...")
                }

                CardType.Prince -> {
                    println("Playing card: prince...")
                }

                CardType.Chancellor -> {
                    println("Playing card: chancellor...")
                }

                CardType.King -> {
                    println("Playing card: king...")
                }

                CardType.Countess -> {
                    println("Playing card: countess...")
                }

                CardType.Princess -> {
                    println("Playing card: princess...")
                }
            }

            onDiscardCard(card, currentPlayerWithState.value!!)

            onEndTurn()
        }
    }

    /**
     * Can be any player
     */
    private fun onDiscardCard(card: Cards, executingPlayer: PlayerWithState) {
        viewModelScope.launch {
            when (card.cardType) {
                CardType.Spy -> {
                    println("Discarding card: spy...")
                }

                CardType.Guard -> {
                    println("Discarding card: guard...")
                }

                CardType.Priest -> {
                    println("Discarding card: priest...")
                }

                CardType.Baron -> {
                    println("Discarding card: baron...")
                }

                CardType.Handmaid -> {
                    println("Discarding card: handmaid...")
                }

                CardType.Prince -> {
                    println("Discarding card: prince...")
                }

                CardType.Chancellor -> {
                    println("Discarding card: chancellor...")
                }

                CardType.King -> {
                    println("Discarding card: king...")
                }

                CardType.Countess -> {
                    println("Discarding card: countess...")
                }

                CardType.Princess -> {
                    println("Discarding card: princess...")
                }
            }

            //TODO: remove card from hand
//            val updatedState = executingPlayer.copy()
//            updatedState.playerState.hand.indexOf(find { id -> id == card.id }!!)
//            onCurrentPlayerWithStateChanged(updatedState)

        }
    }

    private fun onEndTurn() {
        viewModelScope.launch {
            println("Ending turn for player '${_currentPlayer.value?.name}'...")
            val activeGameSession = _activeGameSession.value!!
            val currentTurnIndex = activeGameSession.turnOrder.indexOf(currentTurn.value)
            val nextTurnIndex = currentTurnIndex + 1
            val nextPlayerId: Long = try {
                activeGameSession.turnOrder[nextTurnIndex]
            } catch (e: IndexOutOfBoundsException) {
                activeGameSession.turnOrder[0]
            }

            val nextPlayer = playerRepository.getPlayerByIdSuspend(nextPlayerId)
            val nextPlayerWithState = playerRepository.getPlayerWithState(nextPlayerId)
            onCurrentTurnChanged(nextPlayerId)
            onCurrentPlayerChanged(nextPlayer)
            onCurrentPlayerWithStateChanged(nextPlayerWithState)
            onStartTurn()
        }
    }

    private fun onEndRound() {
        println("Round ended!")
    }

    //player round states
    fun getPlayerRoundState(playerId: Long): PlayerRoundState {
        return playerRoundStates.value[playerId]!!
    }

    private fun dealCardToPlayer(playerId: Long, newCard: Int) {
        _playerRoundStates.update { states ->
            states.mapValues { (id, state) ->
                if (id == playerId) state.copy(hand = state.hand + newCard) else state
            }
        }
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

            // Create player states in db
            // and create player round states in memory
            playerIds.forEach { playerId ->
                val playerState = PlayerState(
                    gameSessionId = gameSession.id, playerId = playerId
                )
                playerStateRepository.insertPlayerState(playerState)
            }

            //update state
            _activeGameSession.value = gameSession
            loadCurrentGameState()
            loadActivePlayersWithState(gameSession.id)

            //start round
            onStartNewRound(playerIds)
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