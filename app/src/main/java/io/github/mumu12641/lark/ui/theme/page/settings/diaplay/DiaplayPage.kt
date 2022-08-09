package io.github.mumu12641.lark.ui.theme.page.settings.diaplay

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.mumu12641.lark.ui.theme.component.LarkSmallTopBar
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.ui.theme.component.AsyncImage
import io.github.mumu12641.lark.ui.theme.component.LarkTopBar

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

@Composable
fun DisplayPageContent(modifier: Modifier) {
    LazyColumn(modifier) {
        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(modifier = Modifier
                    .size(300.dp, 400.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { })
                {
                    Image(
                        painter = painterResource(id = R.drawable.album),
                        contentDescription = "failure",
                        modifier = Modifier.size(300.dp)
                    )
                    Column(
                        modifier = Modifier
                            .size(300.dp, 100.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
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
    }
}