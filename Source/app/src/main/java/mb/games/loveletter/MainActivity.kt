package mb.games.loveletter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mb.games.loveletter.screen.AboutScreen
import mb.games.loveletter.screen.HomeScreen
import mb.games.loveletter.screen.Screen
import mb.games.loveletter.ui.theme.LoveLetterTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navHostController: NavHostController = rememberNavController()
            LoveLetterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    NavigationGraph(navHostController = navHostController)
                }
            }
        }
    }
}

@Composable
fun NavigationGraph(
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = Screen.HomeScreen.route) {
        composable(Screen.HomeScreen.route) {
            HomeScreen(onNavigateToNewGame = {
                navHostController.navigate(Screen.PlayerListScreen.route)
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
    }
}

