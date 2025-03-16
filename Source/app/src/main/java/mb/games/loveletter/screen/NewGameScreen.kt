package mb.games.loveletter.screen

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import mb.games.loveletter.data.newGameMenuItems
import mb.games.loveletter.ui.theme.Bordeaux
import mb.games.loveletter.ui.theme.Orange
import mb.games.loveletter.viewmodel.GameSessionViewModel

@Composable
fun NewGameScreen(
    navController: NavController,
    viewModel: GameSessionViewModel,
    onStartGame: () -> Unit,
    onBackToHome: () -> Unit
) {
    NewGameView(navController, viewModel, onStartGame, onBackToHome)
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
        backgroundColor = Bordeaux,
        topBar = { AppBarView(title = "Players") },
        floatingActionButton = {
            FloatingActionButton(modifier = Modifier.padding(all = 20.dp),
                contentColor = Color.Black,
                backgroundColor = Color.LightGray,
                onClick = {
                    Toast.makeText(context, "FAButton Clicked", Toast.LENGTH_LONG).show()
                    navController.navigate(Screen.AddPlayerScreen.route + "/0")

                }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }

    ) {
        val players = viewModel.getAllPlayers.collectAsState(initial = listOf())
        Row() {
            Column(
                modifier = Modifier.fillMaxWidth(fraction = 0.5F),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "Players")
                }
                Row {

                    LazyColumn(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxWidth()
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
            LazyColumn(
                horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()
            ) {
                items(newGameMenuItems) { menuItem ->
                    MenuItemView(menuItem = menuItem, onClick = {
                        when (menuItem.name) {
                            "Start game" -> {
                                onStartGame()
                            }

                            "Home" -> {
                                onBackToHome()
                            }

                            else -> {
                                onBackToHome()
                            }
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun PlayerItem(player: Player, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Orange, //Card background color
            contentColor = Color.Black  //Card content color,e.g.text
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp,
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(color = Orange)
        ) {
            Row {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(text = "Name:")
                }
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(text = player.name, fontWeight = FontWeight.ExtraBold)
                }
            }
            Row {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(text = "Is human?")
                }
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Checkbox(checked = player.isHuman, onCheckedChange = null)
                }
            }
        }
    }
}

