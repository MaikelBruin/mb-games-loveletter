package mb.games.loveletter.screen

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home")
    object NewGameScreen : Screen("new_game")
    object AddPlayerScreen : Screen("add_player")
    object PlayerListScreen : Screen("player_list")
    object HelpScreen : Screen("help")
    object AboutScreen : Screen("about")
}