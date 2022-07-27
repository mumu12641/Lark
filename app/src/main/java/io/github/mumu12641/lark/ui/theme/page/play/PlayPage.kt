package io.github.mumu12641.lark.ui.theme.page.play

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.service.MediaServiceConnection.Companion.EMPTY_PLAYBACK_STATE
import io.github.mumu12641.lark.service.MediaServiceConnection.Companion.NOTHING_PLAYING
import io.github.mumu12641.lark.ui.theme.component.AsyncImage
import io.github.mumu12641.lark.ui.theme.page.home.MainViewModel
import androidx.compose.material3.MaterialTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayPage(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    val currentMetadata =
        mainViewModel.currentPlayMetadata.collectAsState(initial = NOTHING_PLAYING)
    val currentPlayState =
        mainViewModel.currentPlayState.collectAsState(initial = EMPTY_PLAYBACK_STATE)

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    BottomSheetScaffold(
        modifier = Modifier.padding(
            WindowInsets
                .statusBars
                .only(
                    WindowInsetsSides.Horizontal
                            + WindowInsetsSides.Top
                ).asPaddingValues()
        ),
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {

        },
        sheetPeekHeight = 20.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        content = { paddingValues ->
            PlayPageContent(
                modifier = Modifier.padding(paddingValues),
                currentMetadata.value,
                currentPlayState.value
            )
        }
    )
}

@Composable
fun PlayPageContent(
    modifier: Modifier,
    currentMetadata: MediaMetadataCompat,
    currentPlayState: PlaybackStateCompat
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .size(width = 350.dp, height = 300.dp),
                    imageModel = currentMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI),
                    failure = R.drawable.ic_baseline_music_note_24
                )
                Text(
                    text = currentMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 30.dp)
                )
                Text(
                    text = currentMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
            Row(modifier = Modifier.padding(top = 20.dp)) {
                Box(
                    modifier = Modifier
                        .size(width = 250.dp, height = 75.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
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
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_skip_next_24),
                        modifier = Modifier.size(30.dp),
                        contentDescription = "next"
                    )
                }
            }
            Row(modifier = Modifier.padding(top = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .size(width = 75.dp, height = 75.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_skip_previous_24),
                        modifier = Modifier.size(30.dp),
                        contentDescription = "previous"
                    )
                }
                Slider(value = 1f, onValueChange = {})
            }
        }
    }
}

@Composable
fun SeekBar(
    modifier: Modifier = Modifier,
    progress: Float,
    onValueChanged: (Float) -> Unit
) {
    val sliderColors = SliderDefaults.colors(inactiveTrackColor = MaterialTheme.colorScheme.surface)

    Slider(
        modifier = modifier,
        value = progress,
        onValueChange = onValueChanged,
        colors = sliderColors
    )
}
