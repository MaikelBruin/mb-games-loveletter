package mb.games.loveletter.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import mb.games.loveletter.data.MenuItem

@Composable
fun MenuItemView(menuItem: MenuItem, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clickable { onClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
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
        Divider(color = Color.Black)
    }

}