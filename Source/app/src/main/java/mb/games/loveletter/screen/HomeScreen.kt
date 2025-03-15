package mb.games.loveletter.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mb.games.loveletter.data.MenuItem
import mb.games.loveletter.data.homeMenuItems

@Composable
fun HomeScreen(
    onNavigateToAbout: () -> Unit
) {
    MenuView()
}

@Composable
fun MenuView() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn {
            items(homeMenuItems) { menuItem ->
                MenuItem(menuItem = menuItem)
            }
        }
    }

}

@Composable
fun MenuItem(menuItem: MenuItem) {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Icon(
                        painter = painterResource(id = menuItem.icon),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        contentDescription = menuItem.name
                    )
                    Text(text = menuItem.name)
                }
                Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null)
            }
            Divider(color = Color.LightGray)
        }
    }

}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen({})
}