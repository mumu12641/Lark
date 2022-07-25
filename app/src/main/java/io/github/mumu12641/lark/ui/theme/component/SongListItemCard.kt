package io.github.mumu12641.lark.ui.theme.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.SongList

@Composable
fun SongListItemCard(
    songList: SongList,
    onClickToNavigate: (Long) -> Unit
) {

    val modifierImage = Modifier
        .size(150.dp)
        .clip(RoundedCornerShape(20.dp))

    Column(
        modifier = Modifier.padding(bottom = 10.dp, end = 10.dp)
    ) {
        SongListAlbumCard(onClickToNavigate, songList, modifierImage)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = songList.songListTitle,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = songList.songNumber.toString() + stringResource(id = R.string.songs_text),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun SongListAlbumCard(
    onClick: (Long) -> Unit,
    songList: SongList,
    modifier: Modifier
) {
    Box(
        modifier = Modifier
            .padding(top = 20.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { onClick(songList.songListId) },
        contentAlignment = Alignment.Center
    ) {
        if (songList.type == 1) {
            SongListPicture(modifier, R.drawable.favorite)
        } else {
            AsyncImage(
                modifier = modifier,
                imageModel = songList.imageFileUri,
                failure = R.drawable.album)
        }
    }
}

@Composable
fun SongListPicture(modifier: Modifier, @DrawableRes id: Int) {
    Image(
        painter = painterResource(id),
        modifier = modifier,
        contentDescription = "I like"
    )
}

@Preview
@Composable
fun PreviewSongListItem() {
//    SongListItemCard(
//        songList = SongList(0L,"i like","tody",0,"test","asd",2)
//    )
}