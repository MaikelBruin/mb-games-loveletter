package mb.games.loveletter.data

class Deck(private val cards: MutableList<Cards>) {
    fun drawCard(): Cards? = if (cards.isNotEmpty()) cards.removeAt(0) else null

    fun getCards(): List<Cards> {
        return cards
    }

    fun deal(playerIds: List<Long>): Map<Long, Cards> {
        val hands = mutableMapOf<Long, Cards>()  // Store which player gets which card
        for (player in playerIds) {
            drawCard()?.let { hands[player] = it }
        }

        return hands
    }

    companion object {
        fun createNewDeck(): Deck {
            val allCards: MutableList<Cards> = mutableListOf()
            allCards.add(Cards.Spy1)
            allCards.add(Cards.Spy2)
            allCards.add(Cards.Guard1)
            allCards.add(Cards.Guard2)
            allCards.add(Cards.Guard3)
            allCards.add(Cards.Guard4)
            allCards.add(Cards.Guard5)
            allCards.add(Cards.Guard6)
            allCards.add(Cards.Priest1)
            allCards.add(Cards.Priest2)
            allCards.add(Cards.Baron1)
            allCards.add(Cards.Baron2)
            allCards.add(Cards.Handmaid1)
            allCards.add(Cards.Handmaid2)
            allCards.add(Cards.Prince1)
            allCards.add(Cards.Prince2)
            allCards.add(Cards.Chancellor1)
            allCards.add(Cards.Chancellor2)
            allCards.add(Cards.King)
            allCards.add(Cards.Countess)
            allCards.add(Cards.Princess)
            return Deck(allCards.shuffled().toMutableList())
        }
    }
}
