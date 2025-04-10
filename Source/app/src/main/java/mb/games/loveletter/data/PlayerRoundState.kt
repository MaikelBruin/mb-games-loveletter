package mb.games.loveletter.data

data class PlayerRoundState(
    val playerId: Long,
    val hand: List<Int> = emptyList(),
    val discardPile: List<Int> = emptyList(),
    val isProtected: Boolean = false,
    val isAlive: Boolean = true
)
