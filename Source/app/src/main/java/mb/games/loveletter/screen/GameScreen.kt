package mb.games.loveletter.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mb.games.loveletter.data.Cards
import mb.games.loveletter.data.Player
import mb.games.loveletter.data.exitGameMenuItem
import mb.games.loveletter.data.newRoundMenuItem
import mb.games.loveletter.ui.theme.Bordeaux
import mb.games.loveletter.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onExitGame: () -> Unit
) {
    GameView(viewModel, onExitGame)
}

@Composable
fun GameView(
    viewModel: GameViewModel,
    onExitGame: () -> Unit
) {
    val playersWithGameState by viewModel.playersWithState.collectAsState()
    val currentPlayerWithState by viewModel.currentPlayerWithState.collectAsState()
    val deck by viewModel.deck.collectAsState()
    val activeGameSession by viewModel.activeGameSession.collectAsState()
    val humanPlayerRoundState by viewModel.humanPlayerRoundState.collectAsState()
    val turnOrder by viewModel.turnOrder.collectAsState()
    val activities by viewModel.activities.collectAsState()
    val activitiesListState = rememberLazyListState()
    val roundEnded by viewModel.roundEnded.collectAsState()
    val gameEnded by viewModel.gameEnded.collectAsState()
    val playingChancellor by viewModel.playingChancellor.collectAsState()
    val eligibleTargets by viewModel.eligibleTargetPlayers.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Bordeaux)
    ) {
        //left
        Column(
            modifier = Modifier.fillMaxWidth(fraction = 0.5F),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            //top left, should display card play options
            Row(
                modifier = Modifier.fillMaxHeight(0.5F),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start
            ) {
                Column {
                    Row {
                        val tokensByName =
                            playersWithGameState.map { "${it.player.name}: ${it.playerGameState.favorTokens}" }
                        var tokensToWin = 0 //FIXME: I break when game ends
                        if (activeGameSession != null)
                            tokensToWin = activeGameSession!!.tokensToWin
                        Text(
                            text = "Favor tokens: $tokensByName, tokens to win: $tokensToWin"
                        )
                    }
                    Row {
                        Text(
                            text = "Should show card play options"
                        )
                    }
                    Row {
                        val eligibleTargetIds = eligibleTargets.map { it.playerId }
                        val eligibleTargetNames = playersWithGameState
                            .filter { eligibleTargetIds.contains(it.player.id) }
                            .map { it.player.name }
                        LazyColumn {
                            items(eligibleTargets) {
                                val player = viewModel.getAPlayerById(it.playerId).collectAsState(
                                    initial = Player(name = "")
                                )
                                Text(modifier = Modifier.clickable {
                                    viewModel.onChooseTarget(it)
                                }, text = player.value.name)
                            }
                        }
                    }
                    if (gameEnded) {
                        MenuItemView(
                            menuItem = exitGameMenuItem,
                            onClick = { onExitGame() })
                    } else if (roundEnded) {
                        MenuItemView(
                            menuItem = newRoundMenuItem,
                            onClick = {
                                viewModel.viewModelScope.launch {
                                    viewModel.onStartNewRound(activeGameSession!!.playerIds)
                                }
                            })
                    }
                }
            }
            //bottom left, should display own cards
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Start
            ) {

                Column {
                    var cardsInHand = emptyList<Cards>()
                    if (humanPlayerRoundState != null) {
                        val hand = humanPlayerRoundState!!.hand
                        cardsInHand = Cards.fromIds(hand)
                    }
                    Row {
                        Text(text = "My card(s): ")
                        Text(text = cardsInHand.map { card -> card.cardType.name }.toString())
                    }

                    LazyRow {
                        items(cardsInHand) { card ->
                            CardItemView(card = card, onClick = {
                                if (roundEnded) {
                                    viewModel.onAddActivity("Cannot play card, round has ended")
                                } else {
                                    if (playingChancellor) {
                                        viewModel.onChancellorReturnCardToDeck(
                                            humanPlayerRoundState!!.playerId,
                                            card.id
                                        )
                                    } else {
                                        viewModel.onPlayCard(card)
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }
        //right
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Top,
        ) {
            //top right, should show deck information and turn order
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.End
            ) {
                val cardsRemaining = deck.getCards().size
                val turnOrderNames =
                    turnOrder.map { turn -> playersWithGameState.find { playerWithState -> playerWithState.player.id == turn }?.player?.name }

                Column {
                    Row {
                        Text(text = "cards remaining in deck: $cardsRemaining")
                    }
                    Row {
                        Text(
                            text = "turn order: $turnOrderNames, Current turn: ${currentPlayerWithState?.player?.name}"
                        )
                    }
                    LaunchedEffect(activities) {
                        activitiesListState.animateScrollToItem(activitiesListState.layoutInfo.totalItemsCount)
                    }
                    Row {
                        LazyColumn(
                            modifier = Modifier.background(Color.Yellow),
                            userScrollEnabled = true,
                            state = activitiesListState
                        ) {
                            items(activities) { activity ->
                                Text(text = activity)
                            }
                        }
                    }
                }
            }
        }
    }
}

