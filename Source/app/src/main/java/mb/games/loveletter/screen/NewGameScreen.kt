package mb.games.loveletter.screen

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mb.games.loveletter.data.Player
import mb.games.loveletter.viewmodel.GameSessionViewModel

@Composable
fun NewGameScreen(
    onStartGame: () -> Unit, onBackToHome: () -> Unit
) {
    NewGameScreen(onStartGame, onBackToHome)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NewGameView(
    navController: NavController,
    viewModel: GameSessionViewModel,
    onStartGame: () -> Unit,
    onBackToHome: () -> Unit
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    Scaffold(scaffoldState = scaffoldState,
        topBar = { AppBarView(title = "Players") },
        floatingActionButton = {
            FloatingActionButton(modifier = Modifier.padding(all = 20.dp),
                contentColor = Color.White,
                backgroundColor = Color.Black,
                onClick = {
                    Toast.makeText(context, "FAButton Clicked", Toast.LENGTH_LONG).show()
                    navController.navigate(Screen.AddPlayerScreen.route + "/0L")

                }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }

    ) {
        val players = viewModel.getAllPlayers.collectAsState(initial = listOf())
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            items(players.value, key = { player -> player.id }) { player ->
                val dismissState = rememberDismissState(confirmStateChange = {
                    if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
                        viewModel.deletePlayer(player)
                    }
                    true
                })

                SwipeToDismiss(state = dismissState,
                    background = {
                        val color by animateColorAsState(
                            if (dismissState.dismissDirection == DismissDirection.EndToStart) Color.Red else Color.Transparent,
                            label = ""
                        )
                        val alignment = Alignment.CenterEnd
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(horizontal = 20.dp),
                            contentAlignment = alignment
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Icon",
                                tint = Color.White
                            )
                        }

                    },
                    directions = setOf(DismissDirection.EndToStart),
                    dismissThresholds = { FractionalThreshold(1f) },
                    dismissContent = {
                        PlayerItem(player = player) {
                            val id = player.id
                            navController.navigate(Screen.AddPlayerScreen.route + "/$id")
                        }
                    })
            }
        }
    }
}

@Composable
fun PlayerItem(player: Player, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            .clickable {
                onClick()
            }, elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = player.name, fontWeight = FontWeight.ExtraBold)
            Checkbox(checked = player.isHuman, onCheckedChange = null)
        }
    }
}

