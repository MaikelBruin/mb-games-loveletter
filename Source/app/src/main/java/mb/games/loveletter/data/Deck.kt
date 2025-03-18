package mb.games.loveletter.data

class Deck() {
    private var deck: MutableList<Card> = mutableListOf()

    fun getDefaultDeck(): MutableList<Card> {
        repeat(Cards.Spy.card.amountInDeck) { deck.add(Cards.Spy.card.copy()) }
        repeat(Cards.Guard.card.amountInDeck) { deck.add(Cards.Guard.card.copy()) }
        repeat(Cards.Priest.card.amountInDeck) { deck.add(Cards.Priest.card.copy()) }
        repeat(Cards.Baron.card.amountInDeck) { deck.add(Cards.Baron.card.copy()) }
        repeat(Cards.Handmaid.card.amountInDeck) { deck.add(Cards.Handmaid.card.copy()) }
        repeat(Cards.Prince.card.amountInDeck) { deck.add(Cards.Prince.card.copy()) }
        repeat(Cards.Chancellor.card.amountInDeck) { deck.add(Cards.Chancellor.card.copy()) }
        repeat(Cards.King.card.amountInDeck) { deck.add(Cards.King.card.copy()) }
        repeat(Cards.Countess.card.amountInDeck) { deck.add(Cards.Countess.card.copy()) }
        repeat(Cards.Princess.card.amountInDeck) { deck.add(Cards.Princess.card.copy()) }
        deck.shuffle()
        return deck
    }

    fun drawCard(): Card {
        return deck.removeFirst()
    }
}