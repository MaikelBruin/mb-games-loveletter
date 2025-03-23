package mb.games.loveletter.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mb.games.loveletter.data.Cards
import mb.games.loveletter.ui.theme.Orange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardItemView(
    card: Cards, onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Orange, //Card background color
            contentColor = Color.Black  //Card content color,e.g.text
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(color = Color.Yellow)
        ) {
            Row {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(text = "Name:")
                }
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(text = card.cardType.card.name, fontWeight = FontWeight.ExtraBold)
                }
            }
            Row {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(text = "Value:")
                }
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(text = card.cardType.card.value.toString())
                }
            }
            Row {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        modifier = Modifier.width(100.dp),
                        maxLines = 3,
                        text = card.cardType.card.shortDescription,
                        softWrap = true
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardItemPreview() {
    CardItemView(card = Cards.Prince1, {})
}

