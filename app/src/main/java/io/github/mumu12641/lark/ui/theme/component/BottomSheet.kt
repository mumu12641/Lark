package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheet(
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    coroutineScope: CoroutineScope,
    content: @Composable BoxScope.() -> Unit = {
        Text(text = "hello")
    }
) {
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                content = content
            )
        }, sheetPeekHeight = 0.dp
    ) {
//        Button(onClick = {
//            coroutineScope.launch {
//                if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
//                    bottomSheetScaffoldState.bottomSheetState.expand()
//                } else {
//                    bottomSheetScaffoldState.bottomSheetState.collapse()
//                }
//            }
//        }) {
//            Text(text = "Expand/Collapse Bottom Sheet")
//        }
    }
}