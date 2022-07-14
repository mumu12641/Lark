package io.github.mumu12641.lark.ui.theme.page.function

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import io.github.mumu12641.lark.MainActivity.Companion.context
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.Route
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.ui.theme.component.LarkAlertDialog
import io.github.mumu12641.lark.ui.theme.component.LarkTopBar
import io.github.mumu12641.lark.ui.theme.component.SongItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


@SuppressLint("UnrememberedMutableState")
@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FunctionPage(
    navController: NavController,
    route:String,
    viewModel: FunctionViewModel
){

    val localMusicList : List<Song> by viewModel.getLocalMusic().collectAsState(initial = emptyList())

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                LarkTopBar(
                    title = route,
                    navIcon = Icons.Filled.ArrowBack
                ) {
                    navController.popBackStack()
                }
            },
            content = when(route) {
                Route.ROUTE_LOCAL -> {
                    {
                        paddingValues -> LocalSetUp(
                            modifier = Modifier.padding(paddingValues),localMusicList)
//                        ) {
//                            viewModel.getLocalMusic()
//                        }
                    }
                }
                else -> { {} }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    Toast.makeText(context,"Refresh",Toast.LENGTH_LONG).show()
                }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                }
            }
        )
    }
}


@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("CoroutineCreationDuringComposition", "UnrememberedMutableState")
@Composable
fun LocalSetUp(
    modifier: Modifier,
//    getLocalMusic:() -> Flow<List<Song>>
    localMusic:List<Song>
){
    var showDialog by remember {
        mutableStateOf(
            value = !XXPermissions.isGranted(context,Permission.ACCESS_MEDIA_LOCATION)
        )
    }
    var request by remember {
        mutableStateOf(false)
    }
    if (showDialog) {
        LarkAlertDialog(
            {},
            stringResource(id = R.string.get_media_permission_text),
            Icons.Filled.Notifications,
            stringResource(id = R.string.request_permission_message_text),
            {
                showDialog = false
                request = true
            },
            {
                showDialog = false
            }
        )
    }
    if (request){
        XXPermissions.with(context)
            .permission(
                listOf(
                    Permission.ACCESS_MEDIA_LOCATION,
                    Permission.READ_EXTERNAL_STORAGE,
                    Permission.WRITE_EXTERNAL_STORAGE
                )
            )
            .request { _, _ -> }
    }
    if (XXPermissions.isGranted(context,Permission.ACCESS_MEDIA_LOCATION) && !showDialog ){
        LocalContent(modifier = modifier,localMusic)
    }
}

@SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
@Composable
fun LocalContent(
    modifier: Modifier,
//    getLocalMusic:() -> Flow<List<Song>>
    localMusic: List<Song>
){
//    var localMusicList by mutableStateOf(emptyList<Song>())
//    rememberCoroutineScope().launch {
//        val flow = getLocalMusic()
//        flow.collect{
//            Log.d("TAG", "LocalContent: $it")
//            localMusicList = it
//        }
//    }
    Box(modifier = modifier) {
        LazyColumn {
            items(localMusic) { song: Song ->
                SongItem(song = song)
            }
        }
    }
}

@Composable
fun Content(
    modifier: Modifier
){

}

@Preview
@Composable
fun PreviewDialog(){
    AlertDialog(
        onDismissRequest = {

        },
        title = {
            Text(
                text = stringResource(id = R.string.get_media_permission_text)
            )
        },
        icon = { Icon(Icons.Filled.Notifications, contentDescription = null) },
        text = {
            Text(
                text = "Lark will read your phone's media, please allow it!",
            )
        },
        confirmButton = {
            TextButton(
                onClick = {

                },
            ) {
                Text(
                    stringResource(id = R.string.confirm_text)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                }
            ) {
                Text(
                    stringResource(id = R.string.cancel_text),
                )
            }
        }
    )
}