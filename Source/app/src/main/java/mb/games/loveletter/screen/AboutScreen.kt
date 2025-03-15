package mb.games.loveletter.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AboutScreen(
    onClick: () -> Unit
) {
    AboutView(onClick)
}

@Composable
fun AboutView(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Made from MB with love.", Modifier.clickable { onClick() })
    }
}

@Preview(showBackground = true)
@Composable
fun AboutViewPreview() {
    AboutView({})
}