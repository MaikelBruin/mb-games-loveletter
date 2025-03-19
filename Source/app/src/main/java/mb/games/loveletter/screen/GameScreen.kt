package mb.games.loveletter.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import mb.games.loveletter.viewmodel.GameViewModel

@Composable
fun GameScreen(
    navController: NavController, viewModel: GameViewModel, onBackToHome: () -> Unit
) {
    GameView(navController, viewModel, onBackToHome)
}

@Composable
fun GameView(
    navController: NavController, viewModel: GameViewModel, onBackToHome: () -> Unit
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val players = viewModel.getAllPlayers.collectAsState(initial = listOf())
    val currentGameSession = viewModel.currentGameSession.value

    //should display logs for player and bot turns
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(fraction = 0.5F),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(text = "player names: " + players.value.map { player -> player.name }.toString())
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(text = "cards in deck: " + currentGameSession?.deck.toString())
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(fraction = 0.5F),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Top,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "cards in deck: " + currentGameSession?.deck.toString())
            }
        }

    }
}

