package mb.games.loveletter.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import mb.games.loveletter.R
import mb.games.loveletter.data.Player
import mb.games.loveletter.viewmodel.GameViewModel

@Composable
fun AddEditPlayerDetailView(
    id: Int, viewModel: GameViewModel, navController: NavController
) {

    val snackMessage = remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    if (id != 0) {
        val player = viewModel.getAPlayerById(id).collectAsState(initial = Player(0, "", 0, false))
        viewModel.playerNameState = player.value.name
        viewModel.isHumanState = player.value.isHuman
    } else {
        viewModel.playerNameState = ""
        viewModel.isHumanState = false
    }

    Scaffold(
        topBar = {
            AppBarView(
                title = if (id != 0) stringResource(id = R.string.update_player)
                else stringResource(id = R.string.add_player)
            ) { navController.navigateUp() }
        }, scaffoldState = scaffoldState
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            PlayerTextField(label = "Name", value = viewModel.playerNameState, onValueChanged = {
                viewModel.onPlayerNameChanged(it)
            })
            PlayerCheckBox(checked = viewModel.isHumanState, onValueChanged = {
                viewModel.onPlayerIsHumanChanged(it)
            })

            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                if (viewModel.playerNameState.isNotEmpty()) {
                    if (id != 0) {
                        viewModel.updatePlayer(
                            Player(
                                id = id,
                                name = viewModel.playerNameState.trim(),
                                isHuman = viewModel.isHumanState
                            )
                        )
                    } else {
                        viewModel.addPlayer(
                            Player(
                                name = viewModel.playerNameState.trim(),
                                isHuman = viewModel.isHumanState
                            )
                        )
                        snackMessage.value = "Player has been created"
                    }
                } else {
                    snackMessage.value = "Enter fields to create a player (or bot)."
                }
                scope.launch {
                    navController.navigateUp()
                }

            }) {
                Text(
                    text = if (id != 0) stringResource(id = R.string.update_player)
                    else stringResource(
                        id = R.string.add_player
                    ), style = TextStyle(
                        fontSize = 18.sp
                    )
                )
            }
        }
    }

}


@Composable
fun PlayerTextField(
    label: String, value: String, onValueChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        label = { Text(text = label, color = Color.Black) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            // using predefined Color
            textColor = Color.Black,
            // using our own colors in Res.Values.Color
            focusedBorderColor = colorResource(id = R.color.black),
            unfocusedBorderColor = colorResource(id = R.color.black),
            cursorColor = colorResource(id = R.color.black),
            focusedLabelColor = colorResource(id = R.color.black),
            unfocusedLabelColor = colorResource(id = R.color.black),
        )


    )
}

@Composable
fun PlayerCheckBox(
    checked: Boolean, onValueChanged: (Boolean) -> Unit
) {
    Checkbox(
        checked = checked, onCheckedChange = onValueChanged
    )
}

@Preview
@Composable
fun PlayerFieldPreview() {
    PlayerTextField(label = "text", value = "text", onValueChanged = {})
}

