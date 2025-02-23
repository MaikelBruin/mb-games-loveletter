package mb.games.loveletter.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mb.games.loveletter.data.GameDatabase
import mb.games.loveletter.data.Player

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val db = GameDatabase.getDatabase(application)
    private val gameDao = db.gameDao()

    fun addPlayer(playerName: String) {
        viewModelScope.launch {
            val player = Player(name = playerName)
            gameDao.insertPlayer(player)
        }
    }

    fun getPlayers(onResult: (List<Player>) -> Unit) {
        viewModelScope.launch {
            val players = gameDao.getAllPlayers()
            onResult(players)
        }
    }
}
