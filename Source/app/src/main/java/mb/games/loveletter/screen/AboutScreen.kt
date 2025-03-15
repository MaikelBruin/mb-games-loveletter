package mb.games.loveletter.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AboutScreen() {
    AboutView()
}

@Composable
fun AboutView() {
    Row {
        Text(text = "Made from MB with love.")
    }
}

@Preview(showBackground = true)
@Composable
fun AboutViewPreview() {
    AboutView()
}