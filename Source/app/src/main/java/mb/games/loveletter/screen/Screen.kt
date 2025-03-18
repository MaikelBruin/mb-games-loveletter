package mb.games.loveletter.screen

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home")
    object NewGameScreen : Screen("new_game")
    object GameScreen : Screen("game")
    object AddPlayerScreen : Screen("add_player")
    object HelpScreen : Screen("help")
    object AboutScreen : Screen("about")
}