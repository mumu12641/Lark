package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.mumu12641.lark.ui.theme.applyOpacity


@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector?,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 16.dp)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = if (icon == null) 12.dp else 0.dp)
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
fun SettingSwitchItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector?,
    enable: Boolean = true,
    isChecked: Boolean,
    switchChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = if (enable) modifier.clickable { onClick() } else modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 16.dp)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.secondary.applyOpacity(enable)
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface.applyOpacity(enable)
                )
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.applyOpacity(enable),
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Switch(
                checked = isChecked,
                onCheckedChange = {
                    switchChange(it)
                },
                modifier = Modifier.padding(start = 20.dp, end = 6.dp),
                enabled = enable
            )
        }
    }
}