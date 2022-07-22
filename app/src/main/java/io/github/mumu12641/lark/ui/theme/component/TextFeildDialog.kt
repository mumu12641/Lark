package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.DialogProperties
import io.github.mumu12641.lark.R
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import org.w3c.dom.Text

@Preview
@Composable
fun Test() {
    TextFieldDialog(
        onDismissRequest = { /*TODO*/ },
        title = "Test",
        icon = Icons.Default.Notifications,
        content = "",
        confirmOnClick = { /*TODO*/ }) {

    }
}

@Composable
fun TextFieldDialog(
    onDismissRequest : () -> Unit,
    title:String,
    icon: ImageVector,
    content:String,
    confirmOnClick : () -> Unit,
    dismissOnClick : () -> Unit
) {
    var text by remember {
        mutableStateOf("123")
    }
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
                modifier = Modifier,
                value = text,
                onValueChange = {
                    text = it
                },
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