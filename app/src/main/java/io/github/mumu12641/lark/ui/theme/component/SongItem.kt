package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongItem(
    song: Song
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
            .background(Color.Transparent)
            .clickable {},
        colors = CardDefaults.outlinedCardColors()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(5.dp)
                .fillMaxSize()
        ) {
            AsyncImage(modifier = Modifier
                .size(50.dp)
                .clip(RectangleShape)
                .clip(RoundedCornerShape(10.dp)),
                imageModel = song.songAlbumFileUri,
                failure =  R.drawable.ic_baseline_music_note_24
            )
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(text = song.songTitle,
                    style = MaterialTheme.typography.bodyLarge, softWrap = false)
                Text(text = song.songSinger, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSongItem(){
    SongItem(song = Song(
        0L,"最伟大的作品","周杰伦","11","11",100
    ))
}