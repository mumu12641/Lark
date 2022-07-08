package io.github.mumu12641.lark.ui.theme.page.function

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import io.github.mumu12641.lark.entity.Route
import io.github.mumu12641.lark.ui.theme.component.LarkTopBar
import io.github.mumu12641.lark.R

//PermissionX.init(this)
//                .permissions(
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.ACCESS_MEDIA_LOCATION,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                )
//                .request { allGranted, _, _ ->
//                    if (allGranted) {
//                        lifecycleScope.launch (Dispatchers.IO){
//                            getLocalMusic()
//                        }
//                        Toast.makeText(requireContext(),"加载或更新本地歌曲完成",Toast.LENGTH_LONG).show()
//                    } else {
//                        Toast.makeText(requireContext(), "你拒绝了以上权限", Toast.LENGTH_LONG).show()
//                    }
//                }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FunctionPage(
    navController: NavController,
    route:String,
    viewModel: FunctionViewModel
){

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
                    { paddingValues -> LocalContent(
                        modifier = Modifier.padding(paddingValues),
                        viewModel.uiState.value.checkPermission,
                        {
                            viewModel.test()
                        },
                        {
                            viewModel.uiState.value.checkPermission = false
                        }
                    )
                    }
                }
                else -> { {} }
            }
        )
    }
}

@Composable
fun LocalContent(
    modifier: Modifier,
    checkPermission:Boolean,
    confirmClick:()->Unit,
    cancelClick:()->Unit
){
    Log.d("TAG", "LocalContent: $checkPermission")
    if (!checkPermission) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = stringResource(id = R.string.get_media_permission_text)
                )
            },
            icon = { Icon(Icons.Filled.Notifications, contentDescription = null) },
            text = {
                Text(
                    text = stringResource(id = R.string.request_permission_message_text),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = confirmClick,
                ) {
                    Text(stringResource(id = R.string.confirm_text))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = cancelClick
                ) {
                    Text(stringResource(id = R.string.cancel_text))
                }
            }
        )
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
                    "确认"
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                }
            ) {
                Text(
                    "取消",
                )
            }
        }
    )
}