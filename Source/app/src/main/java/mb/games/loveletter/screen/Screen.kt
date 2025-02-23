package mb.games.loveletter.screen

sealed class Screen(val route: String) {
    object HomeScreen:Screen("home")
    object HelpScreen:Screen("help")
    object AboutScreen:Screen("about")
}