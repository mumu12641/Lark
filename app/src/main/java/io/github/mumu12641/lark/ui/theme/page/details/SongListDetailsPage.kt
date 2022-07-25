package io.github.mumu12641.lark.ui.theme.page.details

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.ui.theme.component.*
import io.github.mumu12641.lark.ui.theme.page.user.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SongListDetailsPage(
    navController: NavController,
    viewModel: SongListDetailsViewModel,
    songListId:String
) {
    if (viewModel.songList.value == null){
        viewModel.getSongList(songListId.toLong())

    }
    Log.d("TAG", "SongListDetailsPage: $songListId " +viewModel.songList.value.toString())
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    modifier = Modifier.padding(
                        WindowInsets
                            .statusBars
                            .only(
                                WindowInsetsSides.Horizontal
                                    + WindowInsetsSides.Top).asPaddingValues()
                    ),
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            content = {
                paddingValues ->
                viewModel.songList.value?.let { it ->
                    SongListDetailsContent(
                        modifier = Modifier.padding(paddingValues), it
                    ){
                            uri -> viewModel.changeSongListImage(uri)
                    }
                }
            }
        )
    }
    
}

@Composable
fun SongListDetailsContent(
    modifier: Modifier,
    songList:SongList,
    changeSongListImage:(String) -> Unit
) {

    val launcherBackground = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        changeSongListImage(uri.toString())
    }

    Column(
        modifier = modifier.padding(top = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .padding(20.dp)
                    .clip(RectangleShape)
                    .clip(RoundedCornerShape(50.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { launcherBackground.launch("image/*") },
                contentAlignment = Alignment.Center,

            ){
                if (songList.type == 1) {
                    SongListPicture(Modifier.size(300.dp), R.drawable.favorite)
                } else {
                    AsyncImage(
                        modifier = Modifier.size(300.dp),
                        imageModel = songList.imageFileUri,
                        failure = R.drawable.album)
                }
            }
        }
        Text(
            text = songList.songListTitle,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(start = 20.dp)
        )
        Text(
            text = songList.description,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(start = 20.dp)
        )
        Row(modifier = Modifier
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(modifier = Modifier.weight(1f),onClick = { /*TODO*/ }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "play")
                    Text(text  = stringResource(id = R.string.play_all_text))
                }
            }
            Spacer(modifier = Modifier.weight(0.25f))
            Button(modifier = Modifier.weight(1f),onClick = { /*TODO*/ }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = R.drawable.ic_baseline_shuffle_24), contentDescription = "play")
                    Text(text = stringResource(id = R.string.shuffle_text))
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 20.dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val list = listOf(
                Song(0L,"周杰倫 Jay Chou【最偉大的作品 Greatest Works of Art】Official MV",
                    "周杰倫 Jay Chou","content://media/external/audio/albumart/1345866317463737330",
                    "qw",3000),
                Song(0L,"周杰倫 - 暗號 (2002 The One 演唱會_TAIPEI)",
                    "周杰倫 Jay Chou","content://media/external/audio/albumart/6668605274195041921",
                    "qw",3000),
                Song(0L,"周杰倫 - 暗號 (2002 The One 演唱會_TAIPEI)",
                    "周杰倫 Jay Chou","content://media/external/audio/albumart/6668605274195041921",
                    "qw",3000),
                Song(0L,"周杰倫 - 暗號 (2002 The One 演唱會_TAIPEI)",
                    "周杰倫 Jay Chou","content://media/external/audio/albumart/6668605274195041921",
                    "qw",3000)
            )
            LazyColumn{
                items(list){
                    item -> SongItemRow(item)
                }
            }
            Text(text = "加载到底啦~", color = MaterialTheme.colorScheme.onSecondary, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview
@Composable
fun PreviewDetail() {
    Row(modifier = Modifier
        .padding(start = 20.dp, end = 20.dp)
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(modifier = Modifier.weight(1f),onClick = { /*TODO*/ }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "play")
                Text(text = "Play all")
            }
        }
        Spacer(modifier = Modifier.weight(0.25f))
        Button(modifier = Modifier.weight(1f),onClick = { /*TODO*/ }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(id = R.drawable.ic_baseline_shuffle_24), contentDescription = "play")
                Text(text = "Shuffle")
            }
        }
    }
}