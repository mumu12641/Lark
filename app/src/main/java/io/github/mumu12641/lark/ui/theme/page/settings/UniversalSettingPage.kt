package io.github.mumu12641.lark.ui.theme.page.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.Audiotrack
import androidx.compose.material.icons.sharp.Autorenew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import io.github.mumu12641.lark.LocalMusicQuality
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.ui.theme.component.LarkTopBar
import io.github.mumu12641.lark.ui.theme.component.SettingItem
import io.github.mumu12641.lark.ui.theme.component.SettingSwitchItem
import io.github.mumu12641.lark.ui.theme.page.home.MainViewModel
import io.github.mumu12641.lark.ui.theme.page.settings.diaplay.RadioOptionsDialog
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.EXHIGH
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.HIGHER
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.HIRES
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.LOSSLESS
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.STANDARD

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalSettingPage(navController: NavController, mainViewModel: MainViewModel) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )
    val repeatMode by mainViewModel.listRepeatState.collectAsState()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LarkTopBar(
                title = stringResource(id = R.string.universal_text),
                scrollBehavior = scrollBehavior,
                navIcon = Icons.Filled.ArrowBack,
                navIconClick = { navController.popBackStack() })
        },
        content = { paddingValues ->
            UniversalSettingPageContent(modifier = Modifier.padding(paddingValues), repeatMode) {
                mainViewModel.onSetPlayListMode(it)
            }
        }
    )
}

@Composable
fun UniversalSettingPageContent(
    modifier: Modifier,
    repeatMode: Boolean,
    setRepeatMode: (Boolean) -> Unit
) {

    val localMusicQuality = LocalMusicQuality.current
    var showMusicLevelDialog by remember {
        mutableStateOf(false)
    }
    val qualityOptions = listOf(
        STANDARD, HIGHER, EXHIGH, LOSSLESS, HIRES
    )
    val (selectedQualityOption, onOptionQualitySelected) = remember {
        mutableStateOf(
            localMusicQuality
        )
    }

    LazyColumn(modifier = modifier) {
        item {
            PreferenceSubtitle(text = stringResource(id = R.string.general_text))
            SettingItem(
                title = stringResource(id = R.string.music_level),
                description = localMusicQuality, icon = Icons.Sharp.Audiotrack
            ) {
                showMusicLevelDialog = true
            }
            SettingSwitchItem(
                title = stringResource(id = R.string.list_play_mode),
                description = stringResource(id = R.string.list_play_mode_desc),
                icon = Icons.Sharp.Autorenew,
                isChecked = repeatMode,
                switchChange = { setRepeatMode(it) }
            ) {

            }
        }
    }

    if (showMusicLevelDialog) {
        RadioOptionsDialog(
            title = stringResource(id = R.string.music_level),
            icon = Icons.Sharp.Audiotrack,
            description = stringResource(id = R.string.music_level_desc),
            radioOptions = qualityOptions,
            selectedOption = selectedQualityOption,
            onOptionSelected = onOptionQualitySelected,
            cancelDialog = { showMusicLevelDialog = false }) {
            showMusicLevelDialog = false
            PreferenceUtil.switchMusicQuality(selectedQualityOption)
        }
    }
}