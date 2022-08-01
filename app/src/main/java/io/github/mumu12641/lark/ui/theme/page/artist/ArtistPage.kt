package io.github.mumu12641.lark.ui.theme.page.artist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.SongList
import io.github.mumu12641.lark.ui.theme.component.ArtistIcon
import io.github.mumu12641.lark.ui.theme.component.LarkSmallTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistPage(
    navController: NavController,
    artistViewModel: ArtistViewModel,
    refreshArtist: () -> Unit,
    navigateToDetail:(Long) -> Unit
) {
    val allArtistSongList by artistViewModel.artistSongList.collectAsState(initial = emptyList())

    Scaffold(
        modifier = Modifier
            .padding(
                WindowInsets
                    .statusBars
                    .only(
                        WindowInsetsSides.Horizontal
                                + WindowInsetsSides.Top
                    )
                    .asPaddingValues()
            ),
        topBar = {
            LarkSmallTopBar(modifier = Modifier.padding(
                WindowInsets
                    .statusBars
                    .only(
                        WindowInsetsSides.Horizontal
                                + WindowInsetsSides.Top
                    ).asPaddingValues()
            ),
                title = stringResource(id = R.string.singer_text),
                navIconClick = { navController.popBackStack() }) {

            }
        },
        content = { paddingValues ->
            ArtistPageContent(modifier = Modifier.padding(paddingValues), list = allArtistSongList){
                navigateToDetail(it)
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = refreshArtist) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
            }
        }
    )
}

@Composable
fun ArtistPageContent(
    modifier: Modifier, list: List<SongList>,navigateToDetail: (Long) -> Unit
) {
    LazyVerticalGrid(modifier = modifier,contentPadding = PaddingValues(5.dp), columns = GridCells.Fixed(2), content = {
        items(list.size) { item ->
            ArtistIcon(modifier = Modifier.size(180.dp), artist = list[item]) {
                navigateToDetail(list[item].songListId)
            }
        }
    })
}