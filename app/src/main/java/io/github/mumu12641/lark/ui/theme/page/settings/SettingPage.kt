package io.github.mumu12641.lark.ui.theme.page.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
                title = stringResource(id = R.string.universal_text),
                description = stringResource(id = R.string.universal_desc),
                icon = Icons.Filled.SettingsSuggest
            ) {
                navController.navigate(Route.ROUTE_UNIVERSAL)
            }
        }
        item {
            SettingItem(
                title = stringResource(id = R.string.display_text), description = stringResource(
                    id = R.string.display_des_text
                ), icon = Icons.Filled.Palette
            ) {
                navController.navigate(Route.ROUTE_DISPLAY)
            }
        }
        item {
            SettingItem(
                title = stringResource(id = R.string.about_text), description = stringResource(
                    id = R.string.about_des_text
                ), icon = Icons.Filled.Info
            ) {
                navController.navigate(Route.ROUTE_ABOUT)
            }
        }
    }
}

@Composable
fun PreferenceSubtitle(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 28.dp, bottom = 12.dp),
        color = color,
        style = MaterialTheme.typography.labelLarge
    )
}