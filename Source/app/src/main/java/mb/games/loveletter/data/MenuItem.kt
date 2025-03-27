package mb.games.loveletter.data

import androidx.annotation.DrawableRes
import mb.games.loveletter.R

data class MenuItem(@DrawableRes val icon: Int, val name: String)

val homeGameMenuItem = MenuItem(R.drawable.baseline_home_24, "Home")
val continueGameMenuItem = MenuItem(R.drawable.baseline_play_circle_24, "Continue game")
val newGameMenuItem = MenuItem(R.drawable.baseline_new_label_24, "New game")
val startGameMenuItem = MenuItem(R.drawable.baseline_videogame_asset_24, "Start game")
val helpMenuItem = MenuItem(R.drawable.baseline_help_24, "Help")
val aboutMenuItem = MenuItem(R.drawable.baseline_info_24, "About")
val newRoundMenuItem = MenuItem(R.drawable.baseline_roundabout_right_24, "Start new round")
val exitGameMenuItem = MenuItem(R.drawable.baseline_exit_to_app_24, "Exit game")
val homeMenuItems = mutableListOf(
    newGameMenuItem,
    helpMenuItem,
    aboutMenuItem
)
val newGameMenuItems = listOf(
    startGameMenuItem,
    homeGameMenuItem
)