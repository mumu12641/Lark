package io.github.mumu12641.lark.ui.theme.page.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.Info
import androidx.compose.material.icons.sharp.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.Route
import io.github.mumu12641.lark.ui.theme.component.LarkTopBar
import io.github.mumu12641.lark.ui.theme.component.SettingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingPage(navController: NavController) {


    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            backgroundColor = MaterialTheme.colorScheme.background,
            topBar = {
                LarkTopBar(
                    title = stringResource(id = R.string.setting_text),
                    navIcon = Icons.Filled.ArrowBack
                ) {
                    navController.popBackStack()
                }
            },
            content = { paddingValues ->
                SettingPageContent(modifier = Modifier.padding(paddingValues), navController)
            }
        )
    }
}

@Composable
fun SettingPageContent(modifier: Modifier, navController: NavController) {
    LazyColumn(modifier = modifier) {
        item {
            SettingItem(
                title = stringResource(id = R.string.display_text), description = stringResource(
                    id = R.string.display_des_text
                ), icon = Icons.Sharp.Palette
            ) {
                navController.navigate(Route.ROUTE_DISPLAY)
            }
        }
        item {
            SettingItem(
                title = stringResource(id = R.string.about_text), description = stringResource(
                    id = R.string.about_des_text
                ), icon = Icons.Sharp.Info
            ) {
                navController.navigate(Route.ROUTE_ABOUT)
            }
        }
    }
}