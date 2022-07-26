package io.github.mumu12641.lark.ui.theme.page.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.skydoves.landscapist.glide.GlideImage
import com.tencent.mmkv.MMKV
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.Route
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.ui.theme.component.CardIcon
import io.github.mumu12641.lark.ui.theme.component.LarkTopBar
import io.github.mumu12641.lark.ui.theme.component.SongListItemCard
import io.github.mumu12641.lark.ui.theme.component.TextFieldDialog
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    flow: Flow<List<SongList>>,
    addSongList: (SongList) -> Unit
) {

    val allSongList by flow.collectAsState(initial = listOf())

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                LarkTopBar(
                    title = stringResource(id = R.string.app_name),
                    Icons.Filled.Home,
                ) {
                    MainViewModel.playMedia(1L,1L)
                }
            },
            content = { paddingValues ->
                HomeContent(
                    modifier = Modifier.padding(paddingValues),
                    allSongList,
                    navController,
                    addSongList
                )
            }
        )
    }

}


@Composable
fun HomeContent(
    modifier: Modifier,
    list: List<SongList>,
    navController: NavController,
    addSongList: (SongList) -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        WelcomeUser(navController)
        FunctionTab(navController)
        SongListRow(list, addSongList) {
            navController.navigate(Route.ROUTE_SONG_LIST_DETAILS + it.toString())
        }
        ArtistRow(list)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtistRow(list: List<SongList>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.singer_text),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = FontFamily.Serif
        )
        LazyRow(
            contentPadding = PaddingValues(5.dp)
        ) {
            items(list) {
                Card(
                    modifier = Modifier
                        .size(150.dp)
                        .padding(5.dp),
                    shape = CircleShape
                ) {
                    Image(
                        Icons.Filled.Face, contentDescription = "test",
                        modifier = Modifier.size(150.dp)
                    )
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
                addSongList(SongList(0L, text, "2022/7/22", 0, "test", "null", 2))
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
                fontFamily = FontFamily.Serif,
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
        if (list.size == 1) {
            SongListItemCard(list[0], navigationToDetails)
        } else if (list.size > 1) {
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
            resourceId = R.drawable.history,
            contentDescription = stringResource(id = R.string.history_text)
        ) {
            navController.navigate(Route.ROUTE_HISTORY)
        }
        CardIcon(
            resourceId = R.drawable.file_icon,
            contentDescription = stringResource(id = R.string.local_text)
        ) {
            navController.navigate(Route.ROUTE_LOCAL)
        }
        CardIcon(
            resourceId = R.drawable.download_icon,
            contentDescription = stringResource(id = R.string.download_text)
        ) {
            navController.navigate(Route.ROUTE_DOWNLOAD)
        }
        CardIcon(
            resourceId = R.drawable.cloud_upload,
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
            .fillMaxWidth(),
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


@Preview
@Composable
fun PreviewTest() {
}