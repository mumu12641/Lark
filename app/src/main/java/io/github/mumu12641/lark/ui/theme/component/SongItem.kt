package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.Song
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongItem(
    song: Song,
    showBottomSheet: (Song) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
        colors = CardDefaults.outlinedCardColors()
    ) {
        SongItemRow(song, showBottomSheet, onClick)
    }
}

@Composable
fun SongItemRow(
    song: Song, showBottomSheet: ((Song) -> Unit)?, onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding( end = 5.dp, top = 5.dp, bottom = 5.dp)
            .background(Color.Transparent)
            .clip(RoundedCornerShape(5.dp))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            modifier = Modifier
                .padding(start = 5.dp)
                .size(50.dp)
                .clip(RectangleShape)
                .clip(RoundedCornerShape(10.dp)),
            imageModel = song.songAlbumFileUri,
            failure = R.drawable.ic_baseline_music_note_24
        )
        Column(
            modifier = Modifier
                .padding(10.dp)
                .weight(1f)
        ) {
            Text(
                text = song.songTitle,
                style = MaterialTheme.typography.bodyLarge,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = song.songSinger, style = MaterialTheme.typography.bodySmall)
        }
        showBottomSheet?.let {
            IconButton(onClick = {
                showBottomSheet(song)
            }, Modifier.weight(0.15f)) {
                Icon(Icons.Filled.MoreVert, contentDescription = "more")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSongItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)

    ) {
        val song = Song(
            0L, "最伟大的作品", "周杰伦", "11", "11", 100
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
                .background(Color.Transparent)
                .clip(RoundedCornerShape(5.dp))
        ) {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = song.songTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = song.songSinger, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "more")
            }
        }
    }

}

@ExperimentalMaterialApi
@Preview
@Composable
fun PreviewBottomSheet() {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    val coroutineScope = rememberCoroutineScope()
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Text(text = "Hello from sheet")
            }
        }, sheetPeekHeight = 0.dp
    ) {
        Button(onClick = {
            coroutineScope.launch {
                if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                    bottomSheetScaffoldState.bottomSheetState.expand()
                } else {
                    bottomSheetScaffoldState.bottomSheetState.collapse()
                }
            }
        }) {
            Text(text = "Expand/Collapse Bottom Sheet")
        }
    }

}