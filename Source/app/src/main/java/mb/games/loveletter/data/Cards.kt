package mb.games.loveletter.data

sealed class Cards(val id: Int, val cardType: CardType) {
    data object Spy1 : Cards(0, CardType.Spy)
    data object Spy2 : Cards(1, CardType.Spy)
    data object Guard1 : Cards(2, CardType.Guard)
    data object Guard2 : Cards(3, CardType.Guard)
    data object Guard3 : Cards(4, CardType.Guard)
    data object Guard4 : Cards(5, CardType.Guard)
    data object Guard5 : Cards(6, CardType.Guard)
    data object Guard6 : Cards(7, CardType.Guard)
    data object Priest1 : Cards(8, CardType.Priest)
    data object Priest2 : Cards(9, CardType.Priest)
    data object Baron1 : Cards(10, CardType.Baron)
    data object Baron2 : Cards(11, CardType.Baron)
    data object Handmaid1 : Cards(12, CardType.Handmaid)
    data object Handmaid2 : Cards(13, CardType.Handmaid)
    data object Prince1 : Cards(14, CardType.Prince)
    data object Prince2 : Cards(15, CardType.Prince)
    data object Chancellor1 : Cards(16, CardType.Chancellor)
    data object Chancellor2 : Cards(17, CardType.Chancellor)
    data object King : Cards(18, CardType.King)
    data object Countess : Cards(19, CardType.Countess)
    data object Princess : Cards(20, CardType.Princess)

    companion object {
        private val allCards: List<Cards> = listOf(
            Spy1,
            Spy2,
            Guard1,
            Guard2,
            Guard3,
            Guard4,
            Guard5,
            Guard6,
            Priest1,
            Priest2,
            Baron1,
            Baron2,
            Handmaid1,
            Handmaid2,
            Prince1,
            Prince2,
            Chancellor1,
            Chancellor2,
            King,
            Countess,
            Princess,
        )

        fun fromId(id: Int): Cards {
            return allCards.first { cards -> cards.id == id }
        }

        fun fromIds(ids: List<Int>): List<Cards> {
            val results = mutableListOf<Cards>()
            ids.forEach { id -> results.add(fromId(id))}
            return results.toList()
        }
    }
}
