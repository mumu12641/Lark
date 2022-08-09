package io.github.mumu12641.lark.ui.theme.page.settings.about

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import io.github.mumu12641.lark.ui.theme.component.LarkSmallTopBar
import io.github.mumu12641.lark.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()){
        Scaffold(
            topBar = {
                LarkSmallTopBar(title = stringResource(id = R.string.about_text), navIconClick = { navController.popBackStack() })
            },
            content = {
                paddingValues ->  AboutContent(modifier = Modifier.padding(paddingValues))
            }
        )
    }
}

@Composable
fun AboutContent(modifier: Modifier) {

}