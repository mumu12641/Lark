package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import io.github.mumu12641.lark.R

@Composable
fun LarkTopBar(
    title: String,
    navIcon: ImageVector,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    colors: TopAppBarColors = TopAppBarDefaults.mediumTopAppBarColors(),
    navIconClick: () -> Unit
) {
    val backgroundColor = colors.containerColor(
        scrollFraction = scrollBehavior?.scrollFraction ?: 0f
    ).value
    Box(
        modifier = Modifier
            .drawBehind { drawRect(backgroundColor) }
            .padding(
                WindowInsets
                    .statusBars
                    .only(
                        WindowInsetsSides.Horizontal
                                + WindowInsetsSides.Top
                    )
                    .asPaddingValues()
            ),
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
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent
            ),
        )
    }
}

@Composable
fun LarkSmallTopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    colors: TopAppBarColors = TopAppBarDefaults.smallTopAppBarColors(),
    navIconClick: () -> Unit,
    actionClick: (() -> Unit)? = null
) {
    var actionMenu by remember {
        mutableStateOf(false)
    }
    val backgroundColor = colors.containerColor(
        scrollFraction = scrollBehavior?.scrollFraction ?: 0f
    ).value
    Box(
        modifier = Modifier
            .drawBehind { drawRect(backgroundColor) }
            .padding(
                WindowInsets
                    .statusBars
                    .only(
                        WindowInsetsSides.Horizontal
                                + WindowInsetsSides.Top
                    )
                    .asPaddingValues()
            ),
    ) {
        SmallTopAppBar(
            title = { Text(text = title) },
            navigationIcon = {
                IconButton(onClick = navIconClick) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "back")
                }
            },
            actions = {
                actionClick?.let {
                    IconButton(onClick = { actionMenu = !actionMenu }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(expanded = actionMenu, onDismissRequest = { actionMenu = false }) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.reset_artist_text)) },
                            onClick = actionClick
                        )
                    }
                }

            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent
            ),
        )
    }
}