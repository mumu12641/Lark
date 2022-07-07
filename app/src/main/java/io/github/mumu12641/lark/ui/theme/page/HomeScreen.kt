package io.github.mumu12641.lark.ui.theme.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.ui.theme.component.CardIcon
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(){

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Scaffold(
            topBar = {
                LarkTopBar()
            },
            content = {
                paddingValues -> HomeContent(
                    modifier = Modifier.padding(paddingValues),
                    listOf(1,2,3,4)
                )
            }
        )
    }

}

@Composable
fun LarkTopBar() {
    MediumTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                fontFamily = FontFamily.Serif
            )
        },
        navigationIcon = {
            IconButton(onClick = {  }) {
                Icon(Icons.Filled.Home, contentDescription = "Home")
            }
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(Icons.Filled.Settings, contentDescription = "Setting")
            }
        }
    )
}

@Composable
fun HomeContent(
    modifier: Modifier,
    list:List<Int>
){
    Column(
        modifier = modifier.padding(horizontal = 10.dp, vertical = 10.dp)
    ){
        WelcomeUser()
        FunctionTab()
        SongListRow(list)
        ArtistRow(list)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtistRow(list: List<Int>) {
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SongListRow(list: List<Int>) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.songList_text),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = FontFamily.Serif
        )
        LazyRow(
            contentPadding = PaddingValues(5.dp)
        ) {
            items(list) {
                Card(
                    modifier = Modifier.padding(5.dp)
                ) {
                    Image(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .padding(5.dp),
                        painter = painterResource(id = R.drawable.favorite),
                        contentDescription = "Test"
                    )
                }
            }
        }
    }
}

@Composable
private fun FunctionTab() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CardIcon(resourceId = R.drawable.history, contentDescription = stringResource(id = R.string.history_text))
        CardIcon(resourceId = R.drawable.file_icon, contentDescription = stringResource(id = R.string.local_text))
        CardIcon(resourceId = R.drawable.download_icon, contentDescription = stringResource(id = R.string.download_text))
        CardIcon(resourceId = R.drawable.cloud_upload, contentDescription = stringResource(id = R.string.cloud_text))
    }
}


@Composable
fun WelcomeUser() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.Face, contentDescription = "Face",
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .size(40.dp)
        )
        Column(
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.welcome_text),
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = stringResource(id = R.string.user),
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily.Serif
            )
        }
    }
}



@Preview
@Composable
fun PreviewTest(){
    HomeScreen()
}