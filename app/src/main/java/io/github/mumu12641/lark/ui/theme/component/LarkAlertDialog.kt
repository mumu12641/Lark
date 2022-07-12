package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import io.github.mumu12641.lark.R

@Composable
fun LarkAlertDialog(
    onDismissRequest : () -> Unit,
    title:String,
    icon: ImageVector,
    content:String,
    confirmOnClick : () -> Unit,
    dismissOnClick : () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title
            )
        },
        icon = { Icon(icon, contentDescription = title) },
        text = {
            Text(
                text = content,
            )
        },
        confirmButton = {
            TextButton(
                onClick = confirmOnClick,
            ) {
                Text(stringResource(id = R.string.confirm_text))
            }
        },
        dismissButton = {
            TextButton(
                onClick = dismissOnClick
            ) {
                Text(stringResource(id = R.string.cancel_text))
            }
        }
    )
}