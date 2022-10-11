package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

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
//        Card(
//            modifier = Modifier
//                .padding(bottom = 2.dp)
//                .size(50.dp),
//            shape = CircleShape,
//            ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable(onClick = onClick),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    icon,
//                    contentDescription = contentDescription,
//                    modifier = Modifier
//                        .size(50.dp)
//                        .padding(5.dp)
//                )
//
//            }
//        }
        IconButton(onClick) {
            Icon(
                icon, contentDescription = contentDescription,
                modifier = Modifier
                    .size(50.dp)
            )
        }
        Text(
            text = contentDescription,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }

}

