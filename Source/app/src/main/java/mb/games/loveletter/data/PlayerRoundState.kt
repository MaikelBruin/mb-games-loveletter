package mb.games.loveletter.data

data class PlayerRoundState(
    val hand: List<Int> = emptyList(),
    val discardPile: List<Int> = emptyList(),
    val isAlive: Boolean = true
)
