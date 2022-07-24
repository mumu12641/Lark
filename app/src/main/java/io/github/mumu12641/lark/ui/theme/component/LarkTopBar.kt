package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily

@Composable
fun LarkTopBar(
    title: String,
    navIcon: ImageVector,
    navIconClick: () -> Unit
) {
    MediumTopAppBar(
        title = {
            Text(
                text = title,
                fontFamily = FontFamily.Serif
            )
        },
        navigationIcon = {
            IconButton(onClick =  navIconClick ) {
                Icon(navIcon, contentDescription = title)
            }
        },
        modifier = Modifier.padding(
            WindowInsets
                .statusBars
                .only(WindowInsetsSides.Horizontal
                        + WindowInsetsSides.Top).asPaddingValues()
        )
    )
}