package io.github.mumu12641.lark.ui.theme.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LarkTopBar(
    paddingValues: PaddingValues = PaddingValues(),
    title: String,
    navIcon: ImageVector,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navIconClick: () -> Unit
) {
    LargeTopAppBar(
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
        actions = actions,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = Modifier.padding(paddingValues),
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LarkSmallTopBar(
    paddingValues: PaddingValues = PaddingValues(),
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navIcon: ImageVector = Icons.Filled.ArrowBack,
    actionIcon: ImageVector = Icons.Filled.MoreVert,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets.only(WindowInsetsSides.Horizontal),
    navIconClick: () -> Unit,
    actions: (@Composable RowScope.() -> Unit)? = null,
    singleActionClick: (() -> Unit)? = null
) {

    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = navIconClick) {
                Icon(navIcon, contentDescription = "back")
            }
        },
        actions = {
            actions?.let {
                it()
            }
            singleActionClick?.let {
                IconButton(onClick = it) {
                    Icon(actionIcon, contentDescription = "Menu")
                }
            }

        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        windowInsets = windowInsets,
        modifier = Modifier.padding(paddingValues),
    )
}

@SuppressLint("ComposableModifierFactory")
@Composable
fun Modifier.adapterSystemBar() =
    this.padding(
        WindowInsets
            .statusBars
            .only(
                WindowInsetsSides.Horizontal
                        + WindowInsetsSides.Top
            )
            .asPaddingValues()
    )

@Composable
fun adapterSystemPadding(): PaddingValues {
    return WindowInsets
        .statusBars
        .only(
            WindowInsetsSides.Horizontal
                    + WindowInsetsSides.Top
        )
        .asPaddingValues()
}