package io.github.mumu12641.lark.ui.theme.page.settings.diaplay

import android.os.Build
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.sharp.Bedtime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import io.github.mumu12641.lark.LocalDarkTheme
import io.github.mumu12641.lark.LocalSeedColor
import io.github.mumu12641.lark.ui.theme.component.LarkSmallTopBar
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.ui.theme.component.AsyncImage
import io.github.mumu12641.lark.ui.theme.component.LarkTopBar
import io.github.mumu12641.lark.ui.theme.component.SettingItem
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.DARK_MODE_CLOSE
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.DARK_MODE_FOLLOW_SYSTEM
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.DARK_MODE_OPEN
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.DEFAULT_SEED_COLOR
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.FOLLOW_SYSTEM
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.OFF
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.ON

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayPage(navController: NavController) {

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        decayAnimationSpec,
        rememberTopAppBarScrollState(),
        canScroll = { true }
    )

    Box(modifier = Modifier.fillMaxSize()) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayPageContent(modifier: Modifier) {

    var showSwitchDarkModeDialog by remember {
        mutableStateOf(false)
    }

    val radioOptions = listOf(DARK_MODE_FOLLOW_SYSTEM, DARK_MODE_OPEN, DARK_MODE_CLOSE)
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    LazyColumn(modifier) {
        item {
            Box(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {},
                    shape = MaterialTheme.shapes.small,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.album),
                            contentDescription = "failure",
                            modifier = Modifier.size(200.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(10.dp)
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
            LazyRow( modifier = Modifier.padding(top = 0.dp,start = 20.dp)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    item { ColorCard(color = dynamicDarkColorScheme(LocalContext.current).primary) }
                    item { ColorCard(color = dynamicDarkColorScheme(LocalContext.current).tertiary) }
                }
                item { ColorCard(Color(DEFAULT_SEED_COLOR)) }
                item { ColorCard(Color.Blue) }
                item { ColorCard(Color.Cyan) }
                item { ColorCard(Color.Magenta) }
                item { ColorCard(Color.Red) }
            }
        }
        item {
            SettingItem(
                title = stringResource(id = R.string.dark_mode_text),
                description = when (LocalDarkTheme.current) {
                    FOLLOW_SYSTEM -> DARK_MODE_FOLLOW_SYSTEM
                    ON -> DARK_MODE_OPEN
                    else -> DARK_MODE_CLOSE
                },
                icon = Icons.Sharp.Bedtime
            ) {
                showSwitchDarkModeDialog = true
            }
        }
    }
    if (showSwitchDarkModeDialog) {
        AlertDialog(
            onDismissRequest = { showSwitchDarkModeDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSwitchDarkModeDialog = false
                        PreferenceUtil.switchDarkMode(radioOptions.indexOf(selectedOption))
                    },
                ) {
                    Text(stringResource(id = R.string.confirm_text))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSwitchDarkModeDialog = false
                    }
                ) {
                    Text(stringResource(id = R.string.cancel_text))
                }
            },
            title = {
                Text(text = stringResource(id = R.string.dark_mode_text))
            },
            text = {
                Column(Modifier.selectableGroup()) {
                    radioOptions.forEach { text ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = (text == selectedOption),
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
                                onClick = null // null recommended for accessibility with screenreaders
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
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorCard(color: Color) {
    ElevatedCard(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(5.dp))
            .padding(end = 10.dp)
            ,
        onClick = {PreferenceUtil.changeSeedColor(color.toArgb())}
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color),
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


@Preview(showBackground = true)
@Composable
fun PreviewDisplayContent() {
    DisplayPageContent(modifier = Modifier)
}