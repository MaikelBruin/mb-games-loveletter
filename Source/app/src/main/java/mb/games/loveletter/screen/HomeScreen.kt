package mb.games.loveletter.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import mb.games.loveletter.data.homeMenuItems

@Composable
fun HomeScreen(
    onNavigateToNewGame: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    MenuView(onNavigateToNewGame, onNavigateToHelp, onNavigateToAbout)
}

@Composable
fun MenuView(
    onNavigateToNewGame: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    LazyColumn {
        items(homeMenuItems) { menuItem ->
            MenuItemView(menuItem = menuItem, onClick = {
                when (menuItem.name) {
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen({}, {}, {})
}