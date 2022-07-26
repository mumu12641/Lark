package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun LarkAlertDialog(
    onDismissRequest: () -> Unit,
    title: String,
    icon: ImageVector,
    text: @Composable () -> Unit,
    confirmOnClick: () -> Unit,
    confirmText:String,
    dismissButton: @Composable (() -> Unit)? = null,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title
            )
        },
        icon = {
            Icon(icon, contentDescription = title)
        },
        text = text,
        confirmButton = {
            TextButton(
                onClick = confirmOnClick,
            ) {
                Text(confirmText)
            }
        },
        dismissButton = dismissButton

    )
}

