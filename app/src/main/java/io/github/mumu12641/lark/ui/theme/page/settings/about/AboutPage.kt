package io.github.mumu12641.lark.ui.theme.page.settings.about

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.mumu12641.lark.BaseApplication.Companion.version
import io.github.mumu12641.lark.ui.theme.component.LarkSmallTopBar
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.ui.theme.component.LarkTopBar
import io.github.mumu12641.lark.ui.theme.component.SettingItem
import io.github.mumu12641.lark.ui.theme.component.TextFieldDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(navController: NavController) {
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        decayAnimationSpec,
        rememberTopAppBarScrollState(),
        canScroll = { true }
    )
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LarkTopBar(
                    title = stringResource(id = R.string.about_text),
                    navIcon = Icons.Default.ArrowBack,
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
}

@Composable
fun AboutContent(modifier: Modifier) {
    val uriHandler = LocalUriHandler.current
    var showThanksDialog by remember {
        mutableStateOf(false)
    }
//    val map = mapOf(Pair(1, "A"), Pair("2", "B"))
    val uris = listOf(
        Pair("Seal","https://github.com/JunkFood02/Seal"),
        Pair("material color utilities","https://github.com/material-foundation/material-color-utilities"),
        Pair("NeteaseCloudMusicApi","https://github.com/mumu12641/NeteaseCloudMusicApi"),
        Pair("retrofit","https://github.com/square/retrofit")
    )

    LazyColumn(modifier = modifier) {
        item {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "icon",
                    modifier = Modifier.size(150.dp)
                )
            }
        }
        item {
            SettingItem(
                title = stringResource(id = R.string.github_text), description = stringResource(
                    id = R.string.github_des_text
                ), icon = Icons.Default.Description
            ) {
                uriHandler.openUri("https://github.com/mumu12641/Lark")
            }
        }
        item {
            SettingItem(
                title = stringResource(id = R.string.thanks_text), description = stringResource(
                    id = R.string.thanks_des_text
                ), icon = Icons.Default.AutoAwesome
            ) {
                showThanksDialog = true
            }
        }
        item {
            SettingItem(
                title = stringResource(id = R.string.version_text),
                description = version,
                icon = Icons.Default.Settings
            ) {

            }
        }
    }
    if (showThanksDialog) {
        AlertDialog(
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
                    items(uris) {
                        Text(text = it.first, modifier = Modifier.fillMaxWidth().clip(
                            RoundedCornerShape(5.dp)).padding(5.dp).clickable { uriHandler.openUri(it.second) })
                    }
                }
            })
    }
}