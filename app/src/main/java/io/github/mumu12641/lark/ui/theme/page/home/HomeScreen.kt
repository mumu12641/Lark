package io.github.mumu12641.lark.ui.theme.page.home

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.skydoves.landscapist.glide.GlideImage
import com.tencent.mmkv.MMKV
import io.github.mumu12641.lark.BaseApplication.Companion.applicationScope
import io.github.mumu12641.lark.MainActivity.Companion.context
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.*
import io.github.mumu12641.lark.entity.network.Banner.BannerX
import io.github.mumu12641.lark.network.NetworkCreator.networkService
import io.github.mumu12641.lark.room.DataBaseUtils
import io.github.mumu12641.lark.ui.theme.component.*
import io.github.mumu12641.lark.ui.theme.page.details.JumpToPlayPageSnackbar
import io.github.mumu12641.lark.ui.theme.page.function.CustomSnackbarVisuals
import io.github.mumu12641.lark.ui.theme.util.StringUtil
import io.github.mumu12641.lark.ui.theme.util.UpdateUtil.RELEASE_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->
                    JumpToPlayPageSnackbar(
                        navController,
                        data,
                        stringResource(id = R.string.successful_add_text),
                        popBackStack = false
                    )
                }
            },
            topBar = {
                LarkSmallTopBar(
                    paddingValues = adapterSystemPadding(),
                    title = stringResource(id = R.string.app_name),
                    navIcon = Icons.Filled.Home,
                    navIconClick = { },
                    actionIcon = Icons.Filled.Settings,
                    singleActionClick = {
                        navController.navigate(Route.ROUTE_SETTING)
                    })
            },
            content = { paddingValues ->
                HomeSetup(
                    modifier = Modifier.padding(paddingValues),
                    mainViewModel,
                    navController,
                    showSnackbar = {
                        scope.launch {
                            snackbarHostState.showSnackbar(CustomSnackbarVisuals(it.songTitle))
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingPlayMediaButton(
                    mainViewModel,
                    navController
                )
            },
            floatingActionButtonPosition = FabPosition.End
        )
    }

}

@SuppressLint("PermissionLaunchedDuringComposition")
@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeSetup(
    modifier: Modifier,
    mainViewModel: MainViewModel,
    navController: NavController,
    showSnackbar: (Song) -> Unit,
) {
    var showDialog by remember {
        mutableStateOf(true)
    }
    val permissionState =
        if (Build.VERSION.SDK_INT >= 33) {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                )
            )
        } else {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_MEDIA_LOCATION,
                )
            )
        }
    if (permissionState.allPermissionsGranted) {
        showDialog = false
        HomeContent(
            modifier,
            mainViewModel,
            navController,
            showSnackbar
        )
    } else {
        showDialog = true
    }
    if (showDialog) {
        LarkAlertDialog(
            onDismissRequest = {},
            title = stringResource(id = R.string.welcome_text),
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    IconDescription(
                        icon = Icons.Filled.MusicNote,
                        description = stringResource(id = R.string.welcome_dialog_1_desc)
                    )
                    IconDescription(
                        icon = Icons.Filled.Backup,
                        description = stringResource(id = R.string.welcome_dialog_2_desc),
                    )
                    IconDescription(
                        icon = Icons.Filled.PlayArrow,
                        description = stringResource(id = R.string.welcome_dialog_3_desc),
                    )
                }
            },
            confirmOnClick = {
                showDialog = false
                permissionState.launchMultiplePermissionRequest()
            },
            confirmText = stringResource(id = R.string.confirm_text)
        )
    }
}


@Composable
fun IconDescription(modifier: Modifier = Modifier, icon: ImageVector, description: String) {
    Row(
        modifier = modifier.padding(top = 12.dp, bottom = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = icon,
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = description,
        )
    }
}


@Composable
fun HomeContent(
    modifier: Modifier,
    mainViewModel: MainViewModel,
    navController: NavController,
    showSnackbar: (Song) -> Unit,
) {
    val loadState by mainViewModel.loadState.collectAsState()
    val uiState by mainViewModel.homeScreenUiState.collectAsState()
    val allSongList by uiState.allSongList.collectAsState(initial = emptyList())
    val artistSongList by uiState.artistSongList.collectAsState(initial = emptyList())
    val banner by mainViewModel.bannerState.collectAsState(initial = emptyList())

    val uriHandler = LocalUriHandler.current
    val checkUpdateState by mainViewModel.checkForUpdate.collectAsState()

    Column(
        modifier = modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        WelcomeUser(navController)
        Banner(banner, showSnackbar) {
            mainViewModel.addSongToCurrentList(it)
        }
        FunctionTab(navController)
        SongListRow(
            allSongList,
            addSongList = { mainViewModel.addSongList(it) },
            { mainViewModel.getNeteaseSongList(it) }
        ) { navController.navigate(Route.ROUTE_SONG_LIST_DETAILS + it.toString()) }
        ArtistRow(navController, artistSongList)
    }
    if (loadState.loadState is LoadState.Loading) {
        AlertDialog(onDismissRequest = { }, confirmButton = {
        }, title = {
            Row {
                Text(text = stringResource(id = R.string.importing_text))
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            }
        }, text = {
            Column {
                LinearProgressIndicator(progress = (loadState.loadState.msg.toFloat() / (loadState.num.toFloat() + 1e-6)).toFloat())
                Text(text = loadState.loadState.msg + "/" + loadState.num.toString())
            }
        })
    }
    if (checkUpdateState.showDialog) {
        LarkAlertDialog(
            onDismissRequest = { mainViewModel.setUpdateDialog() },
            title = checkUpdateState.info.name,
            text = {
                Text(
                    checkUpdateState.info.body
                )
            },
            confirmOnClick = {
                uriHandler.openUri(RELEASE_URL)
            },
            confirmText = stringResource(id = R.string.got_to_update_text),
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun Banner(
    banner: List<BannerX>,
    showSnackbar: (Song) -> Unit,
    addBannerSongToList: (Long) -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = 0
    )
    val modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 5.dp)
        .height(150.dp)
        .clip(
            RoundedCornerShape(20.dp)
        )

    val onClick: (page: Int) -> Unit = { page ->
        applicationScope.launch(Dispatchers.IO) {
            val lyrics = networkService.getLyric(banner[page].song.id.toLong()).lrc.lyric
            val song = Song(
                0L,
                banner[page].song.name,
                songSinger = banner[page].song.ar.joinToString(",") { it.name },
                songAlbumFileUri = banner[page].song.al.picUrl,
                mediaFileUri = EMPTY_URI + banner[page].song.al.picUrl,
                duration = 0,
                isBuffered = NOT_BUFFERED,
                neteaseId = banner[page].song.id.toLong(),
                lyrics = lyrics
            )
            val async = async {
                if (!DataBaseUtils.isNeteaseIdExist(song.neteaseId)) {
                    DataBaseUtils.insertSong(song)
                }
            }
            async.await()
            addBannerSongToList(DataBaseUtils.querySongIdByNeteaseId(song.neteaseId))
            showSnackbar(song)
        }
    }

    if (banner.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        HorizontalPager(
            state = pagerState,
            count = banner.size,
            contentPadding = PaddingValues()
        ) { page ->

            AsyncImage(
                modifier = modifier
                    .graphicsLayer {
                        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
                        lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    },
                imageModel = banner[page].pic,
                failure = R.drawable.lark
            )
            Box(
                modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .height(150.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Card(
                            shape = CircleShape,
                            modifier = Modifier
                                .size(60.dp)
                                .padding(10.dp)
                                .clickable(onClick = {
                                    onClick(page)
//                                    Toast
//                                        .makeText(
//                                            context,
//                                            context.getString(R.string.success_add_to_songlist_text),
//                                            Toast.LENGTH_LONG
//                                        )
//                                        .show()

                                })
                        ) {
                            Row(
                                modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Sharp.PlayArrow,
                                    contentDescription = "Play",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtistRow(navController: NavController, list: List<SongList>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.singer_text),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.see_all_text),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.clickable {
                    navController.navigate(Route.ROUTE_ARTIST_PAGE)
                }
            )
        }
        LazyRow(
            contentPadding = PaddingValues(5.dp)
        ) {
            items(list, key = {
                it.songListId
            }) {
                ArtistIcon(modifier = Modifier.size(150.dp), padding = 5, artist = it) {
                    navController.navigate(Route.ROUTE_ARTIST_DETAIL_PAGE + it.songListId)
                }
            }
        }
    }
}


@Composable
private fun SongListRow(
    list: List<SongList>,
    addSongList: (SongList) -> Unit,
    getNeteaseSongList: (Long) -> Unit,
    navigationToDetails: (Long) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var showNeteaseDialog by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    if (showDialog) {
        AddSongListDialog(
            text = text,
            confirmText = stringResource(id = R.string.confirm_text),
            dismissText = stringResource(id = R.string.import_playlist_text),
            onDismissRequest = { showDialog = false },
            dismissOnClick = {
                showDialog = false
                showNeteaseDialog = true
                text = ""
            },
            confirmOnClick = {
                addSongList(
                    SongList(
                        0L,
                        text,
                        "2022/7/22",
                        0,
                        context.getString(R.string.no_description_text),
                        "null",
                        CREATE_SONGLIST_TYPE
                    )
                )
                showDialog = false
            },
            trailingIconOnClick = { text = "" },
            onValueChange = {
                text = it
            }
        )
    }
    if (showNeteaseDialog) {
        AddSongListDialog(
            icon = Icons.Filled.Link,
            title = stringResource(id = R.string.share_text),
            text = text,
            confirmText = stringResource(id = R.string.import_text),
            dismissText = stringResource(id = R.string.build_own_text),
            onDismissRequest = {
                showNeteaseDialog = false
            },
            dismissOnClick = {
                showDialog = true
                showNeteaseDialog = false
            },
            confirmOnClick = {
                if (StringUtil.getNeteaseSongListId(text) != text) {
                    getNeteaseSongList(StringUtil.getNeteaseSongListId(text)!!.toLong())
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.sry_no_netease_text),
                        Toast.LENGTH_LONG
                    ).show()
                }
                showNeteaseDialog = false
                showDialog = false
            },
            trailingIconOnClick = {
                if (StringUtil.getHttpUrl() != null) {
                    text = StringUtil.getHttpUrl()!!
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.sry_no_url_text),
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            onValueChange = { text = it },
            trailingIcon = Icons.Filled.ContentPaste
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.songList_text),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Filled.Add,
                contentDescription = "More",
                modifier = Modifier.clickable {
                    showDialog = true
                }
            )
        }
        LazyRow(
            contentPadding = PaddingValues(5.dp)
        ) {
            items(list, key = {
                it.songListId
            }) { item ->
                SongListItemCard(songList = item, navigationToDetails)
            }
        }

    }
}

@Composable
private fun AddSongListDialog(
    title: String = context.getString(R.string.add_songlist_text),
    icon: ImageVector = Icons.Filled.Add,
    text: String,
    confirmText: String,
    dismissText: String,
    onDismissRequest: () -> Unit,
    trailingIcon: ImageVector = Icons.Filled.Close,
    dismissOnClick: () -> Unit,
    confirmOnClick: () -> Unit,
    trailingIconOnClick: () -> Unit,
    onValueChange: (String) -> Unit
) {
    TextFieldDialog(
        onDismissRequest = { onDismissRequest() },
        title = title,
        icon = icon,
        trailingIcon = trailingIcon,
        dismissString = dismissText,
        confirmOnClick = { confirmOnClick() },
        confirmString = confirmText,
        dismissOnClick = { dismissOnClick() },
        content = text,
        onValueChange = { onValueChange(it) },
        trailingIconOnClick = {
            trailingIconOnClick()
        }
    )
}


@Composable
private fun FunctionTab(
    navController: NavController
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CardIcon(
            Icons.Rounded.InsertInvitation,
            contentDescription = stringResource(id = R.string.suggestion_text)
        ) {
            navController.navigate(Route.ROUTE_SUGGESTION)
        }
        CardIcon(
            Icons.Rounded.HourglassTop,
            contentDescription = stringResource(id = R.string.history_text)
        ) {
            navController.navigate(Route.ROUTE_HISTORY)
        }
        CardIcon(
            Icons.Rounded.FolderOpen,
            contentDescription = stringResource(id = R.string.local_text)
        ) {
            navController.navigate(Route.ROUTE_LOCAL)
        }
        CardIcon(
            Icons.Rounded.Download,
            contentDescription = stringResource(id = R.string.download_text)
        ) {
            navController.navigate(Route.ROUTE_DOWNLOAD)
        }
        CardIcon(
            Icons.Rounded.Backup,
            contentDescription = stringResource(id = R.string.cloud_text)
        ) {
            navController.navigate(Route.ROUTE_CLOUD)
        }
    }
}


@Composable
fun WelcomeUser(
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            imageModel = MMKV.defaultMMKV().decodeString("iconImageUri"),
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable {
                    navController.navigate(Route.ROUTE_USER)
                },
            failure = {
                Icon(
                    Icons.Filled.Face,
                    contentDescription = "User Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            navController.navigate(Route.ROUTE_USER)
                        }
                )
            }
        )
        Column(
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.welcome_text),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = MMKV.defaultMMKV().decodeString("userName")!!,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

