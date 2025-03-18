package mb.games.loveletter.data

class Deck() {
    private var deck: MutableList<Cards> = mutableListOf()

    fun createStartingDeck(): MutableList<Cards> {
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

    fun drawCard(): Card {
        return deck.removeFirst().cardType.card
    }

}