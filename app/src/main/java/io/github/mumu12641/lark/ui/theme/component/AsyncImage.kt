package io.github.mumu12641.lark.ui.theme.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun AsyncImage(
    modifier: Modifier,
    imageModel: Any?,
    @DrawableRes failure: Int,
) {
    GlideImage(
        imageModel = imageModel,
        modifier = modifier,
        loading = {
            Box(modifier = Modifier.matchParentSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
//        bitmapPalette = ,
        failure = {
            Image(
                painter = painterResource(id = failure),
                contentDescription = "failure",
                modifier = modifier
            )
        }

    )

}