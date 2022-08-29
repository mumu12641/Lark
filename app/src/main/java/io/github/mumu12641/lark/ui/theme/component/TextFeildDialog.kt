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
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.R

@Composable
fun TextFieldDialog(
    onDismissRequest: () -> Unit,
    title: String,
    placeholder: String? = null,
    icon: ImageVector,
    trailingIcon: ImageVector = Icons.Filled.Close,
    confirmString:String = context.getString(R.string.confirm_text),
    dismissString:String = context.getString(R.string.cancel_text),
    confirmOnClick: () -> Unit,
    dismissOnClick: () -> Unit,
    content: String,
    onValueChange: (String) -> Unit,
    trailingIconOnClick: () -> Unit = { onValueChange("") }
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
                maxLines = 4,
                placeholder = {
                    placeholder?.let {
                        Text(text = placeholder)
                    }
                },
                trailingIcon = {
                    IconButton(onClick = { trailingIconOnClick() }) {
                        Icon(trailingIcon, contentDescription = "close")
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
                Text(confirmString)
            }
        },
        dismissButton = {
            TextButton(
                onClick = dismissOnClick
            ) {
                Text(dismissString)
            }
        }
    )
}