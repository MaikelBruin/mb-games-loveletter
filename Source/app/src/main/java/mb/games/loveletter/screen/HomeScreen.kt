package mb.games.loveletter.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import mb.games.loveletter.data.homeMenuItems

@Composable
fun HomeScreen(
    onNavigateToNewGame: () -> Unit, onNavigateToHelp: () -> Unit, onNavigateToAbout: () -> Unit
) {
    MenuView(onNavigateToNewGame, onNavigateToHelp, onNavigateToAbout)
}

@Composable
fun MenuView(
    onNavigateToNewGame: () -> Unit, onNavigateToHelp: () -> Unit, onNavigateToAbout: () -> Unit
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