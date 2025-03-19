package mb.games.loveletter.data

class Deck(private val cards: MutableList<Cards>) {
    fun drawCard(): Cards? = if (cards.isNotEmpty()) cards.removeAt(0) else null

    fun getCards(): List<Cards> {
        return cards
    }

    fun shuffleDeck() {
        cards.shuffle()
    }

    fun isEmpty(): Boolean = cards.isEmpty()

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
            return Deck(allCards.shuffled().toMutableList()) // Shuffle new deck
        }
    }


    fun createStartingDeck(): MutableList<Cards> {
        val deck: MutableList<Cards> = mutableListOf()
        deck.add(Cards.Spy1)
        deck.add(Cards.Spy2)
        deck.add(Cards.Guard1)
        deck.add(Cards.Guard2)
        deck.add(Cards.Guard3)
        deck.add(Cards.Guard4)
        deck.add(Cards.Guard5)
        deck.add(Cards.Guard6)
        deck.add(Cards.Priest1)
        deck.add(Cards.Priest2)
        deck.add(Cards.Baron1)
        deck.add(Cards.Baron2)
        deck.add(Cards.Handmaid1)
        deck.add(Cards.Handmaid2)
        deck.add(Cards.Prince1)
        deck.add(Cards.Prince2)
        deck.add(Cards.Chancellor1)
        deck.add(Cards.Chancellor2)
        deck.add(Cards.King)
        deck.add(Cards.Countess)
        deck.add(Cards.Princess)
        deck.shuffle()
        return deck
    }
}
