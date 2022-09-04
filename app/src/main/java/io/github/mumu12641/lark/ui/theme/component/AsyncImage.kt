package io.github.mumu12641.lark.ui.theme.component

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun AsyncImage(
    modifier: Modifier,
    imageModel: Any?,
    @DrawableRes failure: Int,
) {
    coil.compose.AsyncImage(
        model = imageModel,
        placeholder = painterResource(failure),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier,
        error = painterResource(failure)
    )

}