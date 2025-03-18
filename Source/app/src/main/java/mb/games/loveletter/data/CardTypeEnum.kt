package mb.games.loveletter.data

enum class CardType(val card: Card) {
    Spy(
        Card(
            "Spy",
            0,
            2,
            "Gain favor if no one else plays/discards a spy.",
            "At the end of the round, if you are the only player still in the round who played or discarded a Spy, gain 1 favor token."
        )
    ),
    Guard(
        Card(
            "Guard",
            1,
            6,
            "Guess a hand.",
            "Choose another player and name a non-Guard card. IF that player has that card, they are out of the round."
        )
    ),
    Priest(
        Card(
            "Priest",
            2,
            2,
            "Look at a hand.",
            "Choose and look at another player's hand."
        )
    ),
    Baron(
        Card(
            "Baron",
            3,
            2,
            "Compare hands.",
            "Choose and secretly compare hands with another player. Whoever has the lowest value is out of the round."
        )
    ),
    Handmaid(
        Card(
            "Handmaid",
            4,
            2,
            "Immune to other cards until your next turn.",
            "Until your next turn, other players cannot choose you for their card effects."
        )
    ),
    Prince(
        Card(
            "Prince",
            5,
            2,
            "Discard hand & redraw.",
            "Choose any player (including yourself). That player discards their hand and redraws."
        )
    ),
    Chancellor(
        Card(
            "Chancellor",
            6,
            2,
            "Draw & return 2 cards.",
            "Draw 2 cards. Keep 1 card and put your other 2 cards on the bottom of the deck in any order."
        )
    ),
    King(
        Card(
            "King",
            7,
            1,
            "Trade hands.",
            "Choose and trade hands with another player."
        )
    ),
    Countess(
        Card(
            "Countess",
            8,
            1,
            "Must play if you have King or Prince.",
            "If the King or Prince is in your hand, you must play this card."
        )
    ),
    Princess(
        Card(
            "Princess",
            9,
            1,
            "Out of the round if you play/discard.",
            "If you play or discard this card, you are out of the round."
        )
    )
}