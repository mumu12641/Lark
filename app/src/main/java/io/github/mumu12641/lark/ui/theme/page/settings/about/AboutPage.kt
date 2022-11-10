package io.github.mumu12641.lark.ui.theme.page.settings.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.mumu12641.lark.BaseApplication.Companion.applicationScope
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.BaseApplication.Companion.version
import io.github.mumu12641.lark.LocalAutoUpdateSwitch
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.network.netease.UpdateInfo
import io.github.mumu12641.lark.ui.theme.component.LarkAlertDialog
import io.github.mumu12641.lark.ui.theme.component.LarkTopBar
import io.github.mumu12641.lark.ui.theme.component.SettingItem
import io.github.mumu12641.lark.ui.theme.component.SettingSwitchItem
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil
import io.github.mumu12641.lark.ui.theme.util.UpdateUtil
import io.github.mumu12641.lark.ui.theme.util.UpdateUtil.checkForUpdate
import io.github.mumu12641.lark.ui.theme.util.UpdateUtil.getUpdateInfo
import io.github.mumu12641.lark.ui.theme.util.suspendToast
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(navController: NavController) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )
    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LarkTopBar(
                title = stringResource(id = R.string.about_text),
                navIcon = Icons.Filled.ArrowBack,
                scrollBehavior = scrollBehavior
            ) {
                navController.popBackStack()
            }
        },
        content = { paddingValues ->
            AboutContent(modifier = Modifier.padding(paddingValues))
        }
    )

}

@Composable
fun AboutContent(modifier: Modifier) {
    val uriHandler = LocalUriHandler.current
    var showThanksDialog by remember {
        mutableStateOf(false)
    }
    val uris = listOf(
        Pair("Seal", "https://github.com/JunkFood02/Seal"),
        Pair(
            "material color utilities",
            "https://github.com/material-foundation/material-color-utilities"
        ),
        Pair("yt_dlp", "https://github.com/yt-dlp/yt-dlp"),
        Pair("youtubedl-android", "https://github.com/yausername/youtubedl-android"),
        Pair("NeteaseCloudMusicApi", "https://github.com/Binaryify/NeteaseCloudMusicApi"),
        Pair("retrofit", "https://github.com/square/retrofit"),
        Pair("RetroMusicPlayer", "https://github.com/RetroMusicPlayer/RetroMusicPlayer"),
        Pair("Howl", "https://github.com/Iamlooker/Howl"),
    )
    var showUpdateDialog by remember {
        mutableStateOf(false)
    }
    var updateInfo by remember {
        mutableStateOf(UpdateInfo())
    }

    LazyColumn(modifier = modifier) {
        item {
            SettingItem(
                title = stringResource(id = R.string.github_text), description = stringResource(
                    id = R.string.github_des_text
                ), icon = Icons.Filled.Description
            ) {
                uriHandler.openUri("https://github.com/mumu12641/Lark")
            }
        }
        item {
            SettingItem(
                title = stringResource(id = R.string.thanks_text), description = stringResource(
                    id = R.string.thanks_des_text
                ), icon = Icons.Filled.AutoAwesome
            ) {
                showThanksDialog = true
            }
        }
        item {
            val localAutoUpdateSwitch = LocalAutoUpdateSwitch.current
            SettingSwitchItem(
                title = stringResource(id = R.string.auto_update),
                description = stringResource(id = R.string.auto_update_desc),
                icon = Icons.Filled.Update,
                isChecked = localAutoUpdateSwitch,
                switchChange = { PreferenceUtil.switchAutoUpdate(it) },
            ) {
                PreferenceUtil.switchAutoUpdate(!localAutoUpdateSwitch)
            }
        }
        item {
            SettingItem(
                title = stringResource(id = R.string.version_text),
                description = version,
                icon = Icons.Filled.Info
            ) {
                applicationScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ ->
                    context.getString(R.string.check_network).suspendToast()
                }) {
                    val info = getUpdateInfo()
                    if (checkForUpdate(info)) {
                        updateInfo = info
                        showUpdateDialog = true
                    } else {
                        context.getString(R.string.already_latest).suspendToast()
                    }
                }
            }
        }
    }
    if (showThanksDialog) {
        AlertDialog(
            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
            onDismissRequest = {
                showThanksDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = { showThanksDialog = false },
                ) {
                    Text(stringResource(id = R.string.confirm_text))
                }
            },
            title = {
                Text(text = stringResource(id = R.string.thanks_text))
            },
            text = {
                LazyColumn {
                    items(uris, key = {
                        it.first
                    }) {
                        Text(text = it.first, modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(5.dp)
                            )
                            .padding(5.dp)
                            .clickable { uriHandler.openUri(it.second) })
                    }
                }
            })
    }
    if (showUpdateDialog) {
        LarkAlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = updateInfo.name,
            text = {
                Text(
                    updateInfo.body
                )
            },
            confirmOnClick = {
                uriHandler.openUri(UpdateUtil.RELEASE_URL)
            },
            confirmText = stringResource(id = R.string.got_to_update_text),
        )
    }
}