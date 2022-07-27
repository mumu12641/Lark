package io.github.mumu12641.lark.ui.theme.component

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.mumu12641.lark.MainActivity.Companion.context
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.INIT_SONG
import io.github.mumu12641.lark.entity.LikeSongListId
import io.github.mumu12641.lark.entity.PlaylistSongCrossRef
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.room.DataBaseUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SongDetailBottomSheet(
    song: Song,
    addToSongList: () -> Unit
) {

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp, top = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 25.dp, top = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .size(30.dp, 4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    .zIndex(1f)
            ) {}
        }
        Text(text = song.songTitle, style = MaterialTheme.typography.titleLarge)
        Text(text = song.songSinger, style = MaterialTheme.typography.titleSmall)
        Row(modifier = Modifier.padding(top = 20.dp)) {
            androidx.compose.material3.OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        if (!DataBaseUtils.isRefExist(LikeSongListId, song.songId)) {
                            DataBaseUtils.insertRef(
                                PlaylistSongCrossRef(
                                    LikeSongListId,
                                    song.songId
                                )
                            )
                            DataBaseUtils.updateSongList(
                                DataBaseUtils.querySongListById(LikeSongListId).copy(
                                    songNumber = DataBaseUtils.querySongListWithSongsBySongListId(
                                        LikeSongListId
                                    ).songs.size
                                )
                            )
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.add_successful_text),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.already_added_text),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "like"
                    )
                    Text(text = stringResource(id = R.string.add_to_favourite_text))
                }
            }
            Spacer(modifier = Modifier.weight(0.25f))
            Button(
                modifier = Modifier.weight(1f),
                onClick = addToSongList
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add"
                    )
                    Text(text = stringResource(id = R.string.add_to_song_list_text))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBottomSheetContent() {
//    Box(
//        modifier = Modifier
//            .background(MaterialTheme.colorScheme.secondaryContainer)
//            .clip(
//                RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
//            ),
//    ) {
//        SongDetailBottomSheet(song = INIT_SONG) {}
//    }
}