package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardIcon(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .padding(bottom = 2.dp)
                .size(50.dp),
            shape = CircleShape,

            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    icon,
                    contentDescription = contentDescription,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(5.dp)
                )

            }

        }
        Text(
            text = contentDescription,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }

}

