package mb.games.loveletter

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import mb.games.loveletter.screen.AboutScreen
import mb.games.loveletter.screen.AddEditPlayerDetailView
import mb.games.loveletter.screen.GameScreen
import mb.games.loveletter.screen.HelpScreen
import mb.games.loveletter.screen.HomeScreen
import mb.games.loveletter.screen.NewGameScreen
import mb.games.loveletter.screen.Screen
import mb.games.loveletter.viewmodel.GameViewModel

@Composable
fun NavigationGraph(
    navHostController: NavHostController, gameViewModel: GameViewModel = viewModel()
) {
    NavHost(navController = navHostController, startDestination = Screen.HomeScreen.route) {
        composable(Screen.HomeScreen.route) {
            HomeScreen(viewModel = gameViewModel, onNavigateToContinueGame = {
                navHostController.navigate(Screen.GameScreen.route)
            }, onNavigateToNewGame = {
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
            NewGameScreen(navController = navHostController,
                viewModel = gameViewModel,
                onStartGame = {
                    navHostController.navigate(Screen.GameScreen.route)
                },
                onBackToHome = { navHostController.navigate(Screen.HomeScreen.route) })
        }
        composable(Screen.GameScreen.route) {
            GameScreen(
                viewModel = gameViewModel
            )
        }
        composable(Screen.AddPlayerScreen.route + "/{id}", arguments = listOf(navArgument("id") {
            type = NavType.IntType
            defaultValue = 0
            nullable = false
        })) { entry ->
            val id = if (entry.arguments != null) entry.arguments!!.getLong("id") else 0L
            AddEditPlayerDetailView(
                id = id, viewModel = gameViewModel, navController = navHostController
            )
        }
    }
}

