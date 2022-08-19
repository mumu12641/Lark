package io.github.mumu12641.lark.ui.theme.page.play

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.mumu12641.lark.BaseApplication
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.INIT_SONG_LIST
import io.github.mumu12641.lark.service.MediaServiceConnection.Companion.EMPTY_PLAYBACK_STATE
import io.github.mumu12641.lark.service.MediaServiceConnection.Companion.NOTHING_PLAYING
import io.github.mumu12641.lark.ui.theme.component.AsyncImage
import io.github.mumu12641.lark.ui.theme.page.details.ShowSongs
import io.github.mumu12641.lark.ui.theme.page.home.MainViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayPage(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    val currentPlaySongs by mainViewModel.currentPlaySongs.collectAsState(initial = emptyList())
    val currentSongList by mainViewModel.currentSongList.collectAsState(initial = INIT_SONG_LIST)
    val currentMetadata =
        mainViewModel.currentPlayMetadata.collectAsState(initial = NOTHING_PLAYING)
    val currentPlayState =
        mainViewModel.currentPlayState.collectAsState(initial = EMPTY_PLAYBACK_STATE)
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BottomSheetScaffold(
            backgroundColor = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .padding(
                    WindowInsets
                        .statusBars
                        .only(
                            WindowInsetsSides.Horizontal
                                    + WindowInsetsSides.Top
                        )
                        .asPaddingValues()
                ),
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                ShowSongs(
                    songs = currentPlaySongs,
                    modifier = Modifier,
                    top = 0,
                    playMedia = { songListId: Long, songId: Long ->
                        mainViewModel.playMedia(songListId, songId)
                    },
                    songList = currentSongList
                )
            },
            sheetPeekHeight = (BaseApplication.deviceScreen[1] - 615).dp,
            sheetBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,

            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            content = { paddingValues ->
                PlayPageContent(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    currentMetadata.value,
                    currentPlayState.value,
                    onClickNext = { mainViewModel.onSkipToNext() },
                    onClickPause = { mainViewModel.onPause() },
                    onClickPlay = { mainViewModel.onPlay() },
                    onClickPrevious = { mainViewModel.onSkipToPrevious() },
                    onSeekTo = { mainViewModel.onSeekTo(it) }
                )
            }
        )
    }
}

@Composable
fun PlayPageContent(
    modifier: Modifier,
    currentMetadata: MediaMetadataCompat,
    currentPlayState: PlaybackStateCompat,
    onClickPrevious: () -> Unit,
    onClickPlay: () -> Unit,
    onClickPause: () -> Unit,
    onClickNext: () -> Unit,
    onSeekTo: (Long) -> Unit
) {
    val cornerAlbum: Int by animateIntAsState(if (currentPlayState.state == PlaybackStateCompat.STATE_PLAYING) 100 else 50)
    val cornerButton: Int by animateIntAsState(if (currentPlayState.state == PlaybackStateCompat.STATE_PLAYING) 80 else 28)

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
                if(currentPlayState.state == PlaybackStateCompat.STATE_BUFFERING){
                    androidx.compose.material3.CircularProgressIndicator()
                }else {
                    AsyncImage(
                        modifier = Modifier
                            .clip(RoundedCornerShape(cornerAlbum.dp))
                            .size(width = 350.dp, height = 300.dp),
                        imageModel = currentMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI),
                        failure = R.drawable.ic_baseline_music_note_24
                    )
                }
                Text(
                    text = currentMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 20.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = currentMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 15.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Row(modifier = Modifier.padding(top = 20.dp)) {
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
//                    Row {
//                        AnimatedVisibility(
//                            visible = currentPlayState.state == PlaybackStateCompat.STATE_PLAYING,
//                            enter =   fadeIn() + scaleIn(),
//                            exit =   fadeOut() + scaleOut()
//                        ) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.ic_baseline_pause_24),
//                                modifier = Modifier.size(30.dp),
//                                contentDescription = "pause"
//                            )
//                        }
//                        AnimatedVisibility(
//                            visible = currentPlayState.state != PlaybackStateCompat.STATE_PLAYING,
//                            enter =   fadeIn() + scaleIn(),
//                            exit =   fadeOut() + scaleOut()
//                        ) {
//                            Icon(
//                                Icons.Filled.PlayArrow,
//                                modifier = Modifier.size(30.dp),
//                                contentDescription = "play"
//                            )
//                        }
//                    }


                    if (currentPlayState.state == PlaybackStateCompat.STATE_PLAYING) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_pause_24),
                            modifier = Modifier.size(30.dp),
                            contentDescription = "pause"
                        )
                    } else {
                        Icon(
                            Icons.Filled.PlayArrow,
                            modifier = Modifier.size(30.dp),
                            contentDescription = "play"
                        )
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
                modifier = Modifier.padding(top = 15.dp),
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
                PlayProgressBar(
                    modifier = Modifier.padding(start = 5.dp),
                    progress = currentPlayState.position.toFloat(),
                    valueRange = 0f..currentMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
                        .toFloat(),
                    onValueChanged = {
                        onSeekTo(it.toLong())
                    },
                    colors = SliderDefaults.colors(activeTrackColor = MaterialTheme.colorScheme.primaryContainer)
                )
            }
        }
    }
}

@Composable
fun PlayProgressBar(
    modifier: Modifier = Modifier,
    progress: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChanged: (Float) -> Unit,
    colors: SliderColors
) {

    Slider(
        modifier = modifier,
        value = progress,
        valueRange = valueRange,
        onValueChange = onValueChanged,
        colors = colors
    )
}
