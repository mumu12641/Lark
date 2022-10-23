package io.github.mumu12641.lark.ui.theme.page.function

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.mumu12641.lark.BaseApplication.Companion.applicationScope
import io.github.mumu12641.lark.MainActivity.Companion.context
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.*
import io.github.mumu12641.lark.entity.Route.FUNCTION_ROUTE
import io.github.mumu12641.lark.room.DataBaseUtils
import io.github.mumu12641.lark.ui.theme.component.*
import io.github.mumu12641.lark.ui.theme.page.details.JumpToPlayPageSnackbar
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
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )

    BackHandler {
        if (!bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
            coroutineScope.launch {
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
                SnackbarHost(snackbarHostState) { data ->
                    JumpToPlayPageSnackbar(
                        navController,
                        data,
                        stringResource(id = R.string.now_playing_text)
                    )
                }
            },

            backgroundColor = MaterialTheme.colorScheme.background,
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                SongDetailBottomSheet(song = currentShowSong!!) {
                    showDialog = true
                }
            },
//            sheetPeekHeight = 0.dp,
            sheetBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),

            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),

            topBar = {
                LarkTopBar(
                    title = stringResource(id = FUNCTION_ROUTE[route]!!),
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
                                allSongList,
                                currentShowSong!!,
                                showAddDialogFunction = { showAddDialog = it },
                                showDialogFunction = { showDialog = it }
                            )
                        } else if (showAddDialog) {
                            CreateSongListDialog(
                                text,
                                showAddDialogFunction = { showAddDialog = it },
                                changeText = { text = it })
                        }

                        LocalContent(
                            modifier = Modifier.padding(paddingValues),
                            LocalSongListId,
                            functionUiState,
                            loadState,
                            showSnackbar = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(CustomSnackbarVisuals(it.songTitle))
                                }
                            },
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
                            loadState,
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
            sheetPeekHeight = 0.dp,
            floatingActionButton = {
                when (route) {
                    Route.ROUTE_LOCAL -> {
                        FloatingActionButton(onClick = {
                            viewModel.reFreshLocalMusicList()
                            refreshArtist?.let { it() }
                        }, modifier = Modifier.padding(bottom = 88.dp)) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                        }
                    }
                    Route.ROUTE_HISTORY -> {
                        FloatingActionButton(onClick = {
                            playMedia(
                                HistorySongListId,
                                CHANGE_PLAT_LIST_SHUFFLE
                            )
                        }, modifier = Modifier.padding(bottom = 88.dp)) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "play")
                        }
                    }
                    else -> {
                        FloatingActionButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier.padding(bottom = 88.dp)
                        ) {

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
    loadState: Int,
    showSnackbar: (Song) -> Unit,
    showBottomSheet: ((Song) -> Unit)? = null,
    playMedia: (Long, Long) -> Unit
) {
    val state = rememberLazyListState()
    val localMusicList by uiState.localMusicList.collectAsState(initial = emptyList())
    AnimatedContent(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        targetState = loadState,
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
                    LazyColumn(state = state, modifier = Modifier.Scrollbar(state)) {
                        items(items = localMusicList, key = {
                            it.songId
                        }) { song: Song ->
                            if (showBottomSheet != null) {
                                SongItem(song = song, showBottomSheet = showBottomSheet) {
                                    showSnackbar(song)
                                    playMedia(songListID, song.songId)
                                }
                            } else {
                                SongItem(song = song, showBottomSheet = null) {
                                    showSnackbar(song)
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
    loadState: Int,
    showBottomSheet: ((Song) -> Unit)? = null,
    playMedia: (Long, Long) -> Unit
) {
    val historySongList by uiState.historySongList.collectAsState(initial = emptyList())

    AnimatedContent(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        targetState = loadState,
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
fun CreateSongListDialog(
    text: String,
    showAddDialogFunction: (Boolean) -> Unit,
    changeText: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
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
                        "",
                        "",
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
fun AddToSongListDialog(
    allSongList: List<SongList>,
    currentShowSong: Song,
    showDialogFunction: (Boolean) -> Unit,
    showAddDialogFunction: (Boolean) -> Unit
) {
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
                        applicationScope.launch(Dispatchers.IO) {
                            val id =
                                if (!DataBaseUtils.isNeteaseIdExist(currentShowSong.neteaseId)) {
                                    DataBaseUtils.insertSong(currentShowSong)
                                } else {
                                    DataBaseUtils.querySongIdByNeteaseId(currentShowSong.neteaseId)
                                }
                            if (!DataBaseUtils.isRefExist(item.songListId, id)) {
                                DataBaseUtils.insertRef(PlaylistSongCrossRef(item.songListId, id))
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

class CustomSnackbarVisuals(
    override val message: String,
    private val extraMessage: String? = null,
) : SnackbarVisuals {
    override val actionLabel: String?
        get() = extraMessage
    override val withDismissAction: Boolean
        get() = false
    override val duration: androidx.compose.material3.SnackbarDuration
        get() = androidx.compose.material3.SnackbarDuration.Short
}