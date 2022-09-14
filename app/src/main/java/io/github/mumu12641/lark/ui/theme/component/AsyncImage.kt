package io.github.mumu12641.lark.ui.theme.component

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.glance.Image
import com.skydoves.landscapist.glide.GlideImage

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
        error = painterResource(failure),
    )

}

@Composable
fun GlideAsyncImage(
    modifier: Modifier,
    imageModel: Any?,
    @DrawableRes failure: Int,
) {
    GlideImage(
        imageModel = imageModel,
        modifier,
        loading = {
            androidx.compose.foundation.Image(
                painter = painterResource(id = failure),
                contentDescription = "",
                modifier = modifier
            )
        },
        failure = {
            androidx.compose.foundation.Image(
                painter = painterResource(id = failure),
                contentDescription = "",
                modifier = modifier
            )
        }
    )
}