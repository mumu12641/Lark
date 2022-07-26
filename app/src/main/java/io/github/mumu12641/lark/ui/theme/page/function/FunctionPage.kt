package io.github.mumu12641.lark.ui.theme.page.function

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import io.github.mumu12641.lark.MainActivity.Companion.context
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.Load
import io.github.mumu12641.lark.entity.LocalSongListId
import io.github.mumu12641.lark.entity.Route
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.ui.theme.component.LarkAlertDialog
import io.github.mumu12641.lark.ui.theme.component.LarkTopBar
import io.github.mumu12641.lark.ui.theme.component.SongItem
import kotlinx.coroutines.launch


@SuppressLint("UnrememberedMutableState")
@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun FunctionPage(
    navController: NavController,
    route: String,
    viewModel: FunctionViewModel,
    playMedia: (Long, Long) -> Unit
) {
    val localMusicList by viewModel.localMusicList.collectAsState(initial = emptyList())
    val loadLocal by viewModel.loadLocal.collectAsState(initial = Load.NONE)
    val scaffoldState by viewModel.bottomSheetScaffoldState.collectAsState(
        initial = BottomSheetState(
            BottomSheetValue.Collapsed
        )
    )
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = scaffoldState
    )
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                Text(text = "test")
            },
            sheetPeekHeight = 0.dp,
            topBar = {
                LarkTopBar(
                    title = route,
                    navIcon = Icons.Filled.ArrowBack
                ) {
                    navController.popBackStack()
                }
            },
            content = when (route) {
                Route.ROUTE_LOCAL -> {
                    { paddingValues ->
                        LocalSetUp(
                            modifier = Modifier.padding(paddingValues),
                            localMusicList,
                            loadLocal,
                            {
                                coroutineScope.launch {
                                    if (viewModel.bottomSheetScaffoldState.value.isCollapsed) {
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


@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("CoroutineCreationDuringComposition", "UnrememberedMutableState")
@Composable
fun LocalSetUp(
    modifier: Modifier,
    localMusicList: List<Song>,
    loadLocal: Int,
    showBottomSheet: () -> Unit,
    playMedia: (Long, Long) -> Unit
) {
    var showDialog by remember {
        mutableStateOf(
            value = !XXPermissions.isGranted(context, Permission.ACCESS_MEDIA_LOCATION)
        )
    }
    var request by remember {
        mutableStateOf(false)
    }
    if (showDialog) {
        LarkAlertDialog(
            {},
            stringResource(id = R.string.get_media_permission_text),
            Icons.Filled.Notifications,
            {
                Text(
                    text = stringResource(id = R.string.request_permission_message_text),
                )
            },
            {
                showDialog = false
                request = true
            },
            {
                showDialog = false
            },
        )
    }
    if (request) {
        XXPermissions.with(context)
            .permission(
                listOf(
                    Permission.ACCESS_MEDIA_LOCATION,
                    Permission.READ_EXTERNAL_STORAGE,
                    Permission.WRITE_EXTERNAL_STORAGE
                )
            )
            .request { _, _ -> }
    }
    if (XXPermissions.isGranted(context, Permission.READ_EXTERNAL_STORAGE) && !showDialog) {
        LocalContent(modifier = modifier, localMusicList, loadLocal,showBottomSheet, playMedia)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
fun LocalContent(
    modifier: Modifier,
    localMusic: List<Song>,
    loadLocal: Int,
    showBottomSheet: () -> Unit,
    playMedia: (Long, Long) -> Unit
) {
    AnimatedContent(
        targetState = loadLocal,
        transitionSpec = {
            slideInVertically { height -> height } + fadeIn() with
                    slideOutVertically { height -> -height } + fadeOut()
        }
    ) { targetState ->
        when (targetState) {
            Load.LOADING -> {
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
            else -> {
                Box(modifier = modifier) {
                    LazyColumn {
                        items(items = localMusic, key = {
                            it.songId
                        }) { song: Song ->
                            SongItem(song = song,showBottomSheet = showBottomSheet) {
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
fun Content(
    modifier: Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.coming_soon_text),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview
@Composable
fun PreviewDialog() {
    AlertDialog(
        onDismissRequest = {

        },
        title = {
            Text(
                text = stringResource(id = R.string.get_media_permission_text)
            )
        },
        icon = { Icon(Icons.Filled.Notifications, contentDescription = null) },
        text = {
            Text(
                text = "Lark will read your phone's media, please allow it!",
            )
        },
        confirmButton = {
            TextButton(
                onClick = {

                },
            ) {
                Text(
                    stringResource(id = R.string.confirm_text)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                }
            ) {
                Text(
                    stringResource(id = R.string.cancel_text),
                )
            }
        }
    )
}