package mb.games.loveletter

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import mb.games.loveletter.screen.AboutScreen
import mb.games.loveletter.screen.HelpScreen
import mb.games.loveletter.screen.HomeScreen
import mb.games.loveletter.screen.NewGameScreen
import mb.games.loveletter.screen.Screen

@Composable
fun NavigationGraph(
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = Screen.HomeScreen.route) {
        composable(Screen.HomeScreen.route) {
            HomeScreen(onNavigateToNewGame = {
                navHostController.navigate(Screen.NewGameScreen.route)
            }, onNavigateToHelp = {
                navHostController.navigate(Screen.HelpScreen.route)
            }, onNavigateToAbout = {
                navHostController.navigate(Screen.AboutScreen.route)
            })
        }
        composable(Screen.AboutScreen.route) {
            AboutScreen(onClick = {
                navHostController.navigate(Screen.HomeScreen.route)
            })
        }
        composable(Screen.HelpScreen.route) {
            HelpScreen(onClick = {
                navHostController.navigate(Screen.HomeScreen.route)
            })
        }
        composable(Screen.NewGameScreen.route) {
            NewGameScreen(onStartGame = {
                navHostController.navigate(Screen.HomeScreen.route)
            }, onBackToHome = { navHostController.navigate(Screen.HomeScreen.route) })
        }
    }
}

