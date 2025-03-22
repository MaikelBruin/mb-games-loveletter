package mb.games.loveletter.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import mb.games.loveletter.data.continueGameMenuItem
import mb.games.loveletter.data.homeMenuItems
import mb.games.loveletter.viewmodel.GameViewModel

@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    onNavigateToContinueGame: () -> Unit,
    onNavigateToNewGame: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    HomeView(
        viewModel,
        onNavigateToContinueGame,
        onNavigateToNewGame,
        onNavigateToHelp,
        onNavigateToAbout
    )
}

@Composable
fun HomeView(
    viewModel: GameViewModel,
    onNavigateToContinueGame: () -> Unit,
    onNavigateToNewGame: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val activeGame by viewModel.activeGameSession.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Love Letter!",
                fontFamily = FontFamily.SansSerif,
                fontSize = TextUnit(value = 20F, TextUnitType.Sp)
            )
        }

        Row {
            LazyColumn {
                val menuItems = homeMenuItems
                if (activeGame != null && !menuItems.contains(continueGameMenuItem)) menuItems.add(
                    0, continueGameMenuItem
                )
                items(homeMenuItems) { menuItem ->
                    MenuItemView(menuItem = menuItem, onClick = {
                        when (menuItem.name) {
                            "Continue game" -> {
                                onNavigateToContinueGame()
                            }

                            "New game" -> {
                                onNavigateToNewGame()
                            }

                            "Help" -> {
                                onNavigateToHelp()
                            }

                            else -> {
                                onNavigateToAbout()
                            }
                        }
                    })
                }
            }

        }

    }

}

