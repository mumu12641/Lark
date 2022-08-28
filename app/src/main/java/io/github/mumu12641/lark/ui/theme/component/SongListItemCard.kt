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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
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
                modifier = Modifier.width(130.dp),
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
//            Text(
//                modifier = Modifier.padding(start = 3.dp),
//                text = songList.songNumber.toString() + stringResource(id = R.string.songs_text),
//                style = MaterialTheme.typography.bodySmall
//            )
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
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable { onClick(songList.songListId) },
        contentAlignment = Alignment.Center
    ) {
        if (songList.type == 1) {
            Box(modifier = modifier) {
                AsyncImage(
                    modifier = modifier,
                    imageModel = songList.imageFileUri,
                    failure = R.drawable.favorite
                )
                SongListPicture (modifier, R.drawable.favorite)
            }
        } else {
            AsyncImage(
                modifier = modifier,
                imageModel = songList.imageFileUri,
                failure = R.drawable.album
            )
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

@Composable
fun SongListItemRow(
    songList: SongList,
    onClick:() -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
            .background(Color.Transparent)
            .clip(RoundedCornerShape(5.dp))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            modifier = Modifier
                .size(50.dp)
                .padding(start = 0.dp)
                .clip(RectangleShape)
                .clip(RoundedCornerShape(10.dp)),
            imageModel = songList.imageFileUri,
            failure =
                if (songList.type == 1) R.drawable.favorite
                else R.drawable.album

        )
        Column(
            modifier = Modifier
                .padding(10.dp)
                .weight(1f)
        ) {
            Text(
                text = songList.songListTitle,
                style = MaterialTheme.typography.bodyLarge,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = songList.songNumber.toString() + stringResource(id = R.string.songs_text), style = MaterialTheme.typography.bodySmall)
        }
    }
}
