package io.github.mumu12641.lark.ui.theme.page.home

import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.skydoves.landscapist.glide.GlideImage
import com.tencent.mmkv.MMKV
import io.github.mumu12641.lark.BaseApplication.Companion.applicationScope
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.MainActivity
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.*
import io.github.mumu12641.lark.entity.network.BannerX
import io.github.mumu12641.lark.room.DataBaseUtils
import io.github.mumu12641.lark.service.MediaServiceConnection.Companion.EMPTY_PLAYBACK_STATE
import io.github.mumu12641.lark.service.MediaServiceConnection.Companion.NOTHING_PLAYING
import io.github.mumu12641.lark.ui.theme.component.*
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
//                LarkTopBar(
//                    title = stringResource(id = R.string.app_name),
//                    Icons.Filled.Search,
//                    actions = {
//                        IconButton(onClick = { navController.navigate(Route.ROUTE_SETTING) }) {
//                            Icon(Icons.Filled.Settings, contentDescription = "Setting")
//                        }
//                    }
//                ) {
//                    navController.navigate(Route.ROUTE_SEARCH)
//                }
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
                    allSongList,
                    artistSongList,
                    navController,
                    banner,
                    reFreshLocalMusicList,
                    addSongList,
                    addBannerSongToList
                )
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

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeSetup(
    modifier: Modifier,
    list: List<SongList>,
    artistSongList: List<SongList>,
    navController: NavController,
    banner: List<BannerX>,
    reFreshLocalMusicList: () -> Unit,
    addSongList: (SongList) -> Unit,
    addBannerSongToList: (Long) -> Unit,
) {
    var showDialog by remember {
        mutableStateOf(true)
    }
    var request by remember {
        mutableStateOf(false)
    }
    val permissionState =
        rememberPermissionState(permission = android.Manifest.permission.ACCESS_MEDIA_LOCATION)
    if (permissionState.hasPermission) {
        showDialog = false
        HomeContent(
            modifier,
            list,
            artistSongList,
            navController,
            banner,
            addSongList,
            addBannerSongToList
        )
    } else {
        if (showDialog) {
            LarkAlertDialog(
                {},
                stringResource(id = R.string.get_media_permission_text),
                Icons.Filled.Notifications,
                { Text(text = stringResource(id = R.string.request_permission_message_text)) },
                {
                    showDialog = false
                    request = true
                },
                stringResource(id = R.string.confirm_text),
                {
                    TextButton(onClick = {
                        showDialog = false
                        request = true
                    }) { Text(stringResource(id = R.string.cancel_text)) }
                },
            )
        }
        if (request) {
            XXPermissions.with(MainActivity.context)
                .permission(
                    listOf(
                        Permission.ACCESS_MEDIA_LOCATION,
                        Permission.READ_EXTERNAL_STORAGE,
                        Permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                .request { _, all ->
                    if (all) {
                        reFreshLocalMusicList()
                    }
                }
        }
    }


}

@Composable
fun HomeContent(
    modifier: Modifier,
    list: List<SongList>,
    artistSongList: List<SongList>,
    navController: NavController,
    banner: List<BannerX>,
    addSongList: (SongList) -> Unit,
    addBannerSongToList: (Long) -> Unit,
) {


    LazyColumn(
        modifier = modifier.padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        item { WelcomeUser(navController) }
        item { Banner(banner, addBannerSongToList) }
        item { FunctionTab(navController) }
        item {
            SongListRow(
                list,
                addSongList
            ) { navController.navigate(Route.ROUTE_SONG_LIST_DETAILS + it.toString()) }
        }
        item { ArtistRow(navController, artistSongList) }
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
                            modifier = Modifier
                                .size(60.dp)
                                .padding(10.dp)
                                .clickable(onClick = {
                                    onClick(page)
                                    Toast
                                        .makeText(context, "成功添加到播放列表", Toast.LENGTH_LONG)
                                        .show()
                                }),
                                shape = CircleShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtistRow(navController: NavController, list: List<SongList>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.singer_text),
            style = MaterialTheme.typography.titleLarge
        )
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
//            if (list.size == 5 ) {
            item {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        shape = CircleShape,
                        modifier = Modifier
                            .size(150.dp)
                            .padding(5.dp)

                    ) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "more",
                            modifier = Modifier
                                .size(150.dp)
                                .clickable(onClick = {
                                    navController.navigate(Route.ROUTE_ARTIST_PAGE)
                                })
                                .padding(25.dp),
                        )
                    }
//                    }
                }
            }
        }
    }
}


@Composable
private fun SongListRow(
    list: List<SongList>,
    addSongList: (SongList) -> Unit,
    navigationToDetails: (Long) -> Unit
) {

    var showDialog by remember {
        mutableStateOf(false)
    }

    var text by remember {
        mutableStateOf("")
    }

    if (showDialog) {
        TextFieldDialog(
            onDismissRequest = { showDialog = false },
            title = stringResource(id = R.string.add_songlist_text),
            icon = Icons.Filled.Add,
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
                text = ""
            },
            dismissOnClick = { showDialog = false },
            content = text,
            onValueChange = {
                text = it
            }
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
//        OutlinedButton(onClick = { navController.navigate(Route.ROUTE_HISTORY) }) {
//            Row(
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.width((BaseApplication.deviceScreen[0] / 2 - 70).dp)
//            ) {
//                Icon(
//                    modifier = Modifier.size(25.dp),
//                    painter = painterResource(id = R.drawable.history),
//                    contentDescription = "history"
//                )
//                Text(
//                    text = stringResource(id = R.string.history_text),
//                    modifier = Modifier.padding(start = 20.dp, end = 20.dp)
//                )
//            }
//        }
//        OutlinedButton(onClick = { navController.navigate(Route.ROUTE_LOCAL) }) {
//            Row(
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.width((BaseApplication.deviceScreen[0] / 2 - 70).dp)
//            ) {
//                Icon(
//                    modifier = Modifier.size(25.dp),
//                    painter = painterResource(id = R.drawable.file_icon),
//                    contentDescription = "local"
//                )
//                Text(
//                    text = stringResource(id = R.string.local_text),
//                    modifier = Modifier.padding(start = 20.dp, end = 20.dp)
//                )
//            }
//        }


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

