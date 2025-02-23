package mb.games.loveletter.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import mb.games.loveletter.data.Player
import mb.games.loveletter.viewmodel.GameViewModel

@Composable
fun PlayerListScreen(viewModel: GameViewModel) {
    var players by remember { mutableStateOf<List<Player>>(emptyList()) }

    LaunchedEffect(Unit) {
        viewModel.getPlayers { players = it }
    }

    LazyColumn {
        items(players) { player ->
            Text(text = "${player.name} - Wins: ${player.wins}")
        }
    }
}
