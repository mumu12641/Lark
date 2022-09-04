package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.SongList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistIcon(
    modifier: Modifier = Modifier,
    padding: Int = 0,
    artist: SongList,
    imageClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = modifier.padding(padding.dp),
            shape = CircleShape
        ) {
            AsyncImage(
                modifier = modifier
                    .clickable(onClick = imageClick),
                imageModel = artist.imageFileUri,
                failure = R.drawable.ic_baseline_face_24
            )

        }
        Text(
            modifier = Modifier
                .width(100.dp)
                .padding(bottom = 10.dp),
            text = artist.songListTitle, softWrap = false,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }

}