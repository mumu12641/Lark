package io.github.mumu12641.lark.ui.theme.component

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.Route
import io.github.mumu12641.lark.service.MediaServiceConnection.Companion.EMPTY_PLAYBACK_STATE
import io.github.mumu12641.lark.service.MediaServiceConnection.Companion.NOTHING_PLAYING
import io.github.mumu12641.lark.ui.theme.page.home.MainViewModel

@Composable
fun FloatingPlayMediaButton(
    mainViewModel: MainViewModel,
    navController: NavController
) {
    var extend by remember { mutableStateOf(false) }
    val playState by mainViewModel.playState.collectAsState()
    val currentMetadata by playState.currentPlayMetadata.collectAsState(initial = NOTHING_PLAYING)
    val currentPlayState by playState.currentPlayState.collectAsState(initial = EMPTY_PLAYBACK_STATE)
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        val rotation = infiniteRotation(currentPlayState.state == PlaybackStateCompat.STATE_PLAYING)
        AnimatedVisibility(
            visible = extend
        ) {
            ControlPlayBar(
                currentMetadata,
                currentPlayState,
                { navController.navigate(Route.ROUTE_PLAY_PAGE) },
                { mainViewModel.onSkipToPrevious() },
                { mainViewModel.onPause() },
                { mainViewModel.onPlay() },
                { mainViewModel.onSkipToNext() }
            )
        }
        if (currentPlayState.state == PlaybackStateCompat.STATE_BUFFERING) {
            CircularProgressIndicator()
        } else {
            AsyncImage(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .clickable { extend = !extend }
                    .graphicsLayer {
                        rotationZ = rotation.value
                    },
                imageModel = currentMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI),
                failure = R.drawable.ic_baseline_music_note_24
            )
        }
    }
}

@Composable
private fun infiniteRotation(
    startRotate: Boolean,
    duration: Int = 15 * 1000
): Animatable<Float, AnimationVector1D> {
    var rotation by remember { mutableStateOf(Animatable(0f)) }
    LaunchedEffect(key1 = startRotate, block = {
        if (startRotate) {
            rotation.animateTo(
                (rotation.value % 360f) + 360f, animationSpec = infiniteRepeatable(
                    animation = tween(duration, easing = LinearEasing)
                )
            )
        } else {
            rotation.stop()
            rotation = Animatable(rotation.value % 360f)
        }
    })
    return rotation
}

@Composable
private fun ControlPlayBar(
    currentMetadata: MediaMetadataCompat,
    currentPlayState: PlaybackStateCompat,
    onClickToPlayPage: () -> Unit,
    onClickPrevious: () -> Unit,
    onClickPause: () -> Unit,
    onClickPlay: () -> Unit,
    onClickNext: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(end = 10.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable(onClick = onClickToPlayPage),
        contentAlignment = Alignment.Center

    ) {
        Column(Modifier.padding(5.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = currentMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
                    modifier = Modifier.width(150.dp),
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_skip_previous_24),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onClickPrevious),
                    contentDescription = null,
                )
                if (currentPlayState.state == PlaybackStateCompat.STATE_PLAYING) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_pause_24),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onClickPause),
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_play_arrow_24),
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onClickPlay),
                        contentDescription = null
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_skip_next_24),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onClickNext),
                    contentDescription = null
                )
            }

        }
    }
}