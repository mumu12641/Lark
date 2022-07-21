package io.github.mumu12641.lark.ui.theme.page.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.skydoves.landscapist.glide.GlideImage
import com.tencent.mmkv.MMKV
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.Route
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.ui.theme.component.CardIcon
import io.github.mumu12641.lark.ui.theme.component.LarkTopBar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    flow:Flow<List<SongList>>,
    addSongList: () -> Unit
){

    val allSongList by flow.collectAsState(initial = listOf())

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Scaffold(
            topBar = {
                LarkTopBar(
                    title = stringResource(id = R.string.app_name),
                    Icons.Filled.Home,
                    addSongList
                )
            },
            content = {
                paddingValues -> HomeContent(
                    modifier = Modifier.padding(paddingValues),
                    allSongList,
                    navController
                )
            }
        )
    }

}



@Composable
fun HomeContent(
    modifier: Modifier,
    list:List<SongList>,
    navController: NavController
){
    Column(
        modifier = modifier.padding(horizontal = 10.dp, vertical = 10.dp)
    ){
        WelcomeUser(navController)
        FunctionTab(navController)
        SongListRow(list)
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SongListRow(list: List<SongList>) {
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
        ){
            navController.navigate(Route.ROUTE_HISTORY)
        }
        CardIcon(
            resourceId = R.drawable.file_icon,
            contentDescription = stringResource(id = R.string.local_text)
        ){
            navController.navigate(Route.ROUTE_LOCAL)
        }
        CardIcon(
            resourceId = R.drawable.download_icon,
            contentDescription = stringResource(id = R.string.download_text)
        ) {
            navController.navigate(Route.ROUTE_DOWNLOAD)
        }
        CardIcon(resourceId = R.drawable.cloud_upload,
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
//        Icon(
//            Icons.Filled.Face, contentDescription = "Face",
//            modifier = Modifier
//                .clip(RoundedCornerShape(10.dp))
//                .size(40.dp)
//                .clickable {
//                    navController.navigate(Route.ROUTE_USER)
//                }
//        )
        GlideImage(
            imageModel = MMKV.defaultMMKV().decodeString("iconImageUri"),
            modifier = Modifier.size(40.dp).clip(CircleShape).clickable {
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
                fontFamily = FontFamily.Serif
            )
            Text(
                text = MMKV.defaultMMKV().decodeString("userName")!!,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily.Serif
            )
        }
    }
}



@Preview
@Composable
fun PreviewTest(){
    val flow:Flow<List<SongList>> = flow {
        emit(listOf(SongList(0L,"test","test",0,"test","test")))
    }
    HomeScreen(rememberNavController(),flow){

    }
}