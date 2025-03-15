package mb.games.loveletter.data

import androidx.annotation.DrawableRes
import mb.games.loveletter.R

data class MenuItem(@DrawableRes val icon: Int, val name: String)

val newGameMenuItem = MenuItem(R.drawable.baseline_play_circle_24, "New game")
val helpMenuItem = MenuItem(R.drawable.baseline_help_24, "Help")
val aboutMenuItem = MenuItem(R.drawable.baseline_info_24, "About")
val homeMenuItems = listOf(
    newGameMenuItem,
    helpMenuItem,
    aboutMenuItem
)