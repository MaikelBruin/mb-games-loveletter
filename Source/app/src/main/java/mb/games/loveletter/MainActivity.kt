package mb.games.loveletter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import mb.games.loveletter.ui.theme.Bordeaux
import mb.games.loveletter.ui.theme.LoveLetterTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navHostController: NavHostController = rememberNavController()
            LoveLetterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = Bordeaux
                ) {
                    NavigationGraph(navHostController = navHostController)
                }
            }
        }
    }
}

