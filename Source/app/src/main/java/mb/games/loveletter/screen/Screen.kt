package mb.games.loveletter.screen

sealed class Screen(val route: String) {
    data object HomeScreen : Screen("home")
    data object NewGameScreen : Screen("new_game")
    data object GameScreen : Screen("game")
    data object AddPlayerScreen : Screen("add_player")
    data object HelpScreen : Screen("help")
    data object AboutScreen : Screen("about")
}