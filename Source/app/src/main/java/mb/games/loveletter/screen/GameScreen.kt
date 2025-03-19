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

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        //left
        Column(
            modifier = Modifier.fillMaxWidth(fraction = 0.5F),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            //top left, should display logs for player and bot turns
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top
            ) {
                Text(text = "player names: " + players.value.map { player -> player.name }
                    .toString())
                Text(text = "should show turn logs: ")
            }
            //bottom left, should display own cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End
            ) {
                Text(text = "my own card(s)")
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(text = "cards remaining in deck: " + currentGameSession?.deck?.size.toString())
            }

            //bottom right, should show menu items
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End
            ) {
                Text(text = "cards remaining in deck: " + currentGameSession?.deck?.size.toString())
            }
        }

    }
}

