package io.github.mumu12641.lark.ui.theme.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.ui.theme.component.LarkTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalPage(
    navController: NavController
){
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                LarkTopBar(
                    title = stringResource(id = R.string.local_text),
                    navIcon = Icons.Filled.ArrowBack
                ) {
                    navController.popBackStack()
                }
            },
            content = {
                    paddingValues -> LocalContent(
                        modifier = Modifier.padding(paddingValues)
                )
            }
        )
    }
}

@Composable
fun LocalContent(
    modifier: Modifier
){

}

@Preview
@Composable
fun PreviewLocalPage(){
    LocalPage(navController = rememberNavController())
}