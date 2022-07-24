package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import io.github.mumu12641.lark.R

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
                modifier = Modifier.background(Color.Transparent),
                value = content,
                onValueChange = onValueChange,
                trailingIcon = {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(Icons.Filled.Close, contentDescription = "close")
                    }
                },
                colors = textFieldColors(
                    containerColor = Color.Transparent
                )
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