package io.github.mumu12641.lark.ui.theme.page.play

import android.os.Build
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import io.github.mumu12641.lark.*
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.*
import io.github.mumu12641.lark.room.DataBaseUtils
import io.github.mumu12641.lark.service.MediaServiceConnection.Companion.EMPTY_PLAYBACK_STATE
import io.github.mumu12641.lark.ui.theme.PlayPageTheme
import io.github.mumu12641.lark.ui.theme.component.*
import io.github.mumu12641.lark.ui.theme.page.details.ShowSongs
import io.github.mumu12641.lark.ui.theme.page.function.AddToSongListDialog
import io.github.mumu12641.lark.ui.theme.page.function.CreateSongListDialog
import io.github.mumu12641.lark.ui.theme.page.home.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalPagerApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun PlayPage(
    navController: NavController,
    mainViewModel: MainViewModel,
    playViewModel: PlayViewModel
) {
    val homeUiState by mainViewModel.homeScreenUiState.collectAsState()
    val allSongList by homeUiState.allSongList.collectAsState(initial = emptyList())
    val playState by mainViewModel.playState.collectAsState()
    val currentPlaySongs by playState.currentPlaySongs.collectAsState(initial = emptyList())
    val currentSongList by playState.currentSongList.collectAsState(initial = INIT_SONG_LIST)
    val currentPlaySong by playState.currentPlaySong.collectAsState(initial = INIT_SONG)
    val currentPosition by mainViewModel.currentPosition.collectAsState(initial = 0)
    val currentPlayState by playState.currentPlayState.collectAsState(initial = EMPTY_PLAYBACK_STATE)
    val repeatState by mainViewModel.repeatState.collectAsState()
    val playUiState by playViewModel.playUiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showRemindDialog by remember { mutableStateOf(false) }
    var notRemindCheck by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    val pagerState = rememberPagerState()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    var actionMenu by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    BackHandler {
        if (!bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
            scope.launch {
                bottomSheetScaffoldState.bottomSheetState.collapse()
            }
        } else {
            navController.popBackStack()
        }
    }


    PlayPageTheme(
        followAlbumSwitch = FollowAlbumSwitch.current,
        currentAlbumColor = CurrentAlbumColor.current,
        seedColor = LocalSeedColor.current,
        darkTheme = LocalDarkTheme.current,
        dynamicColorEnable = DynamicColorSwitch.current.enable,
        dynamicColor = DynamicColorSwitch.current.dynamicColorSwitch
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            BottomSheetScaffold(
                modifier = Modifier.adapterSystemBar(),
                backgroundColor = MaterialTheme.colorScheme.background,
                topBar = {
                    LarkSmallTopBar(
                        title = "",
                        navIcon = Icons.Filled.ArrowBack,
                        navIconClick = { navController.popBackStack() },
                        actions = {
                            IconButton(onClick = {
                                if (currentPlaySong != INIT_SONG) {
                                    actionMenu = !actionMenu
                                }
                            }) {
                                Icon(
                                    Icons.Filled.MoreVert,
                                    contentDescription = null
                                )
                            }
                            PlayPageMenu(
                                repeatState,
                                actionMenu,
                                scope,
                                currentPlaySong,
                                navController,
                                bottomSheetScaffoldState,
                                pagerState,
                                { actionMenu = it },
                                { showDialog = it },
                                {
                                    showRemindDialog = it
                                    mainViewModel.onSetRepeatMode(it)
                                }
                            )
                        }
                    )
                },
                scaffoldState = bottomSheetScaffoldState,
                sheetContent = {
                    SheetContent(
                        currentPlaySongs,
                        mainViewModel,
                        currentSongList,
                        currentPlaySong,
                        { currentPosition },
                        currentPlayState,
                        playUiState,
                        pagerState,
                        { playViewModel.initData(it) },
                    ) {
                        if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                            scope.launch {
                                bottomSheetScaffoldState.bottomSheetState.expand()
                            }
                        }
                    }
                },
                sheetPeekHeight = 72.dp,
                sheetBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                content = { paddingValues ->
                    if (showDialog) {
                        AddToSongListDialog(
                            allSongList,
                            currentPlaySong,
                            showAddDialogFunction = { showAddDialog = it },
                            showDialogFunction = { showDialog = it }
                        )
                    } else if (showAddDialog) {
                        CreateSongListDialog(
                            text,
                            showAddDialogFunction = { showAddDialog = it },
                            changeText = { text = it })
                    }
                    PlayPageContent(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        navController,
                        currentPlaySong,
                        currentPlayState,
                        { currentPosition },
                        onClickNext = { mainViewModel.onSkipToNext() },
                        onClickPause = { mainViewModel.onPause() },
                        onClickPlay = { mainViewModel.onPlay() },
                        onClickPrevious = { mainViewModel.onSkipToPrevious() }
                    ) { mainViewModel.onSeekTo(it) }
                }
            )
        }
        if (!repeatState.notRemind && showRemindDialog) {
            LarkAlertDialog(
                icon = Icons.Filled.Lightbulb,
                onDismissRequest = { showRemindDialog = false },
                title = stringResource(id = R.string.remind_text),
                text = {
                    Column {
                        Text(stringResource(id = R.string.repeat_one_desc))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material3.Checkbox(
                                checked = notRemindCheck,
                                onCheckedChange = { notRemindCheck = it })
                            Text(
                                text = stringResource(id = R.string.do_not_remind)
                            )
                        }
                    }
                },
                confirmOnClick = {
                    mainViewModel.setRemindDialog(notRemindCheck)
                    showRemindDialog = false
                },
                confirmText = stringResource(id = R.string.confirm_text)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
private fun PlayPageMenu(
    repeatState: MainViewModel.RepeatState,
    actionMenu: Boolean,
    scope: CoroutineScope,
    currentPlaySong: Song,
    navController: NavController,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    pagerState: PagerState,
    setActionMenu: (Boolean) -> Unit,
    setShowDialog: (Boolean) -> Unit,
    setRepeatMode: (Boolean) -> Unit
) {
    val repeatOne = repeatState.repeatOne
    val text = if (repeatOne) R.string.cancel_single_loop_text else R.string.single_cycle_text
    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(
            extraSmall = RoundedCornerShape(18.dp)
        )
    ) {
        DropdownMenu(
            offset = DpOffset(10.dp, 0.dp),
            expanded = actionMenu,
            onDismissRequest = { setActionMenu(false) }) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.add_to_favourite_text)) },
                onClick = {
                    addSongToLike(scope, currentPlaySong)
                }
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.add_to_song_list_text)) },
                onClick = { setShowDialog(true) }
            )
            Divider()
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(
                            id = R.string.view_artist_text
                        )
                    )
                }, onClick = {
                    scope.launch(Dispatchers.IO) {
                        setActionMenu(false)
                        val songListId = DataBaseUtils.querySongListId(
                            currentPlaySong.songSinger
                                .split(",")[0],
                            ARTIST_SONGLIST_TYPE
                        )
                        withContext(Dispatchers.Main) {
                            navController.navigate(Route.ROUTE_ARTIST_DETAIL_PAGE + songListId)
                        }
                    }
                })
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.view_lyrics_text))
                }, onClick = {
                    scope.launch {
                        setActionMenu(false)
                        bottomSheetScaffoldState.bottomSheetState.expand()
                        pagerState.animateScrollToPage(1)
                    }
                })
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.view_playlist_text))
                }, onClick = {
                    scope.launch {
                        setActionMenu(false)
                        bottomSheetScaffoldState.bottomSheetState.expand()
                        pagerState.animateScrollToPage(0)
                    }
                })
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = text))
                },
                onClick = {
                    setRepeatMode(!repeatOne)
                })
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalPagerApi::class
)
@Composable
private fun SheetContent(
    currentPlaySongs: List<Song>,
    mainViewModel: MainViewModel,
    currentSongList: SongList,
    currentPlaySong: Song,
    currentPositionProvider: () -> Long,
    currentPlayState: PlaybackStateCompat,
    playUiState: PlayViewModel.PlayUiState,
    pagerState: PagerState,
    getLyrics: (Long) -> Unit,
    showBottomSheet: () -> Unit,
) {

    val pages = listOf(
        context.getString(R.string.next_to_play_text),
        context.getString(R.string.lyrics_text)
    )
    val scope = rememberCoroutineScope()
    val state = rememberLazyListState()

    Column {
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
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
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.height(60.dp),
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            pages.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        showBottomSheet()
                        scope.launch {
                            pagerState.scrollToPage(index and 1)
                        }
                    },
                )
            }
        }
        HorizontalPager(
            count = pages.size,
            state = pagerState,
        ) { page ->
            when (page) {
                NEXT_TO_PLAY_PAGE -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ScaffoldWithFab(
                            fabPosition = FabPosition.End,
                            onClick = {
                                scope.launch {
                                    state.animateScrollToItem(
                                        currentPlaySongs.indexOf(
                                            currentPlaySong
                                        )
                                    )
                                }
                            },
                            FabContent = {
                                Icon(
                                    Icons.Filled.GpsFixed,
                                    contentDescription = null
                                )
                            }) {
                            ShowSongs(
                                songs = currentPlaySongs,
                                currentPlaySong = currentPlaySong,
                                isPlaying = currentPlayState.state == PlaybackStateCompat.STATE_PLAYING,
                                modifier = Modifier,
                                top = 0,
                                state = state,
                                clipShape = RoundedCornerShape(0.dp),
                                seekToSong = { songId: Long -> mainViewModel.seekToSong(songId) },
                                songList = currentSongList
                            )
                        }
                    }
                }
                LYRICS_PAGE -> {
                    LyricsPage(
                        getLyrics = getLyrics,
                        currentPlaySong = currentPlaySong,
                        playUiState = playUiState,
                        currentPositionProvider = currentPositionProvider
                    ) {
                        mainViewModel.onSeekTo(it)
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
private fun LyricsPage(
    currentPlaySong: Song,
    playUiState: PlayViewModel.PlayUiState,
    currentPositionProvider: () -> Long,
    getLyrics: (Long) -> Unit,
    seekTo: (Long) -> Unit
) {
    val lyricsState = rememberLazyListState()
    val lyrics = playUiState.lyrics
    val loading = playUiState.isLoading
    val timing = playUiState.lyricsTiming
    val scope = rememberCoroutineScope()
    var lyricsIndex by remember { mutableStateOf(0) }

    LaunchedEffect(currentPlaySong.neteaseId) {
        getLyrics(currentPlaySong.neteaseId)
    }

    LaunchedEffect((currentPositionProvider() / 1000L) * 1000L) {
        val fixPosition = (currentPositionProvider() / 1000L) * 1000L
        if (timing.contains(fixPosition)) {
            scope.launch {
                lyricsState.animateScrollToItem(timing.indexOf(fixPosition) + 1)
            }
            lyricsIndex = timing.indexOf(fixPosition)
        }
    }
    ScaffoldWithFab(
        fabPosition = FabPosition.End,
        onClick = { getLyrics(currentPlaySong.neteaseId) },
        FabContent = { Icon(Icons.Filled.Refresh, contentDescription = null) }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            AnimatedContent(targetState = loading) { targetState ->
                when (targetState) {
                    true ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    false ->
                        LyricsContent(lyricsState, lyrics, lyricsIndex) {
                            seekTo(timing[it])
                        }
                }
            }
        }
    }
}

@Composable
private fun LyricsContent(
    lyricsState: LazyListState,
    lyrics: List<String>,
    lyricsIndex: Int,
    clickToSeek: (Int) -> Unit
) {
    LazyColumn(state = lyricsState) {
        itemsIndexed(lyrics) { index, item ->
            val color by animateColorAsState(
                targetValue = if (index == lyricsIndex + 1) MaterialTheme.colorScheme.primary else
                    MaterialTheme.colorScheme.onSecondaryContainer
            )
            if (index >= 1 && item != "") {
                Text(
                    text = item,
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            clickToSeek(index - 1)
                        },
                    color = color,
                    style = if (index == lyricsIndex + 1) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PlayPageContent(
    modifier: Modifier,
    navController: NavController,
    currentPlaySong: Song,
    currentPlayState: PlaybackStateCompat,
    currentPosition: () -> Long,
    onClickPrevious: () -> Unit,
    onClickPlay: () -> Unit,
    onClickPause: () -> Unit,
    onClickNext: () -> Unit,
    onSeekTo: (Long) -> Unit
) {
    val cornerAlbum: Int by animateIntAsState(if (currentPlayState.state == PlaybackStateCompat.STATE_PLAYING) 100 else 50)
    val cornerButton: Int by animateIntAsState(if (currentPlayState.state == PlaybackStateCompat.STATE_PLAYING) 80 else 28)
    val scope = rememberCoroutineScope()



    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (currentPlaySong.songId == -1L || currentPlayState.state == PlaybackStateCompat.STATE_BUFFERING) {
                        Row(
                            modifier = Modifier.size(width = 350.dp, height = 300.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        AsyncImage(
                            modifier = Modifier
                                .clip(RoundedCornerShape(cornerAlbum.dp))
                                .size(width = 350.dp, height = 300.dp)
                                .clickable { },
                            imageModel = currentPlaySong.songAlbumFileUri,
                            failure = R.drawable.ic_baseline_music_note_24
                        )
                    }
                }
                Text(
                    text = currentPlaySong.songTitle,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 30.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = currentPlaySong.songSinger,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .clickable {
                            scope.launch(Dispatchers.IO) {
                                val songListId = DataBaseUtils.querySongListId(
                                    currentPlaySong.songSinger
                                        .split(",")[0],
                                    ARTIST_SONGLIST_TYPE
                                )
                                withContext(Dispatchers.Main) {
                                    navController.navigate(Route.ROUTE_ARTIST_DETAIL_PAGE + songListId)
                                }
                            }
                        },
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Row(modifier = Modifier.padding(top = 30.dp)) {

                Box(
                    modifier = Modifier
                        .size(width = 200.dp, height = 75.dp)
                        .clip(RoundedCornerShape(cornerButton.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable(
                            onClick = if (currentPlayState.state == PlaybackStateCompat.STATE_PLAYING) onClickPause
                            else onClickPlay
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(targetState = currentPlayState, transitionSpec = {
                        slideInVertically(
                            animationSpec = tween(220, delayMillis = 90)
                        ) + fadeIn(
                            animationSpec = tween(
                                220,
                                delayMillis = 90
                            )
                        ) with slideOutVertically(
                            animationSpec = tween(220, delayMillis = 90)
                        ) + fadeOut(
                            animationSpec = tween(
                                90
                            )
                        )
                    }) { targetState ->
                        when (targetState.state) {
                            PlaybackStateCompat.STATE_PLAYING -> Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_pause_24),
                                modifier = Modifier.size(30.dp),
                                contentDescription = "pause"
                            )
                            else -> Icon(
                                Icons.Filled.PlayArrow,
                                modifier = Modifier.size(30.dp),
                                contentDescription = "play"
                            )
                        }

                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(width = 100.dp, height = 75.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clickable(onClick = onClickNext),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_skip_next_24),
                        modifier = Modifier.size(30.dp),
                        contentDescription = "next"
                    )
                }
            }
            Row(
                modifier = Modifier.padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(28.dp))
                        .size(width = 75.dp, height = 75.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clickable(onClick = onClickPrevious),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_skip_previous_24),
                        modifier = Modifier.size(30.dp),
                        contentDescription = "previous"
                    )
                }
                WavySeekbar(
                    modifier = Modifier.padding(start = 5.dp),
                    valueProvider = { currentPosition().toFloat() },
                    onValueChange = {
                        onSeekTo(it.toLong())
                    },
                    valueRange = 0f..currentPlaySong.duration
                        .toFloat(),
                    colors = SliderDefaults.colors(
                        activeTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        thumbColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithFab(
    fabPosition: FabPosition,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    FabContent: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    androidx.compose.material3.Scaffold(
        floatingActionButtonPosition = fabPosition,
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = onClick,
                modifier = modifier,
                content = FabContent
            )
        }
    ) {
        content(it)
    }
}