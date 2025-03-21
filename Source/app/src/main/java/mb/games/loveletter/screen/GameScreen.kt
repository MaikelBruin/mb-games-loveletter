package mb.games.loveletter.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mb.games.loveletter.data.Cards
import mb.games.loveletter.ui.theme.Bordeaux
import mb.games.loveletter.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel
) {
    GameView(viewModel)
}

@Composable
fun GameView(
    viewModel: GameViewModel
) {
    val players = viewModel.getAllPlayers.collectAsState(initial = listOf())
    val playerState = viewModel.playerState.collectAsState()
    val currentPlayer = viewModel.currentPlayer.value

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
            //top left, should display logs for player and bot turns
            val playersInGame = players.value
            val playerNames = playersInGame.map { player -> player.name }
            Row(
                modifier = Modifier.fillMaxHeight(0.5F),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start
            ) {
                Column {
                    Row {
                        Text(
                            text = "player names: $playerNames"
                        )
                    }
                    Row {
                        Text(text = "Current turn: ${currentPlayer?.name}")
                    }
                }
            }
            //bottom left, should display own cards
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Start
            ) {
                val cardsInHand = Cards.fromIds(playerState.value!!.hand)
                Column {
                    Row {
                        Text(text = "My card(s):")
                    }
                    LazyRow {
                        items(cardsInHand) { card ->
                            CardItemView(card = card)
                        }
                    }
                }
            }
        }
        //right
        Column(
            modifier = Modifier.fillMaxWidth(fraction = 0.5F),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Top,
        ) {
            //top right, should show deck information
            Row(
                modifier = Modifier.fillMaxHeight(0.5F),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.End
            ) {
                Text(text = "cards remaining in deck: placeholder")  //FIXME: deck is less than 21 cards because hands are dealt
            }

            //bottom right, should show menu items
            Row(
                modifier = Modifier.fillMaxHeight(0.5F),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End
            ) {
                Text(text = "should show menu items")
            }
        }

    }
}

