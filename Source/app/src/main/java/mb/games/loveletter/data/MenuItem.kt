package mb.games.loveletter.data

import androidx.annotation.DrawableRes
import mb.games.loveletter.R

data class MenuItem(@DrawableRes val icon: Int, val name: String)

val menuItems = listOf(
    MenuItem(R.drawable.baseline_play_circle_24, "New game"),
    MenuItem(R.drawable.baseline_help_24, "Help"),
    MenuItem(R.drawable.baseline_info_24, "About"),
)