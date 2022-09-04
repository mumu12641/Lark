package io.github.mumu12641.lark.ui.theme.page.artist

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.*
import io.github.mumu12641.lark.ui.theme.component.AsyncImage
import io.github.mumu12641.lark.ui.theme.component.LarkSmallTopBar
import io.github.mumu12641.lark.ui.theme.component.TextFieldDialog
import io.github.mumu12641.lark.ui.theme.page.details.PlayButton
import io.github.mumu12641.lark.ui.theme.page.details.ShowArtistSongs
import io.github.mumu12641.lark.ui.theme.page.function.FunctionViewModel
import io.github.mumu12641.lark.ui.theme.page.function.LoadAnimation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailPage(
    navController: NavController,
    artistViewModel: ArtistViewModel,
    playMedia: (Long, Long) -> Unit
) {

    var showResetArtistDialog by remember {
        mutableStateOf(false)
    }
    val scrollBehavior = pinnedScrollBehavior(rememberTopAppBarScrollState())

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LarkSmallTopBar(
                title = "",
                scrollBehavior = scrollBehavior,
                actionIcon = Icons.Filled.Refresh,
                navIconClick = { navController.popBackStack() }) {
                showResetArtistDialog = true
            }
        },
        content = { paddingValues ->
            ArtistDetailContent(
                modifier = Modifier.padding(paddingValues),
                artistViewModel,
                showResetArtistDialog,
                { artistViewModel.setStateToNone() },
                playMedia,
                { showResetArtistDialog = it },
            ) {
                artistViewModel.updateArtistDetail(it)
            }
        },
        backgroundColor = MaterialTheme.colorScheme.background
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ArtistDetailContent(
    modifier: Modifier,
    artistViewModel: ArtistViewModel,
    showResetArtistDialog: Boolean,
    setLoadStateToNone: () -> Unit,
    playMedia: (Long, Long) -> Unit,
    setShowResetArtistDialog: (Boolean) -> Unit,
    updateArtistDetail: (String) -> Unit,
) {
    val songs by artistViewModel.songs.collectAsState(initial = emptyList())
    val songList by artistViewModel.songList.collectAsState(initial = INIT_SONG_LIST)
    val loadState by artistViewModel.loadState.collectAsState(initial = LoadState.None())
    var maxLines by remember {
        mutableStateOf(4)
    }
    var text by remember {
        mutableStateOf("")
    }

//    AnimatedContent(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background),
//        targetState = loadState,
//    ) { targetState ->
    when (loadState) {
        is LoadState.Loading -> {
            Dialog(onDismissRequest = {}) {
                CircularProgressIndicator()
            }
        }
        else -> {
            if (loadState is LoadState.Fail) {
                Toast.makeText(context, loadState.msg, Toast.LENGTH_LONG).show()
                Log.d("TAG", "ArtistDetailContent: " + loadState.msg)
                setLoadStateToNone()
            }
            LazyColumn(modifier = modifier) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(350.dp)
                                .padding(10.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                modifier = Modifier.size(350.dp),
                                imageModel = songList.imageFileUri,
                                failure = R.drawable.ic_baseline_face_24
                            )
                        }
                    }
                }
                item {
                    Text(
                        text = songList.songListTitle,
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                }
                item {
                    PlayButton(playMedia, songList = songList, songs = songs)
                }
                item {
                    Text(
                        text = stringResource(id = R.string.description_text),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(20.dp)
                    )
                }
                item {
                    Column(
                        modifier = Modifier.padding(
                            start = 20.dp,
                            top = 5.dp,
                            end = 20.dp
                        )
                    ) {
                        Text(
                            text = songList.description,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = maxLines,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = if (maxLines == 4) stringResource(id = R.string.show_more_text)
                            else stringResource(id = R.string.fold_text),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier

                                .clickable {
                                    maxLines = if (maxLines == 4) Int.MAX_VALUE
                                    else 4
                                }
                        )
                    }

                }
                item {
                    Text(
                        text = stringResource(id = R.string.song_text),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(20.dp)
                    )
                }
                item {
                    ShowArtistSongs(
                        songs = songs,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                        playMedia = { songListId: Long, songId: Long ->
                            playMedia(songListId, songId)
                        },
                        songList = songList
                    )
                }
            }
        }
    }

//    }


    if (showResetArtistDialog) {
        TextFieldDialog(
            onDismissRequest = { setShowResetArtistDialog(false) },
            title = stringResource(id = R.string.reset_artist_text),
            icon = Icons.Filled.Search,
            placeholder = stringResource(id = R.string.fill_search_text),
            confirmOnClick = {
                if (text != "") {
                    setShowResetArtistDialog(false)
                    updateArtistDetail(text)
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.not_empty_text),
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            dismissOnClick = { setShowResetArtistDialog(false) },
            content = text,
            onValueChange = {
                text = it
            }
        )
    }


}