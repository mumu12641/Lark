package io.github.mumu12641.lark.ui.theme.page.function

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarScrollState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.mumu12641.lark.MainActivity.Companion.context
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.*
import io.github.mumu12641.lark.room.DataBaseUtils
import io.github.mumu12641.lark.ui.theme.component.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@SuppressLint("UnrememberedMutableState", "RememberReturnType")
@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FunctionPage(
    navController: NavController,
    route: String,
    viewModel: FunctionViewModel,
    refreshArtist: (() -> Unit)? = null,
    playMedia: (Long, Long) -> Unit,
) {
    val functionUiState by viewModel.functionUiState.collectAsState()
    val allSongList by functionUiState.allSongList.collectAsState(initial = emptyList())
    val currentShowSong by viewModel.currentShowSong.collectAsState(initial = INIT_SONG)
    val loadState by viewModel.loadLocal.collectAsState()

    val bottomSheetScaffoldState =
        rememberBottomSheetScaffoldState(bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed))
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        decayAnimationSpec,
        rememberTopAppBarScrollState(),
        canScroll = { true }
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        BottomSheetScaffold(
            backgroundColor = MaterialTheme.colorScheme.background,
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                SongDetailBottomSheet(song = currentShowSong!!) {
                    showDialog = true
                }
            },
            sheetPeekHeight = 0.dp,
            sheetBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),

            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),

            topBar = {
                LarkTopBar(
                    adapterSystemPadding(),
                    title = route,
                    navIcon = Icons.Filled.ArrowBack,
                    scrollBehavior = scrollBehavior,
                ) {
                    navController.popBackStack()
                }
            },
            content = when (route) {
                Route.ROUTE_LOCAL -> {
                    { paddingValues ->
                        if (showDialog) {
                            AddToSongListDialog(
                                { allSongList },
                                coroutineScope,
                                viewModel,
                                showAddDialogFunction = { showAddDialog = it },
                                showDialogFunction = { showDialog = it }
                            )
                        } else if (showAddDialog) {
                            CreateSongListDialog(
                                coroutineScope,
                                text,
                                showAddDialogFunction = { showAddDialog = it },
                                changeText = { text = it })
                        }

                        LocalContent(
                            modifier = Modifier.padding(paddingValues),
                            LocalSongListId,
                            functionUiState,
                            { loadState },
                            { song ->
                                viewModel.changeCurrentShowSong(song)
                                coroutineScope.launch {
                                    if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                                        bottomSheetScaffoldState.bottomSheetState.expand()
                                    } else {
                                        bottomSheetScaffoldState.bottomSheetState.collapse()
                                    }
                                }
                            },
                            playMedia,
                        )
                    }
                }
                Route.ROUTE_HISTORY -> {
                    {
                        HistoryContent(
                            modifier = Modifier.padding(it),
                            HistorySongListId,
                            functionUiState,
                            { loadState },
                            playMedia = playMedia
                        )
                    }
                }
                else -> {
                    {
                        Box(
                            modifier = Modifier
                                .padding(it)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(id = R.string.coming_soon_text))
                        }
                    }

                }
            },
            floatingActionButton = {
                when (route) {
                    Route.ROUTE_LOCAL -> {
                        FloatingActionButton(onClick = {
                            viewModel.reFreshLocalMusicList()
                            refreshArtist?.let { it() }
                        }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                        }
                    }
                    Route.ROUTE_HISTORY -> {
                        FloatingActionButton(onClick = {
                            playMedia(
                                HistorySongListId,
                                CHANGE_PLAT_LIST_SHUFFLE
                            )
                        }) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "play")
                        }
                    }
                    else -> {
                        FloatingActionButton(onClick = { /*TODO*/ }) {

                        }
                    }
                }
            }
        )
    }
}


@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
fun LocalContent(
    modifier: Modifier,
    songListID: Long,
    uiState: FunctionViewModel.FunctionUiState,
    loadStateProvider: () -> Int,
    showBottomSheet: ((Song) -> Unit)? = null,
    playMedia: (Long, Long) -> Unit
) {

    val localMusicList by uiState.localMusicList.collectAsState(initial = emptyList())
    val loadLocal = loadStateProvider()
    AnimatedContent(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        targetState = loadLocal,
        transitionSpec = {
            slideInVertically { height -> height } + fadeIn() with
                    slideOutVertically { height -> -height } + fadeOut()
        }
    ) { targetState ->
        when (targetState) {
            Load.LOADING -> {
                LoadAnimation(modifier)
            }
            else -> {
                Box(modifier = modifier) {
                    LazyColumn {
                        items(items = localMusicList, key = {
                            it.songId
                        }) { song: Song ->
                            if (showBottomSheet != null) {
                                SongItem(song = song, showBottomSheet = showBottomSheet) {
                                    playMedia(songListID, song.songId)
                                }
                            } else {
                                SongItem(song = song, showBottomSheet = null) {
                                    playMedia(songListID, song.songId)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
fun HistoryContent(
    modifier: Modifier,
    songListID: Long,
    uiState: FunctionViewModel.FunctionUiState,
    loadStateProvider: () -> Int,
    showBottomSheet: ((Song) -> Unit)? = null,
    playMedia: (Long, Long) -> Unit
) {
    val historySongList by uiState.historySongList.collectAsState(initial = emptyList())
    val loadLocal = loadStateProvider()

    AnimatedContent(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        targetState = loadLocal,
        transitionSpec = {
            slideInVertically { height -> height } + fadeIn() with
                    slideOutVertically { height -> -height } + fadeOut()
        }
    ) { targetState ->
        when (targetState) {
            Load.LOADING -> {
                LoadAnimation(modifier)
            }
            else -> {
                Box(modifier = modifier) {
                    LazyColumn {
                        items(items = historySongList, key = {
                            it.songId
                        }) { song: Song ->
                            if (showBottomSheet != null) {
                                SongItem(song = song, showBottomSheet = showBottomSheet) {
                                    playMedia(songListID, song.songId)
                                }
                            } else {
                                SongItem(song = song, showBottomSheet = null) {
                                    playMedia(songListID, song.songId)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadAnimation(modifier: Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .padding(start = 5.dp)
                .size(25.dp)
        )
    }
}


@Composable
private fun CreateSongListDialog(
    coroutineScope: CoroutineScope,
    text: String,
    showAddDialogFunction: (Boolean) -> Unit,
    changeText: (String) -> Unit
) {
    TextFieldDialog(
        onDismissRequest = { showAddDialogFunction(false) },
        title = stringResource(id = R.string.add_songlist_text),
        icon = Icons.Filled.Add,
        confirmOnClick = {
            coroutineScope.launch(Dispatchers.IO) {
                DataBaseUtils.insertSongList(
                    SongList(
                        0L,
                        text,
                        "2022/7/22",
                        0,
                        "test",
                        "null",
                        2
                    )
                )
            }
            showAddDialogFunction(false)
            changeText("")
        },
        dismissOnClick = { showAddDialogFunction(false) },
        content = text,
        onValueChange = {
            changeText(it)
        }
    )
}

@Composable
private fun AddToSongListDialog(
    allSongListProvider: () -> List<SongList>,
    coroutineScope: CoroutineScope,
    viewModel: FunctionViewModel,
    showDialogFunction: (Boolean) -> Unit,
    showAddDialogFunction: (Boolean) -> Unit
) {
    val allSongList = allSongListProvider()
    LarkAlertDialog(
        onDismissRequest = { showDialogFunction(false) },
        title = stringResource(id = R.string.add_to_song_list_text),
        icon = Icons.Filled.Add,
        text = {
            LazyColumn {
                items(items = allSongList, key = {
                    it.songListId
                }) { item: SongList ->
                    SongListItemRow(item) {
                        coroutineScope.launch(Dispatchers.IO) {
                            if (!DataBaseUtils.isRefExist(
                                    item.songListId,
                                    viewModel.currentShowSong.value!!.songId
                                )
                            ) {
                                DataBaseUtils.insertRef(
                                    PlaylistSongCrossRef(
                                        item.songListId,
                                        viewModel.currentShowSong.value!!.songId
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
                        showDialogFunction(false)
                    }
                }
            }
        },
        confirmOnClick = {
            showDialogFunction(false)
            showAddDialogFunction(true)
        },
        confirmText = stringResource(id = R.string.create_song_Llst_text)
    )
}