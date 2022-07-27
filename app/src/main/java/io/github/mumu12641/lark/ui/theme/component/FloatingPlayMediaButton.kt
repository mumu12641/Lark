package io.github.mumu12641.lark.ui.theme.component

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mumu12641.lark.R

@Composable
fun FloatingPlayMediaButton(
    currentMetadata: MediaMetadataCompat,
    currentPlayState: PlaybackStateCompat,
    onClickPrevious: () -> Unit,
    onClickPlay: () -> Unit,
    onClickPause: () -> Unit,
    onClickNext: () -> Unit,
    onClickToPlayPage: () -> Unit
) {
    var extend by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .clickable { extend = !extend },
            imageModel = currentMetadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI),
            failure = R.drawable.ic_baseline_music_note_24
        )
        if (extend) {
            Box(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable (onClick = onClickToPlayPage),
            ) {
                Column(Modifier.padding(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = currentMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE),
                            modifier = Modifier.width(200.dp),
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
    }
}