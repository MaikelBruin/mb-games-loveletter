package mb.games.loveletter.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mb.games.loveletter.data.CardType
import mb.games.loveletter.ui.theme.Orange

@Composable
fun OverviewCard(
) {
    Card(
        backgroundColor = Orange
    ) {
        LazyColumn(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Top) {
            items(CardType.entries.toTypedArray().reversed()) { item: CardType ->
                OverviewCardItem(cardType = item)
            }
        }
    }
}

@Composable
fun OverviewCardItem(cardType: CardType) {
    Row(
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text(text = cardType.card.value.toString() + " - ")
        }
        Column(
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text(text = cardType.card.name + " (" + cardType.card.amountInDeck + "x): ")
        }
        Column {
            Text(text = cardType.card.shortDescription)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OverviewCardPreview() {
    OverviewCard()
}

