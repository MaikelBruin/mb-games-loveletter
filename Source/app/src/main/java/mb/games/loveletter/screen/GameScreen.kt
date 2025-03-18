package mb.games.loveletter.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "player names: " + players.value.map { player -> player.name }.toString())

    }
}

