package io.github.mumu12641.lark.ui.theme.page.home

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
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
import io.github.mumu12641.lark.room.DataBaseUtils
import io.github.mumu12641.lark.service.MediaServiceConnection.Companion.EMPTY_PLAYBACK_STATE
import io.github.mumu12641.lark.service.MediaServiceConnection.Companion.NOTHING_PLAYING
import io.github.mumu12641.lark.ui.theme.component.*
import io.github.mumu12641.lark.ui.theme.util.StringUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    metadata: Flow<MediaMetadataCompat>,
    playState: Flow<PlaybackStateCompat>,
    flow: Flow<List<SongList>>,
    reFreshLocalMusicList: () -> Unit,
    addBannerSongToList: (Long) -> Unit,
    addSongList: (SongList) -> Unit
) {

    val allSongList by flow.collectAsState(initial = listOf())
    val currentMetadata by metadata.collectAsState(initial = NOTHING_PLAYING)
    val currentPlayState by playState.collectAsState(initial = EMPTY_PLAYBACK_STATE)
    val artistSongList by mainViewModel.artistSongList.collectAsState(initial = listOf())

    val banner by mainViewModel.bannerState.collectAsState(initial = emptyList())

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                LarkSmallTopBar(
                    title = stringResource(id = R.string.app_name),
                    navIcon = Icons.Filled.Search,
                    navIconClick = { navController.navigate(Route.ROUTE_SEARCH) },
                    actionIcon = Icons.Filled.Settings,
                    singleActionClick = {
                        navController.navigate(Route.ROUTE_SETTING)
                    })
            },
            content = { paddingValues ->
                HomeSetup(
                    modifier = Modifier.padding(paddingValues),
                    mainViewModel,
                    allSongList,
                    artistSongList,
                    navController,
                    banner,
                    reFreshLocalMusicList,
                    addSongList,
                    addBannerSongToList
                ) { mainViewModel.getNeteaseSongList(it) }
            },
            floatingActionButton = {
                FloatingPlayMediaButton(
                    currentMetadata,
                    currentPlayState,
                    onClickNext = { mainViewModel.onSkipToNext() },
                    onClickPause = { mainViewModel.onPause() },
                    onClickPlay = { mainViewModel.onPlay() },
                    onClickPrevious = { mainViewModel.onSkipToPrevious() }) {
                    navController.navigate(Route.ROUTE_PLAY_PAGE)
                }
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
    list: List<SongList>,
    artistSongList: List<SongList>,
    navController: NavController,
    banner: List<BannerX>,
    reFreshLocalMusicList: () -> Unit,
    addSongList: (SongList) -> Unit,
    addBannerSongToList: (Long) -> Unit,
    getNeteaseSongList: (Long) -> Unit
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
            rememberMultiplePermissionsState(permissions = listOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    if (permissionState.allPermissionsGranted) {
        showDialog = false
        HomeContent(
            modifier,
            mainViewModel,
            list,
            artistSongList,
            navController,
            banner,
            addSongList,
            addBannerSongToList
        ) { getNeteaseSongList(it) }
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
                        description = "您可以获取并且播放本地以及网络歌曲。"
                    )
                    IconDescription(
                        icon = Icons.Filled.Backup,
                        description = "登录您的网易云账号，获取个人歌单以及个性化推荐。"
                    )
                    IconDescription(
                        icon = Icons.Filled.PlayArrow,
                        description = "简洁美观的播放界面，无广告无其他冗余功能，带给你最舒适、最纯粹的体验"
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
    list: List<SongList>,
    artistSongList: List<SongList>,
    navController: NavController,
    banner: List<BannerX>,
    addSongList: (SongList) -> Unit,
    addBannerSongToList: (Long) -> Unit,
    getNeteaseSongList: (Long) -> Unit
) {
    val loadState by mainViewModel.loadState.collectAsState()
    Column(
        modifier = modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        WelcomeUser(navController)
        Banner(banner, addBannerSongToList)
        FunctionTab(navController)
        SongListRow(
            list,
            addSongList,
            { getNeteaseSongList(it) }
        ) { navController.navigate(Route.ROUTE_SONG_LIST_DETAILS + it.toString()) }
        ArtistRow(navController, artistSongList)
    }
    if (loadState == Load.LOADING) {
        Dialog(onDismissRequest = {}) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun Banner(
    banner: List<BannerX>,
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
            val song = Song(
                0L,
                banner[page].song.name,
                songSinger = banner[page].song.ar.joinToString(",") { it.name },
                songAlbumFileUri = banner[page].song.al.picUrl,
                mediaFileUri = EMPTY_URI + banner[page].song.al.picUrl,
                duration = 0,
                isBuffered = NOT_BUFFERED,
                neteaseId = banner[page].song.id.toLong(),
            )
            val async = async {
                if (!DataBaseUtils.isNeteaseIdExist(song.neteaseId)) {
                    DataBaseUtils.insertSong(song)
                }
            }
            async.await()
            addBannerSongToList(DataBaseUtils.querySongIdByNeteaseId(song.neteaseId))
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
                                    Toast
                                        .makeText(context, "成功添加到播放列表", Toast.LENGTH_LONG)
                                        .show()
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
//            FilterDrama
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

