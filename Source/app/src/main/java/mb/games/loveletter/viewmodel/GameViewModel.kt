package mb.games.loveletter.viewmodel

import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.flow.filterNotNull
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
import mb.games.loveletter.data.PlayerGameState
import mb.games.loveletter.data.PlayerGameStateRepository
import mb.games.loveletter.data.PlayerWithGameState

class GameViewModel(
    private val playerRepository: PlayerRepository = Graph.playerRepository,
    private val playerStateRepository: PlayerGameStateRepository = Graph.playerStateRepository,
    private val gameSessionRepository: GameSessionRepository = Graph.gameSessionRepository
) : ViewModel() {

    //STATES
    //new game screen
    var playerNameState by mutableStateOf("")
    var isHumanState by mutableStateOf(false)

    //game screen
    private val _activeGameSession = MutableStateFlow<GameSession?>(null)
    val activeGameSession: StateFlow<GameSession?> = _activeGameSession.asStateFlow()

    private val _deck = MutableStateFlow(Deck.createNewDeck())
    val deck: StateFlow<Deck> = _deck.asStateFlow()

    private val _activities = MutableStateFlow<List<String>>(emptyList())
    val activities: StateFlow<List<String>> = _activities.asStateFlow()

    private val _roundEnded = MutableStateFlow(false)
    val roundEnded: StateFlow<Boolean> = _roundEnded.asStateFlow()

    //all players
    private val _playersWithState = MutableStateFlow<List<PlayerWithGameState>>(emptyList())
    val playersWithState: StateFlow<List<PlayerWithGameState>> = _playersWithState.asStateFlow()

    private val _playerRoundStates = MutableStateFlow<Map<Long, PlayerRoundState>>(emptyMap())
    private val playerRoundStates: StateFlow<Map<Long, PlayerRoundState>> =
        _playerRoundStates.asStateFlow()

    //current turn / player
    private val _turnOrder = MutableStateFlow<List<Long>>(emptyList())
    val turnOrder: StateFlow<List<Long>> = _turnOrder.asStateFlow()

    private val _currentTurnIndex = MutableStateFlow(0)
    private val currentTurnIndex: StateFlow<Int> = _currentTurnIndex.asStateFlow()

    private val _currentTurn = MutableStateFlow<Long>(0)
    private val currentTurn: StateFlow<Long> = _currentTurn.asStateFlow()

    private val _currentPlayerWithState = MutableStateFlow<PlayerWithGameState?>(null)
    val currentPlayerWithState: StateFlow<PlayerWithGameState?> =
        _currentPlayerWithState.asStateFlow()

    //human player
    private val _humanPlayerWithState = MutableStateFlow<PlayerWithGameState?>(null)
    val humanPlayerWithState: StateFlow<PlayerWithGameState?> = _humanPlayerWithState.asStateFlow()

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

    fun onAddActivity(activity: String) {
        println(activity)
        val currentActivities = _activities.value
        _activities.value = currentActivities + activity
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
                onStartNewRound(activeGameSession.value!!.playerIds)
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

    private suspend fun onStartNewRound(playerIds: List<Long>) {
        onAddActivity("Starting new round...")
        _roundEnded.value = false
        _playerRoundStates.value = playerIds.associateWith {
            PlayerRoundState(playerId = it)
        }

        val newTurnOrder = playerIds.shuffled()
        _turnOrder.value = newTurnOrder
        _currentTurnIndex.value = 0
        _currentTurn.value = newTurnOrder[currentTurnIndex.value]

        val hands = deck.value.deal(newTurnOrder)
        for ((playerId, card) in hands) {
            onDealCardToPlayer(playerId, card.id)
        }

        loadActivePlayersWithState(activeGameSession.value!!.id)
        val newPlayerWithState = playerRepository.getPlayerWithState(currentTurn.value)
        val newHumanPlayerWithState = playerRepository.getHumanPlayerWithState()

        //update state
        _humanPlayerWithState.value = newHumanPlayerWithState
        _currentPlayerWithState.value = newPlayerWithState
        onStartTurn()

    }

    private fun onStartTurn() {
        viewModelScope.launch {
            val card = deck.value.drawCard()
            currentPlayerWithState.value.let { currentPlayerWithState ->
                val message = "Starting turn for '${currentPlayerWithState!!.player.name}'..."
                onAddActivity(message)
                onDealCardToPlayer(currentPlayerWithState.player.id, card!!.id)
                if (currentPlayerWithState.player.isHuman) {
                    onAddActivity("Play a card")
                } else {
                    onPlayCard(Cards.fromId(getPlayerRoundState(currentPlayerWithState.player.id).hand.random()))
                }
            }
        }
    }

    /**
     * Should always be the current player
     */
    fun onPlayCard(card: Cards, targetPlayer: PlayerWithGameState? = null) {
        viewModelScope.launch {
            when (card.cardType) {
                CardType.Spy -> {
                    onAddActivity("Playing card: spy...")
                }

                CardType.Guard -> {
                    onAddActivity("Playing card: guard...")
                }

                CardType.Priest -> {
                    onAddActivity("Playing card: priest...")
                }

                CardType.Baron -> {
                    onAddActivity("Playing card: baron...")
                }

                CardType.Handmaid -> {
                    onAddActivity("Playing card: handmaid...")
                }

                CardType.Prince -> {
                    onAddActivity("Playing card: prince...")
                }

                CardType.Chancellor -> {
                    onAddActivity("Playing card: chancellor...")
                }

                CardType.King -> {
                    onAddActivity("Playing card: king...")
                }

                CardType.Countess -> {
                    onAddActivity("Playing card: countess...")
                }

                CardType.Princess -> {
                    onAddActivity("Playing card: princess...")
                    eliminatePlayer(currentTurn.value)
                }
            }

            onDiscardCard(card, _currentPlayerWithState.value!!)
            onEndTurn()
        }
    }

    /**
     * Can be any player
     */
    private fun onDiscardCard(card: Cards, executingPlayer: PlayerWithGameState) {
        viewModelScope.launch {
            when (card.cardType) {
                CardType.Spy -> {
                    onAddActivity("Discarding card: spy...")
                }

                CardType.Guard -> {
                    onAddActivity("Discarding card: guard...")
                }

                CardType.Priest -> {
                    onAddActivity("Discarding card: priest...")
                }

                CardType.Baron -> {
                    onAddActivity("Discarding card: baron...")
                }

                CardType.Handmaid -> {
                    onAddActivity("Discarding card: handmaid...")
                }

                CardType.Prince -> {
                    onAddActivity("Discarding card: prince...")
                }

                CardType.Chancellor -> {
                    onAddActivity("Discarding card: chancellor...")
                }

                CardType.King -> {
                    onAddActivity("Discarding card: king...")
                }

                CardType.Countess -> {
                    onAddActivity("Discarding card: countess...")
                }

                CardType.Princess -> {
                    onAddActivity("Discarding card: princess...")
                    eliminatePlayer(executingPlayer.player.id)
                }
            }

            //update hand and discard pile
            _playerRoundStates.update { states ->
                states.mapValues { (id, state) ->
                    if (id == executingPlayer.player.id) state.copy(
                        hand = state.hand - card.id, discardPile = state.discardPile + card.id
                    ) else state
                }
            }

        }
    }

    private fun eliminatePlayer(playerId: Long) {
        _playerRoundStates.update { states ->
            states.mapValues { (id, state) ->
                if (id == playerId) state.copy(isAlive = false) else state
            }
        }

        val newOrder = _turnOrder.value.filter { it != playerId }
        _turnOrder.value = newOrder
        _currentTurnIndex.value = 0.coerceAtMost(newOrder.lastIndex)
    }

    private fun onEndTurn() {
        viewModelScope.launch {
            if (roundEnded()) {
                return@launch
            } else {
                onAddActivity("Ending turn for player '${currentPlayerWithState.value?.player?.name}'...")
                val order = _turnOrder.value
                if (order.isNotEmpty()) {
                    val nextIndex = (_currentTurnIndex.value + 1) % order.size
                    _currentTurnIndex.value = nextIndex
                    _currentTurn.value = order[nextIndex]
                }

                val nextPlayerId = currentTurn.filterNotNull().first()
                val nextPlayerWithState = playerRepository.getPlayerWithState(nextPlayerId)
                _currentPlayerWithState.value = nextPlayerWithState
                onStartTurn()
            }

        }
    }

    private fun roundEnded(): Boolean {
        val onlyOneAlive = playerRoundStates.value.values.count { it.isAlive } == 1
        val deckIsEmpty = deck.value.getCards().isEmpty()

        return if (onlyOneAlive || deckIsEmpty) {
            onAddActivity("Round ended: ${if (onlyOneAlive) "Only one player is alive" else "Deck is empty"}")
            onEndRound()
            true
        } else {
            false
        }
    }

    private fun onEndRound() {
        viewModelScope.launch {
            onAddActivity("Round ended!")
            _roundEnded.value = true
            val roundWinners = mutableListOf<Long>()

            //determine winners
            if (turnOrder.value.size == 1) {
                val roundWinner = turnOrder.value[0]
                roundWinners.add(roundWinner)
                onAddActivity("Player with id '$roundWinner' wins! All other players are eliminated.")
            } else {
                val winningPlayer = playerRoundStates.value.values
                    .filter { it.isAlive }
                    .maxByOrNull { playerRoundState -> Cards.fromId(playerRoundState.hand[0]).cardType.card.value }!!
                val winningPlayerIds =
                    playerRoundStates.value.values
                        .filter { it.hand[0] == winningPlayer.hand[0] }
                        .map { it.playerId }
                roundWinners.addAll(winningPlayerIds)
                val winningPlayersNames =
                    playersWithState.value.filter { winningPlayerIds.contains(it.player.id) }
                onAddActivity(
                    "Player(s) '${winningPlayersNames.map { it.player.name }}' win(s) with card '${
                        Cards.fromId(winningPlayer.hand[0]).cardType.card.name
                    }'"
                )
                onAddActivity("Final cards: " + playerRoundStates.value.values
                    .filter { it.isAlive }
                    .map {
                        Cards.fromId(it.hand[0]).cardType.card.name
                    })
            }

            //call out round winners
            val roundWinnerNames = playersWithState.value
                .filter { roundWinners.contains(it.player.id) }
                .map { it.player.name }
            onAddActivity("Round winner(s): '${roundWinnerNames}'")

            //call out spy winner
            val spyWinner = determineSpyWinner(playerRoundStates.value)
            if (spyWinner != null) {
                val spyWinnerName =
                    playersWithState.value.find { it.player.id == spyWinner.playerId }!!.player.name
                onAddActivity("Spy winner: '${spyWinnerName}'")
                roundWinners.add(spyWinner.playerId)
            }

            //deal out token to round winner(s) and spy winner
            roundWinners.forEach { roundWinner ->
                val playerGameState = playerRepository.getPlayerWithState(
                    roundWinner, activeGameSession.value!!.id
                ).playerGameState
                val newPlayerGameState =
                    playerGameState.copy(favorTokens = playerGameState.favorTokens + 1)
                playerStateRepository.updatePlayerState(newPlayerGameState)
            }

            //check if game has ended
            val gameSession = activeGameSession.value!!
            val gameWinners = playersWithState.value.filter {
                it.playerGameState.favorTokens == gameSession.tokensToWin
            }

            if (gameWinners.isNotEmpty()) {
                onEndGame(gameWinners)
                return@launch
            }

            //if game has not ended, update game state and show button to start next round
            val updatedGameSession = gameSession.copy(currentRound = gameSession.currentRound++)
            //TODO: set turn order based on round winner
        }
    }

    private fun onEndGame(winners: List<PlayerWithGameState>) {
        onAddActivity("Game has ended!")
        //update game session
        //set gameSession isActive to false
        //update player objects (wins, plays, game session id)
    }

    private fun determineSpyWinner(playerRoundStates: Map<Long, PlayerRoundState>): PlayerRoundState? {
        val spyCards = listOf(Cards.Spy1.id, Cards.Spy2.id)
        val hasASpyCard = playerRoundStates.values.filter { playerRoundState ->
            playerRoundState.isAlive && spyCards.any { playerRoundState.discardPile.contains(it) }
        }
        return if (hasASpyCard.size == 1) {
            hasASpyCard.first()
        } else null
    }

    //player round states
    private fun getPlayerRoundState(playerId: Long): PlayerRoundState {
        return _playerRoundStates.value[playerId]!!
    }

    private fun onDealCardToPlayer(playerId: Long, newCard: Int) {
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
            _activities.value = emptyList()
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
                val playerState = PlayerGameState(
                    gameSessionId = gameSession.id, playerId = playerId
                )
                playerStateRepository.insertPlayerState(playerState)
            }

            //update state
            _activeGameSession.value = gameSession

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