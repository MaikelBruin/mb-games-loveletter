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
import kotlinx.coroutines.flow.collectLatest
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

    private val _gameEnded = MutableStateFlow(false)
    val gameEnded: StateFlow<Boolean> = _gameEnded.asStateFlow()

    private val _playingCard = MutableStateFlow<CardType?>(null)
    val playingCard: StateFlow<CardType?> = _playingCard.asStateFlow()

    private val _cardTypes = MutableStateFlow<List<CardType>>(emptyList())
    val cardTypes: StateFlow<List<CardType>> = _cardTypes.asStateFlow()

    //all players
    private val _playersWithState = MutableStateFlow<List<PlayerWithGameState>>(emptyList())
    val playersWithState: StateFlow<List<PlayerWithGameState>> = _playersWithState.asStateFlow()

    private val _playerRoundStates = MutableStateFlow<Map<Long, PlayerRoundState>>(emptyMap())
    private val playerRoundStates: StateFlow<Map<Long, PlayerRoundState>> =
        _playerRoundStates.asStateFlow()

    private val _eligibleTargetPlayers = MutableStateFlow<List<PlayerRoundState>>(emptyList())
    val eligibleTargetPlayers: StateFlow<List<PlayerRoundState>> =
        _eligibleTargetPlayers.asStateFlow()

    private val _targetPlayer = MutableStateFlow<PlayerRoundState?>(null)
    val targetPlayer: StateFlow<PlayerRoundState?> = _targetPlayer.asStateFlow()

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

    private fun getCurrentPlayerWithGameState(): PlayerWithGameState {
        return playersWithState.value.find {
            currentTurn.value == it.player.id
        }!!
    }

    private fun getCurrentPlayerRoundState(): PlayerRoundState {
        return playerRoundStates.value.values.find {
            currentTurn.value == it.playerId
        }!!
    }

    private fun updatePlayerRoundState(playerId: Long, updatedState: PlayerRoundState) {
        _playerRoundStates.update { currentStates ->
            currentStates + (playerId to updatedState)
        }
    }

    suspend fun onStartNewRound(playerIds: List<Long>) {
        onAddActivity("Starting new round...")
        _roundEnded.value = false
        _playerRoundStates.value = playerIds.associateWith {
            PlayerRoundState(playerId = it)
        }

        val newTurnOrder = playerIds.shuffled() //TODO: base this on previous round winner
        _turnOrder.value = newTurnOrder
        _currentTurnIndex.value = 0
        _currentTurn.value = newTurnOrder[currentTurnIndex.value]

        _deck.value = Deck.createNewDeck()
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
        val card = deck.value.drawCard()
        _currentPlayerWithState.value.let { currentPlayerWithState ->
            onAddActivity("Starting turn for '${currentPlayerWithState!!.player.name}'...")
            onDealCardToPlayer(currentPlayerWithState.player.id, card!!.id)
            if (currentPlayerWithState.player.isHuman) {
                onAddActivity("Play a card")
            } else {
                onPlayCard(Cards.fromId(getPlayerRoundState(currentPlayerWithState.player.id).hand.random()))
            }
        }
    }

    /**
     * Should always be the current player
     */
    fun onPlayCard(card: Cards) {
        viewModelScope.launch {
            onDiscardCard(card, currentTurn.value)
            when (card.cardType) {
                CardType.Spy -> {
                    onAddActivity("Playing card: spy...")
                }

                CardType.Guard -> {
                    onAddActivity("Playing card: guard...")
                    onPlayGuard()
                }

                CardType.Priest -> {
                    onAddActivity("Playing card: priest...")
                }

                CardType.Baron -> {
                    onAddActivity("Playing card: baron...")
                }

                CardType.Handmaid -> {
                    onAddActivity("Playing card: handmaid...")
                    val updatedState =
                        getPlayerRoundState(currentTurn.value).copy(isProtected = true)
                    updatePlayerRoundState(currentTurn.value, updatedState)
                }

                CardType.Prince -> {
                    onAddActivity("Playing card: prince...")
                }

                CardType.Chancellor -> {
                    onAddActivity("Playing card: chancellor...")
                    onPlayChancellor()
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

            onEndTurn()
        }
    }

    fun showCardTypes(target: PlayerRoundState, playingCardType: CardType) {
        val targetName =
            playersWithState.value.find { it.player.id == target.playerId }!!.player.name
        onAddActivity("Target chosen: '${targetName}'")
        _eligibleTargetPlayers.value = emptyList()
        _targetPlayer.value = target
        when (playingCardType) {
            CardType.Guard -> {
                onAddActivity("Choose a non-guard card:")
                _cardTypes.value = CardType.entries.filter { it != CardType.Guard }
            }

            CardType.Priest -> {
                onAddActivity("Discarding card: priest...")
            }

            CardType.Baron -> {
                onAddActivity("Discarding card: baron...")
            }

            CardType.Prince -> {
                onAddActivity("Discarding card: prince...")
            }

            CardType.King -> {
                onAddActivity("Discarding card: king...")
            }

            else -> {}
        }

    }

    fun onGuessHand(cardType: CardType) {
        targetPlayer.value?.let {
            val targetPlayerWithGameState =
                playersWithState.value.find { it.player.id == targetPlayer.value!!.playerId }!!
            onAddActivity("Guessing that player '${targetPlayerWithGameState.player.name}' has cardType '${cardType.card.name}'")
            if (Cards.fromId(targetPlayer.value!!.hand[0]).cardType == cardType) {
                onAddActivity("Correct guess! Eliminating player '${targetPlayerWithGameState.player.name}'...")
                eliminatePlayer(targetPlayer.value!!.playerId)
            } else {
                onAddActivity("Incorrect guess, '${targetPlayerWithGameState.player.name}' is still alive and kicking...")
            }
        }

        //restore state after playing guard
        onAddActivity("Finished playing guard...")
        _cardTypes.value = emptyList()
        _targetPlayer.value = null
        _playingCard.value = null
    }

    private suspend fun onPlayGuard() {
        val eligibleTargets = getEligiblePlayersForCardEffect(currentTurn.value, CardType.Guard)
        if (eligibleTargets.isEmpty()) {
            onAddActivity("Cannot play guard, no eligible targets.")
            return
        }

        val currentPlayerGameState = getCurrentPlayerWithGameState()
        if (currentPlayerGameState.player.isHuman) {
            _playingCard.value = CardType.Guard
            _eligibleTargetPlayers.value = eligibleTargets
            //FIXME: it looks like this collectLatest method is never returning
            playingCard.collectLatest { playingCard ->
                if (playingCard == null || (_eligibleTargetPlayers.value.isEmpty() && _targetPlayer.value == null)) {
                    return@collectLatest
                }
            }
        } else {
            val target = eligibleTargets.random()
            showCardTypes(target, CardType.Guard)
            onGuessHand(CardType.entries.toTypedArray().random())
        }

        //FIXME: we need to correctly update the game state when a human player plays the guard, currently the game freezes after guessing a hand
        onAddActivity("end of onPlayGuard")

    }

    private suspend fun onPlayChancellor() {
        val firstCard = deck.value.drawCard()
        val secondCard = deck.value.drawCard()
        var currentPlayerRoundState: PlayerRoundState = getCurrentPlayerRoundState()
        val currentPlayerGameState = getCurrentPlayerWithGameState()
        var remainingHand: List<Int> = currentPlayerRoundState.hand
        val cardIdsToReturn =
            emptyList<Int>().toMutableList()
        if (firstCard == null) {
            onAddActivity("Deck is empty so chancellor has no effect...")
        } else if (secondCard == null) {
            onAddActivity("Can only draw one card because deck is empty...")
            onDealCardToPlayer(currentPlayerGameState.player.id, firstCard.id)
            currentPlayerRoundState = getCurrentPlayerRoundState()
            if (currentPlayerGameState.player.isHuman) {
                _playingCard.value = CardType.Chancellor
                playingCard.collectLatest { playingCard ->
                    if (playingCard == null || getCurrentPlayerRoundState().hand.size <= 1) {
                        return@collectLatest
                    }
                }
            } else {
                val shuffled = currentPlayerRoundState.hand.shuffled()
                cardIdsToReturn.addAll(shuffled.take(1))
                remainingHand = shuffled.drop(1)
            }
        } else {
            //normal case for chancellor
            onDealCardToPlayer(currentPlayerGameState.player.id, firstCard.id)
            onDealCardToPlayer(currentPlayerGameState.player.id, secondCard.id)
            currentPlayerRoundState = getCurrentPlayerRoundState()
            if (currentPlayerGameState.player.isHuman) {
                _playingCard.value = CardType.Chancellor
                playingCard.collectLatest { playingCard ->
                    if (playingCard == null || getCurrentPlayerRoundState().hand.size <= 1) {
                        return@collectLatest
                    }
                }
            } else {
                val shuffled = currentPlayerRoundState.hand.shuffled()
                cardIdsToReturn.addAll(shuffled.take(2))
                remainingHand = shuffled.drop(2)
            }
        }
        //FIXME: this code does not get executed when a human player plays the chancellor

        _playingCard.value = null

        val updatedState =
            getPlayerRoundState(currentTurn.value).copy(hand = remainingHand)
        updatePlayerRoundState(currentTurn.value, updatedState)
        onAddActivity("end of onPlayChancellor")

    }

    private fun getEligiblePlayersForCardEffect(
        executingPlayerId: Long,
        cardType: CardType
    ): List<PlayerRoundState> {
        var result = playerRoundStates.value.values.filter {
            !it.isProtected && it.isAlive
        }
        if (cardType != CardType.Prince) {
            result = result.filter { it.playerId != executingPlayerId }
        }
        return result
    }

    /**
     * Can be any player
     */
    private fun onDiscardCard(card: Cards, executingPlayerId: Long) {
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
                eliminatePlayer(executingPlayerId)
            }
        }

        //update hand and discard pile
        val currentState = getPlayerRoundState(executingPlayerId)
        val updatedState = currentState.copy(
            hand = currentState.hand - card.id,
            discardPile = currentState.discardPile + card.id
        )
        updatePlayerRoundState(executingPlayerId, updatedState)

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
                onAddActivity("Ending turn for player '${getCurrentPlayerWithGameState().player.name}'...")
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
                onAddActivity(
                    "Final cards: " + playerRoundStates.value.values
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
            loadActivePlayersWithState(gameSession.id) //update needed for determining game end
            val gameWinners = playersWithState.value.filter {
                it.playerGameState.favorTokens >= gameSession.tokensToWin
            }

            if (gameWinners.isNotEmpty()) {
                onEndGame(gameWinners)
                return@launch
            }

            //if game has not ended, update game state and show button to start next round
            val updatedGameSession = gameSession.copy(currentRound = gameSession.currentRound++)
            gameSessionRepository.updateGameSession(updatedGameSession)
            //TODO: set turn order based on round winner
        }
    }

    private fun onEndGame(winners: List<PlayerWithGameState>) {
        viewModelScope.launch {
            val winnerNames = winners.map { it.player.name }
            onAddActivity("Game has ended! Winner(s): $winnerNames")
            _gameEnded.value = true
            winners.forEach {
                val player = playerRepository.getPlayerByIdSuspend(it.player.id)
                val updatedPlayer = player.copy(wins = player.wins++)
                playerRepository.updatePlayer(updatedPlayer)
            }
            playersWithState.value.forEach {
                val player = playerRepository.getPlayerByIdSuspend(it.player.id)
                val updatedPlayer = player.copy(plays = player.plays++)
                playerRepository.updatePlayer(updatedPlayer)
            }

            activeGameSession.value?.let { session ->
                gameSessionRepository.updateGameSession(session.copy(isActive = false))
            }
            _activeGameSession.value = null
        }

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

    fun onChancellorReturnCardToDeck(playerId: Long, cardToReturn: Int) {
        onAddActivity("Returning card '${cardToReturn}' to deck for player '${playerId}'...")
        _playerRoundStates.update { states ->
            states.mapValues { (id, state) ->
                if (id == playerId) state.copy(hand = state.hand - cardToReturn) else state
            }
        }

        deck.value.returnCardToBottomOfDeck(Cards.fromId(cardToReturn))
        if (getCurrentPlayerRoundState().hand.size == 1) {
            onAddActivity("Done playing chancellor")
            _playingCard.value = null
            onEndTurn()
        }
    }

    //Game sessions
    suspend fun onStartNewGame(playerIds: List<Long>) {
        _gameEnded.value = false
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