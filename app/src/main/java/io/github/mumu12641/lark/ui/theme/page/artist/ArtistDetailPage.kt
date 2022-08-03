package io.github.mumu12641.lark.ui.theme.page.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.mumu12641.lark.entity.INIT_SONG_LIST
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.ui.theme.component.AsyncImage
import io.github.mumu12641.lark.ui.theme.component.LarkSmallTopBar
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.ui.theme.page.details.PlayButton
import io.github.mumu12641.lark.ui.theme.page.details.ShowArtistSongs
import io.github.mumu12641.lark.ui.theme.page.details.ShowSongs

@Composable
fun ArtistDetailPage(
    navController: NavController,
    artistViewModel: ArtistViewModel
) {
    val songs by artistViewModel.songs.collectAsState(initial = emptyList())
    val songList by artistViewModel.songList.collectAsState(initial = INIT_SONG_LIST)
    val scrollState = rememberScrollState()
        Scaffold(
            modifier = Modifier
                .height(5000.dp)
                .padding(
                    WindowInsets
                        .statusBars
                        .only(
                            WindowInsetsSides.Horizontal
                                    + WindowInsetsSides.Top
                        )
                        .asPaddingValues()
                ),
            topBar = {
                LarkSmallTopBar(title = "", navIconClick = { navController.popBackStack() }) {

                }
            },
            content = { paddingValues ->
                ArtistDetailContent(modifier = Modifier.padding(paddingValues), songs, songList) {
                    artistViewModel.updateArtistDetail(it)
                }
            }
        )


}

@Composable
fun ArtistDetailContent(
    modifier: Modifier, songs: List<Song>, songList: SongList, updateArtistDetail: (String) -> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(350.dp)
                    .padding(10.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                        updateArtistDetail(songList.songListTitle)
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    modifier = Modifier.size(350.dp),
                    imageModel = songList.imageFileUri,
                    failure = R.drawable.ic_baseline_face_24
                )
            }
        }
        Text(
            text = songList.songListTitle,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 20.dp)
        )
        PlayButton(playMedia = { _, _ ->
            {}
        }, songList = songList, songs = songs)
        Text(
            text = stringResource(id = R.string.description_text),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(20.dp)
        )
        Text(
            text = songList.description,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 20.dp, top = 5.dp)
        )
        Text(
            text = stringResource(id = R.string.song_text),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(20.dp)
        )
        ShowArtistSongs(
            songs = songs,
            modifier = Modifier.padding(start = 20.dp),
            playMedia = { _: Long, _: Long ->
//                playMedia(songListId, songId)
            },
            songList = songList
        )

    }
}