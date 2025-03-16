package mb.games.loveletter.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NewGameScreen(
    onStartGame: () -> Unit,
    onBackToHome: () -> Unit
) {
    NewGameScreen(onStartGame, onBackToHome)
}

@Composable
fun NewGameView(
    onStartGame: () -> Unit,
    onBackToHome: () -> Unit
) {
    var numberOfPlayers by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(value = numberOfPlayers,
            onValueChange = { numberOfPlayers = it },
            label = { Text("Number Of Players") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        Button(
            onClick = {
                onStartGame()
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Start Game")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Back to main menu.",
            modifier = Modifier.clickable { onBackToHome() })
    }
}

@Preview(showBackground = true)
@Composable
fun NewGameScreenPreview() {
    NewGameView({}, {})
}