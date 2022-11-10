package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.Song

@Composable
fun SongItem(
    song: Song,
    showBottomSheet: ((Song) -> Unit)?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
        colors = CardDefaults.outlinedCardColors()
    ) {
        SongItemRow(song, showBottomSheet = showBottomSheet, onClick = onClick)
    }
}

@Composable
fun SongItemRow(
    song: Song,
    modifier: Modifier = Modifier,
    isCurrentSong: Boolean = false,
    isPlaying:Boolean = false,
    isSelectEnabled: Boolean = false,
    isSelected: Boolean = false,
    onCheckChange: ((Boolean) -> Unit)? = null,
    showBottomSheet: ((Song) -> Unit)?,
    onClick: () -> Unit
) {
    val lottieComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.music_play),
    )
    val lottieAnimationState by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying,
        speed = 1f,
        restartOnPlay = false
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(end = 5.dp, top = 5.dp, bottom = 5.dp)
            .background(Color.Transparent)
            .clip(RoundedCornerShape(5.dp))
            .clickable {
                if (!isSelectEnabled) {
                    onClick()
                } else {
                    onCheckChange!!(!isSelected)
                }
            }
    ) {
        AnimatedVisibility(visible = isSelectEnabled) {
            Checkbox(checked = isSelected, onCheckedChange = {
                onCheckChange?.let { it1 -> it1(it) }
            })
        }
        AsyncImage(
            modifier = Modifier
                .padding(start = 5.dp)
                .size(50.dp)
                .clip(RectangleShape)
                .clip(RoundedCornerShape(10.dp)),
            imageModel = song.songAlbumFileUri,
            failure = R.drawable.ic_baseline_music_note_24
        )

        Column(
            modifier = Modifier
                .padding(10.dp)
                .weight(1f)
        ) {
            MarqueeText(
                text = song.songTitle,
                style = MaterialTheme.typography.bodyLarge,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                gradientEdgeColor = Color.Transparent
            )
            MarqueeText(
                text = song.songSinger,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
                gradientEdgeColor = Color.Transparent
            )
        }
        AnimatedVisibility(visible = isCurrentSong,modifier = Modifier.weight(0.15f)) {
                LottieAnimation(
                    lottieComposition,
                    {lottieAnimationState},
                    modifier = Modifier.fillMaxSize()
                )

        }
        showBottomSheet?.let {
            IconButton(onClick = {
                showBottomSheet(song)
            }, Modifier.weight(0.15f)) {
                Icon(Icons.Filled.MoreVert, contentDescription = "more")
            }
        }
    }
}

