package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import io.github.mumu12641.lark.R

//@Preview
//@Composable
//fun Test() {
//    TextFieldDialog(
//        onDismissRequest = { /*TODO*/ },
//        title = "Test",
//        icon = Icons.Default.Notifications,
//        confirmOnClick = { /*TODO*/ }) {
//
//    }
//}

@Composable
fun TextFieldDialog(
    onDismissRequest : () -> Unit,
    title:String,
    icon: ImageVector,
    confirmOnClick : () -> Unit,
    dismissOnClick : () -> Unit,
    content:String,
    onValueChange:(String) -> Unit
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
            TextField(
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                value = content,
                onValueChange = onValueChange,
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