package mb.games.loveletter.screen

sealed class Screen(val route: String) {
    object HomeScreen:Screen("home")
    object NewGameScreen:Screen("new-game")
    object PlayerListScreen:Screen("player-list")
    object HelpScreen:Screen("help")
    object AboutScreen:Screen("about")
}