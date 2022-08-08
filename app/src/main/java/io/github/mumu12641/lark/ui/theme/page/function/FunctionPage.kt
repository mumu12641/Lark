package io.github.mumu12641.lark.ui.theme.page.function

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
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
import com.airbnb.lottie.compose.*
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
    playMedia: (Long, Long) -> Unit
) {
    val localMusicList by viewModel.localMusicList.collectAsState(initial = emptyList())
    val allSongList by viewModel.allSongList.collectAsState(initial = emptyList())
    val loadLocal by viewModel.loadLocal.collectAsState(initial = Load.NONE)
    val currentShowSong by viewModel.currentShowSong.collectAsState(initial = INIT_SONG)
    val bottomSheetScaffoldState =
        rememberBottomSheetScaffoldState(bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed))
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

//    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarScrollState())

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
                                allSongList,
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
                            showDialog,
                            showAddDialog,
                            localMusicList,
                            loadLocal,
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
                else -> {
                    {
                        Content(modifier = Modifier.padding(it))
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    viewModel.reFreshLocalMusicList()
                }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                }
            }
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
    allSongList: List<SongList>,
    coroutineScope: CoroutineScope,
    viewModel: FunctionViewModel,
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


@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
fun LocalContent(
    modifier: Modifier,
    showDialog: Boolean,
    showAddDialog: Boolean,
    localMusic: List<Song>,
    loadLocal: Int,
    showBottomSheet: (Song) -> Unit,
    playMedia: (Long, Long) -> Unit
) {
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
                        items(items = localMusic, key = {
                            it.songId
                        }) { song: Song ->
                            SongItem(song = song, showBottomSheet = showBottomSheet) {
                                playMedia(LocalSongListId, song.songId)
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
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(composition, progress)
    }
}

@Composable
fun Content(
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.coming_soon_text),
            style = MaterialTheme.typography.titleMedium
        )
    }
}


//@RequiresApi(Build.VERSION_CODES.Q)
//@SuppressLint("CoroutineCreationDuringComposition", "UnrememberedMutableState")
//@Composable
//fun LocalSetUp(
//    modifier: Modifier,
//    localMusicList: List<Song>,
//    loadLocal: Int,
//    showBottomSheet: (Song) -> Unit,
//    playMedia: (Long, Long) -> Unit
//) {
//    var showDialog by remember {
//        mutableStateOf(
//            value = !XXPermissions.isGranted(context, Permission.ACCESS_MEDIA_LOCATION)
//        )
//    }
//    var request by remember {
//        mutableStateOf(false)
//    }
//    if (showDialog) {
//        LarkAlertDialog(
//            {},
//            stringResource(id = R.string.get_media_permission_text),
//            Icons.Filled.Notifications,
//            {
//                Text(
//                    text = stringResource(id = R.string.request_permission_message_text),
//                )
//            },
//            {
//                showDialog = false
//                request = true
//            },
//            stringResource(id = R.string.confirm_text),
//            {
//                TextButton(
//                    onClick = {
//                        showDialog = false
//                    }
//                ) {
//                    Text(stringResource(id = R.string.cancel_text))
//                }
//            },
//        )
//    }
//    if (request) {
//        XXPermissions.with(context)
//            .permission(
//                listOf(
//                    Permission.ACCESS_MEDIA_LOCATION,
//                    Permission.READ_EXTERNAL_STORAGE,
//                    Permission.WRITE_EXTERNAL_STORAGE
//                )
//            )
//            .request { _, _ -> }
//    }
//    if (XXPermissions.isGranted(context, Permission.READ_EXTERNAL_STORAGE) && !showDialog) {
//        LocalContent(modifier = modifier, localMusicList, loadLocal, showBottomSheet, playMedia)
//    }
//    Box(
//        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
//    ) {
//
//    }
//}