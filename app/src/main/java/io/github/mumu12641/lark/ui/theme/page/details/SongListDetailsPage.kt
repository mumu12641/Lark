package io.github.mumu12641.lark.ui.theme.page.details

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarData
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
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.*
import io.github.mumu12641.lark.ui.theme.component.*
import io.github.mumu12641.lark.ui.theme.page.function.CustomSnackbarVisuals
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SongListDetailsPage(
    navController: NavController,
    viewModel: SongListDetailsViewModel,
    playMedia: (Long, Long) -> Unit
) {
    val uiState by viewModel.songListDetailUiState.collectAsState()
    val songList by uiState.songList.collectAsState(initial = INIT_SONG_LIST)
    val songs by uiState.songs.collectAsState(initial = emptyList())

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }

    LaunchedEffect(Unit) {
        navController.currentBackStackEntryFlow.collect {
            it.arguments?.getString("songListId")?.let { songListId ->
                viewModel.initData(songListId.toLong())
            }
        }
    }
    BackHandler {
        if (!bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
            scope.launch {
                bottomSheetScaffoldState.bottomSheetState.collapse()
            }
        } else {
            navController.popBackStack()
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        BottomSheetScaffold(
            snackbarHost = {
                androidx.compose.material3.SnackbarHost(snackbarHostState) { data ->
                    JumpToPlayPageSnackbar(
                        navController,
                        data,
                        data.visuals.actionLabel.toString()
                    )
                }
            },

            modifier = Modifier.adapterSystemBar(),
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
                    songList = songList
                )
            },
            sheetPeekHeight = 260.dp,
            sheetBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,

            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            topBar = {
                LarkSmallTopBar(title = "", navIconClick = { navController.popBackStack() })
            },
            content = { paddingValues ->
                SongListDetailsContent(
                    modifier = Modifier.padding(paddingValues),
                    songList = songList,
                    songs = songs,
                    isLoading = uiState.isLoading,
                    showSnackbar = { songList, string ->
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                CustomSnackbarVisuals(
                                    songList.songListTitle,
                                    extraMessage = string
                                )
                            )
                        }
                    },
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
fun JumpToPlayPageSnackbar(
    navController: NavController,
    data: SnackbarData,
    string: String
) {
    androidx.compose.material3.Snackbar(
        modifier = Modifier.padding(12.dp),
        action = {
            androidx.compose.material3.TextButton(onClick = {
                navController.popBackStack()
                navController.navigate(Route.ROUTE_PLAY_PAGE)
            }) {
                Text(stringResource(id = R.string.jump_text))
            }
        }) {
        Text(text = string + " " + data.visuals.message)
    }
}

@Composable
fun SongListDetailsContent(
    modifier: Modifier,
    songList: SongList?,
    songs: List<Song>,
    isLoading: Boolean,
    showSnackbar: (SongList, String) -> Unit,
    changeSongListImage: (String) -> Unit,
    updateDescription: (String) -> Unit,
    playMedia: (Long, Long) -> Unit
) {
    val launcherBackground = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            changeSongListImage(it.toString())
        }
    }
    var showDialog by remember { mutableStateOf(false) }
    var textDescription by remember { mutableStateOf(value = songList?.description) }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            androidx.compose.material3.CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = modifier
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(350.dp)
                        .padding(5.dp)
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
                                GlideAsyncImage(
                                    modifier = Modifier.size(350.dp),
                                    imageModel = songList.imageFileUri,
                                    failure = R.drawable.favorite
                                )
                            }
                            SongListPicture(Modifier.size(350.dp), R.drawable.favorite)
                        }
                    } else {
                        GlideAsyncImage(
                            modifier = Modifier.size(350.dp),
                            imageModel = songList!!.imageFileUri,
                            failure = R.drawable.album
                        )
                    }
                }
            }
            Text(
                text = songList!!.songListTitle,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 20.dp, bottom = 20.dp),
                maxLines = 1
            )
            if (songList.description.toList().size <= 14) {
                Text(
                    songList.description,
                    modifier = Modifier
                        .padding(start = 20.dp, bottom = 20.dp, end = 20.dp)
                        .clickable { showDialog = true },
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                MarqueeText(
                    songList.description,
                    modifier = Modifier
                        .padding(start = 20.dp, bottom = 20.dp, end = 20.dp)
                        .clickable { showDialog = true },
                    color = MaterialTheme.colorScheme.primary
                )
            }
            PlayButton(showSnackbar, playMedia, songList, songs)
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
}

@Composable
fun PlayButton(
    showSnackbar: (SongList, String) -> Unit,
    playMedia: (Long, Long) -> Unit,
    songList: SongList,
    songs: List<Song>
) {
    val playInOrder = stringResource(id = R.string.play_in_order_text)
    val playShuffle = stringResource(id = R.string.shuffle_text)
    Row(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = {
                if (songs.isNotEmpty()) {
                    showSnackbar(songList, playInOrder)
                    playMedia(songList.songListId, songs[0].songId)
                }
            }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "play")
                Text(text = stringResource(id = R.string.play_all_text))
            }
        }
        Spacer(modifier = Modifier.weight(0.25f))
        Button(modifier = Modifier.weight(1f), onClick = {
            if (songs.isNotEmpty()) {
                showSnackbar(songList, playShuffle)
                playMedia(
                    songList.songListId,
                    CHANGE_PLAT_LIST_SHUFFLE
                )
            }
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
    state: LazyListState = rememberLazyListState(),
    clipShape: RoundedCornerShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    playMedia: ((Long, Long) -> Unit)? = null,
    seekToSong: ((Long) -> Unit)? = null,
    songList: SongList,
    key: (Song) -> Long = { it.songId }
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
            .clip(clipShape)
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
                    text = stringResource(id = R.string.no_songs_yet),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        } else {
            LazyColumn(state = state) {
                items(songs, key = key) { item ->
                    SongItemRow(
                        item, null, onClick = {
                            onClick(item)
                        }
                    )
                }
            }
            Text(
                text = stringResource(id = R.string.load_to_end_text),
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
