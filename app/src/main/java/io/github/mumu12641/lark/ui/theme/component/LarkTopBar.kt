package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun LarkTopBar(
    title: String,
    navIcon: ImageVector,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navIconClick: () -> Unit
) {
    MediumTopAppBar(
        title = {
            Text(
                text = title
            )
        },
        navigationIcon = {
            IconButton(onClick = navIconClick) {
                Icon(navIcon, contentDescription = title)
            }
        },
        modifier = Modifier.padding(
            WindowInsets
                .statusBars
                .only(
                    WindowInsetsSides.Horizontal
                            + WindowInsetsSides.Top
                ).asPaddingValues()
        ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun LarkSmallTopBar(
    modifier: Modifier = Modifier,
    title: String,
    navIconClick: () -> Unit,
    actionClick: () -> Unit
) {
    SmallTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = navIconClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "back")
            }
        },
        actions = {
            IconButton(onClick = actionClick) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
            }
        }
    )
}