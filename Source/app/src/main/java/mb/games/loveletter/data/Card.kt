package mb.games.loveletter.data

data class Card(
    val name: String,
    val value: Int,
    val amountInDeck: Int,
    val shortDescription: String,
    val longDescription: String
)
