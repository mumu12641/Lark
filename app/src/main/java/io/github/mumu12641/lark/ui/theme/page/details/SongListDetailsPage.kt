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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import io.github.mumu12641.lark.BaseApplication
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.*
import io.github.mumu12641.lark.ui.theme.component.*

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
            backgroundColor = MaterialTheme.colorScheme.background,
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                ShowSongs(
                    songs = songs,
                    modifier = Modifier,
                    top = 0,
                    playMedia = { songListId: Long, songId: Long ->
                        playMedia(songListId, songId)
                    },
                    songList = state
                )
            },
            sheetPeekHeight = (BaseApplication.deviceScreen[1] - 570).dp,
            sheetBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,

            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            topBar = {
                LarkSmallTopBar(title = "", navIconClick = { navController.popBackStack() })
            },
            content = { paddingValues ->
                SongListDetailsContent(
                    modifier = Modifier.padding(paddingValues),
                    songList = state,
                    songs = songs,
                    changeSongListImage = { uri ->
                        viewModel.changeSongListImage(uri)
                    },
                    { description -> viewModel.updateSongListDescription(description) },
                    playMedia = playMedia
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
    updateDescription: (String) -> Unit,
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
    var showDialog by remember {
        mutableStateOf(false)
    }
    var textDescription by remember {
        mutableStateOf(songList?.description)
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
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable(
                        onClick = {
                            if (songList?.type == CREATE_SONGLIST_TYPE) {
                                launcherBackground.launch("image/*")
                            } else {
                                Log.d("TAG", "SongListDetailsContent")
                            }
                        }
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (songList?.type == PREFILL_SONGLIST_TYPE) {
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
            softWrap = false,
            overflow = TextOverflow.Ellipsis,

            modifier = Modifier.padding(start = 20.dp),
            maxLines = 1
        )
        Text(
            text = songList.description,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(start = 20.dp)
                .clickable { showDialog = true },
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        PlayButton(playMedia, songList, songs)
        if (showDialog) {
            TextFieldDialog(
                onDismissRequest = { showDialog = false },
                title = stringResource(id = R.string.add_description_text),
                icon = Icons.Filled.Edit,
                confirmOnClick = {
                    if (textDescription != "") {
                        textDescription?.let { updateDescription(it) }
                    }
                    showDialog = false
                },
                dismissOnClick = { showDialog = false },
                content = textDescription!!,
                onValueChange = { textDescription = it }
            )
        }
    }
}

@Composable
fun PlayButton(
    playMedia: (Long, Long) -> Unit,
    songList: SongList,
    songs: List<Song>
) {
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
}

@Composable
fun ShowSongs(
    songs: List<Song>,
    modifier: Modifier,
    top: Int,
    playMedia: ((Long, Long) -> Unit)? = null,
    seekToSong: ((Long) -> Unit)? = null,
    songList: SongList
) {
    val onClick: (Song) -> Unit = if (playMedia != null) { song: Song ->
        playMedia(songList.songListId, song.songId)
    } else { song: Song ->
        seekToSong?.let { it(song.songId) }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = top.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (songs.isEmpty()) {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "暂无歌曲~",
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        } else {
            LazyColumn {
                items(songs,key = {
                    it.songId
                }) { item ->
                    SongItemRow(
                        item, null, onClick = {
                            Log.d("TAG", "ShowSongs: $item")
                            onClick(item)
                        }
                    )
                }
            }
            Text(
                text = "加载到底啦~",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ShowArtistSongs(
    songs: List<Song>,
    modifier: Modifier,
    playMedia: (Long, Long) -> Unit,
    songList: SongList
) {
    Column(modifier = modifier) {
        repeat(songs.size) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(5.dp))
                    .clickable {
                        playMedia(songList.songListId, songs[it].songId)
                    }
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .size(10.dp, 2.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        .zIndex(1f)
                ) {}
                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = songs[it].songTitle,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
