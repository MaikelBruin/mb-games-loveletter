package mb.games.loveletter

import android.app.Application

class LoveLetterApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}