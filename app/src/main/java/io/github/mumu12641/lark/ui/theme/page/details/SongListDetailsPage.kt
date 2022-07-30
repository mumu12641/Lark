package io.github.mumu12641.lark.ui.theme.page.details

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.mumu12641.lark.BaseApplication
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.CHANGE_PLAT_LIST_SHUFFLE
import io.github.mumu12641.lark.entity.INIT_SONG_LIST
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.ui.theme.component.AsyncImage
import io.github.mumu12641.lark.ui.theme.component.SongItemRow
import io.github.mumu12641.lark.ui.theme.component.SongListPicture
import io.github.mumu12641.lark.ui.theme.page.home.MainViewModel

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SongListDetailsPage(
    navController: NavController,
    viewModel: SongListDetailsViewModel,
    playMedia: (Long, Long) -> Unit
) {
    val state by viewModel.songList.collectAsState(
        initial = INIT_SONG_LIST
    )

    val songs by viewModel.songs.collectAsState(initial = emptyList())
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                ShowSongs(
                    songs = songs,
                    modifier = Modifier,
                    top = 0,
                    playMedia = { songListId: Long, songId: Long ->
                        MainViewModel.playMedia(songListId, songId)
                    },
                    songList = state
                )
            },
            sheetPeekHeight = (BaseApplication.deviceScreen[1] - 570).dp,
            sheetBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,

            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            topBar = {
                SmallTopAppBar(
                    modifier = Modifier.padding(
                        WindowInsets
                            .statusBars
                            .only(
                                WindowInsetsSides.Horizontal
                                        + WindowInsetsSides.Top
                            ).asPaddingValues()
                    ),
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                        }
                    }
                )
            },
            content = { paddingValues ->
                SongListDetailsContent(
                    modifier = Modifier.padding(paddingValues),
                    songList = state,
                    songs = songs,
                    changeSongListImage = { uri ->
                        viewModel.changeSongListImage(uri)
                    }, playMedia = playMedia
                )
            }
        )
    }

}

@Composable
fun SongListDetailsContent(
    modifier: Modifier,
    songList: SongList?,
    songs: List<Song>,
    changeSongListImage: (String) -> Unit,
    playMedia: (Long, Long) -> Unit
) {

    val launcherBackground = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            changeSongListImage(uri.toString())
        }
    }

    Column(
        modifier = modifier.padding(top = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(350.dp)
                    .padding(10.dp)
                    .clip(RectangleShape)
                    .clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable(
                        onClick = {
                            if (songList?.type != 1) {
                                launcherBackground.launch("image/*")
                            } else {
                                Log.d("TAG", "SongListDetailsContent")
                            }
                        }
                    ),
                contentAlignment = Alignment.Center,

                ) {
                if (songList?.type == 1) {
                    Box(modifier = Modifier.size(350.dp)) {
                        if (songs.isNotEmpty()) {
                            AsyncImage(
                                modifier = Modifier.size(350.dp),
                                imageModel = songList.imageFileUri,
                                failure = R.drawable.favorite
                            )
                        }
                        SongListPicture(Modifier.size(350.dp), R.drawable.favorite)
                    }
                } else {
                    AsyncImage(
                        modifier = Modifier.size(350.dp),
                        imageModel = songList!!.imageFileUri,
                        failure = R.drawable.album
                    )
                }
            }
        }
        Text(
            text = songList!!.songListTitle,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(start = 20.dp)
        )
        Text(
            text = songList.description,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(start = 20.dp)
        )
        Row(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = { playMedia(songList.songListId, songs[0].songId) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "play")
                    Text(text = stringResource(id = R.string.play_all_text))
                }
            }
            Spacer(modifier = Modifier.weight(0.25f))
            Button(modifier = Modifier.weight(1f), onClick = {
                playMedia(
                    songList.songListId,
                    CHANGE_PLAT_LIST_SHUFFLE
                )
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_shuffle_24),
                        contentDescription = "play"
                    )
                    Text(text = stringResource(id = R.string.shuffle_text))
                }
            }
        }
//        ShowSongs(songs, modifier, 20, playMedia, songList)
    }
}

@Composable
fun ShowSongs(
    songs: List<Song>,
    modifier: Modifier,
    top: Int,
    playMedia: (Long, Long) -> Unit,
    songList: SongList
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = top.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (songs.isEmpty()) {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "暂无歌曲~",
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        } else {
            LazyColumn {
                items(songs) { item ->
                    SongItemRow(item, null) {
                        playMedia(songList.songListId, item.songId)
                    }
                }
            }
            Text(
                text = "加载到底啦~",
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

}
