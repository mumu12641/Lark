package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardIcon(
    resourceId:Int,
    contentDescription:String,
    onClick:() -> Unit
){
    Card(
        modifier = Modifier
            .padding(10.dp)
            .size(width = 60.dp, height = 70.dp)
            .clickable (onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painterResource(id = resourceId),
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(50.dp)
                    .padding(5.dp)
            )
            Text(
                text = contentDescription,
                style = MaterialTheme.typography.bodySmall
            )
        }

    }
}
