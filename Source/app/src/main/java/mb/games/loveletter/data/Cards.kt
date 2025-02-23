package mb.games.loveletter.data

sealed class Cards(val card: Card) {
    object Spy : Cards(Card("Spy", 0, 2))
    object Guard : Cards(Card("Guard", 1, 6))
    object Priest : Cards(Card("Priest", 2, 2))
    object Baron : Cards(Card("Baron", 3, 2))
    object Handmaid : Cards(Card("Handmaid", 4, 2))
    object Prince : Cards(Card("Prince", 5, 2))
    object Chancellor : Cards(Card("Chancellor", 6, 2))
    object King : Cards(Card("King", 7, 1))
    object Countess : Cards(Card("Countess", 8, 1))
    object Princess : Cards(Card("Princess", 9, 1))
}
