package io.github.mumu12641.lark.ui.theme.page.settings.diaplay

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.sharp.Bedtime
import androidx.compose.material.icons.sharp.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.mumu12641.lark.*
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.ui.theme.color.palettes.CorePalette
import io.github.mumu12641.lark.ui.theme.component.LarkAlertDialog
import io.github.mumu12641.lark.ui.theme.component.LarkTopBar
import io.github.mumu12641.lark.ui.theme.component.SettingItem
import io.github.mumu12641.lark.ui.theme.component.SettingSwitchItem
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.DEFAULT_SEED_COLOR
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.EMPTY_SEED_COLOR
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.FOLLOW_SYSTEM
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.OFF
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.ON
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.getLanguageConfiguration
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.getLanguageDesc
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.getLanguageNumber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayPage(navController: NavController) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LarkTopBar(
                title = stringResource(id = R.string.display_text),
                scrollBehavior = scrollBehavior,
                navIcon = Icons.Filled.ArrowBack,
                navIconClick = { navController.popBackStack() })
        },
        content = { paddingValues ->
            DisplayPageContent(modifier = Modifier.padding(paddingValues))
        }
    )

}

@SuppressLint("CheckResult")
@Composable
fun DisplayPageContent(modifier: Modifier) {

    var showSwitchDarkModeDialog by remember {
        mutableStateOf(false)
    }
    var showSwitchLanguageDialog by remember {
        mutableStateOf(false)
    }

    val radioOptions = listOf(
        stringResource(id = R.string.follow_system_text),
        stringResource(id = R.string.on_text),
        stringResource(id = R.string.off_text)
    )
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[PreferenceUtil.getDarkMode()]) }

    val languageOptions = listOf(
        stringResource(id = R.string.follow_system_text), stringResource(id = R.string.la_zh_CN),
        stringResource(id = R.string.la_en_US)
    )
    val (selectedLanguageOption, onOptionLanguageSelected) = remember {
        mutableStateOf(
            languageOptions[getLanguageNumber()]
        )
    }

    val switchDynamicColor: (Boolean) -> Unit = {
        if (it) {
            PreferenceUtil.switchDynamicColor(ON)
            PreferenceUtil.changeSeedColor(EMPTY_SEED_COLOR)
        } else {
            PreferenceUtil.switchDynamicColor(OFF)
            PreferenceUtil.changeSeedColor(DEFAULT_SEED_COLOR)
        }
    }

    fun setLanguage(selectedLanguage: Int) {
        PreferenceUtil.setLanguage(selectedLanguage)
        MainActivity.setLanguage(getLanguageConfiguration())
    }

    LazyColumn(modifier) {
        item {
            Box(
                modifier = Modifier
                    .padding(15.dp)
                    .fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {},
                    shape = MaterialTheme.shapes.small,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.unnamed),
                            contentDescription = "failure"
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "SongList sample text",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "SongList description sample text",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                }
            }
        }
        item {
            Column {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 15.dp, vertical = 12.dp)
                ) {
                    ColorCard(Color(DEFAULT_SEED_COLOR))
                    ColorCard(Color(0, 68, 155))
                    ColorCard(Color(220, 123, 88))
                    ColorCard(Color(181, 210, 180))
                    ColorCard(Color(233, 30, 99, 255))
                    ColorCard(Color(255, 235, 59, 255))
                }
            }
        }
        item {
            if (DynamicColorSwitch.current.enable) {
                SettingSwitchItem(
                    title = stringResource(id = R.string.dynamic_text),
                    description = stringResource(id = R.string.dynamic_color_des_text),
                    icon = Icons.Filled.Brightness6,
                    isChecked = DynamicColorSwitch.current.dynamicColorSwitch == ON,
                    switchChange = switchDynamicColor,
                    enable = DynamicColorSwitch.current.enable
                ) {
                }
            }
        }
        item {
            SettingSwitchItem(
                title = stringResource(id = R.string.adaptive_color_text),
                description = stringResource(id = R.string.adaptive_color_des_text),
                icon = Icons.Filled.Colorize,
                isChecked = FollowAlbumSwitch.current == ON,
                switchChange = {
                    if (it) PreferenceUtil.switchFollowAlbum(ON) else PreferenceUtil.switchFollowAlbum(
                        OFF
                    )
                }
            ) {

            }
        }
        item {
            SettingItem(
                title = stringResource(id = R.string.dark_mode_text),
                description = when (LocalDarkTheme.current) {
                    FOLLOW_SYSTEM -> stringResource(id = R.string.follow_system_text)
                    ON -> stringResource(id = R.string.on_text)
                    else -> stringResource(id = R.string.off_text)
                },
                icon = Icons.Sharp.Bedtime
            ) {
                showSwitchDarkModeDialog = true
            }
        }
        item {
            SettingItem(
                title = stringResource(id = R.string.language_desc),
                description = getLanguageDesc(),
                icon = Icons.Sharp.Language
            ) {
                showSwitchLanguageDialog = true
            }
        }
    }
    if (showSwitchDarkModeDialog) {
        RadioOptionsDialog(
            title = stringResource(id = R.string.dark_mode_text),
            radioOptions = radioOptions,
            selectedOption = selectedOption,
            onOptionSelected = onOptionSelected,
            cancelDialog = { showSwitchDarkModeDialog = false }) {
            showSwitchDarkModeDialog = false
            PreferenceUtil.switchDarkMode(radioOptions.indexOf(selectedOption))
        }
    }

    if (showSwitchLanguageDialog) {
        RadioOptionsDialog(
            title = stringResource(id = R.string.language_desc),
            radioOptions = languageOptions,
            selectedOption = selectedLanguageOption,
            onOptionSelected = onOptionLanguageSelected,
            cancelDialog = { showSwitchLanguageDialog = false }) {

            showSwitchLanguageDialog = false
            setLanguage(languageOptions.indexOf(selectedLanguageOption))
        }
    }
}

@Composable
fun RadioOptionsDialog(
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    radioOptions: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    cancelDialog: () -> Unit,
    confirmOnClick: () -> Unit,
) {
    LarkAlertDialog(
        onDismissRequest = { cancelDialog() },
        title = title,
        icon = icon,
        text = {
            Column {
                description?.let { Text(text = it) }
                RadioOptions(radioOptions, onOptionSelected, selectedOption)
            }
        },
        confirmOnClick = { confirmOnClick() },
        confirmText = stringResource(id = R.string.confirm_text),
        dismissButton = {
            TextButton(
                onClick = {
                    cancelDialog()
                }
            ) {
                Text(stringResource(id = R.string.cancel_text))
            }
        },
    )
}

@Composable
private fun RadioOptions(
    radioOptions: List<String>,
    onOptionSelected: (String) -> Unit,
    selectedOption: String
) {
    Column(Modifier.selectableGroup()) {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (text == radioOptions[LocalDarkTheme.current]),
                        onClick = {
                            onOptionSelected(text)
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorCard(color: Color) {

    val corePalette = CorePalette.of(color.toArgb())
    val lightColor = corePalette.a2.tone(80)
    val darkColor = corePalette.a2.tone(60)
    val showColor =
        if (LocalDarkTheme.current == ON || (LocalDarkTheme.current == FOLLOW_SYSTEM && isSystemInDarkTheme())) darkColor else lightColor
    ElevatedCard(
        modifier = Modifier
            .padding(end = 10.dp)
            .size(72.dp),
        onClick = {
            PreferenceUtil.changeSeedColor(color.toArgb())
            PreferenceUtil.switchDynamicColor(OFF)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(showColor)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(
                    visible = Color(LocalSeedColor.current) == color,
                    enter = expandVertically(
                        expandFrom = Alignment.Top
                    ) + fadeIn(
                        initialAlpha = 0.3f
                    ),
                    exit = slideOutVertically() + shrinkVertically() + fadeOut()
                ) {
                    Icon(
                        Icons.Default.Check,
                        modifier = Modifier.size(30.dp),
                        contentDescription = null
                    )
                }
            }
        }
    }
}