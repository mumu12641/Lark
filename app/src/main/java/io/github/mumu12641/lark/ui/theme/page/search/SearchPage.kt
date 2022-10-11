package io.github.mumu12641.lark.ui.theme.page.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.LoadState
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.ui.theme.component.SongDetailBottomSheet
import io.github.mumu12641.lark.ui.theme.component.SongItemRow
import io.github.mumu12641.lark.ui.theme.component.adapterSystemBar
import io.github.mumu12641.lark.ui.theme.page.function.AddToSongListDialog
import io.github.mumu12641.lark.ui.theme.page.function.CreateSongListDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchPage(
    navController: NavController,
    searchViewModel: SearchViewModel,
    addBannerSongToList: (Long) -> Unit,
) {
    val bottomSheetScaffoldState =
        rememberBottomSheetScaffoldState(bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed))
    val currentShowSong by searchViewModel.currentShowSong.collectAsState()
    val allSongList by searchViewModel.allSongList.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        BottomSheetScaffold(
            modifier = Modifier.adapterSystemBar(),
            sheetContent = {
                SongDetailBottomSheet(currentShowSong) {
                    showDialog = true
                }
            },
            scaffoldState = bottomSheetScaffoldState,
            sheetPeekHeight = 0.dp,
            sheetBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            backgroundColor = MaterialTheme.colorScheme.background,
            topBar = {
                SearchTopBar(navController, searchViewModel)
            },
            content = { paddingValues ->
                if (showDialog) {
                    AddToSongListDialog(
                        allSongList,
                        currentShowSong,
                        showAddDialogFunction = { showAddDialog = it },
                        showDialogFunction = { showDialog = it }
                    )
                } else if (showAddDialog) {
                    CreateSongListDialog(
                        text,
                        showAddDialogFunction = { showAddDialog = it },
                        changeText = { text = it })
                }

                SearchPageContent(
                    modifier = Modifier.padding(paddingValues),
                    searchViewModel,
                    addBannerSongToList
                ) {
                    scope.launch {
                        bottomSheetScaffoldState.bottomSheetState.expand()
                    }
                }
            }
        )
    }
}

@Composable
private fun SearchTopBar(
    navController: NavController,
    searchViewModel: SearchViewModel
) {
    Surface(
        elevation = 10.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 20.dp)
                    .clip(CircleShape)
                    .clickable { navController.popBackStack() })
            SearchTextInput(searchViewModel = searchViewModel)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SearchPageContent(
    modifier: Modifier,
    searchViewModel: SearchViewModel,
    addBannerSongToList: (Long) -> Unit,
    showBottomSheet: () -> Unit,
) {

    val hotSearchWord by searchViewModel.hotSearchWord.collectAsState(initial = emptyList())
    val loadState by searchViewModel.loadState.collectAsState()
    val searchSongList by searchViewModel.searchSongList.collectAsState(initial = emptyList())


    Column(modifier = modifier) {

        AnimatedContent(targetState = loadState) { targetState: LoadState ->
            if (targetState is LoadState.Loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.CircularProgressIndicator()
                }
            } else {
                ShowSearchSongs(searchSongList) {
//                    BaseApplication.applicationScope.launch(Dispatchers.IO) {
//                        val async = async {
//                            if (!DataBaseUtils.isNeteaseIdExist(it.neteaseId)) {
//                                DataBaseUtils.insertSong(it)
//                            }
//                        }
//                        async.await()
//                        addBannerSongToList(DataBaseUtils.querySongIdByNeteaseId(it.neteaseId))
//                    }
                    searchViewModel.setCurrentShowSong(it)
                    showBottomSheet()
                }
            }
        }

//        item {
//            Text(
//                text = stringResource(id = R.string.hot_search_text),
//                modifier = Modifier.padding(horizontal = 10.dp, vertical = 20.dp),
//                style = MaterialTheme.typography.titleMedium
//            )
//        }
//        repeat(hotSearchWord.size) {
//            item {
//                Row(
//                    modifier = Modifier
//                        .clip(RoundedCornerShape(15.dp))
//                        .padding(5.dp)
//                        .clickable { },
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.Center
//                ) {
//                    Text(
//                        text = (it + 1).toString(),
//                        color = if (it <= 2) MaterialTheme.colorScheme.primary else Color.Unspecified,
//                        modifier = Modifier.padding(10.dp),
//                        style = MaterialTheme.typography.titleSmall
//                    )
//                    Text(
//                        text = hotSearchWord[it].hotSearchWord,
//                        modifier = Modifier.padding(5.dp),
//                        color = if (it <= 2) MaterialTheme.colorScheme.primary else Color.Unspecified,
//                    )
//                    Spacer(modifier = Modifier.weight(1f))
//                    Text(
//                        text = hotSearchWord[it].hotScore.toString(),
//                        modifier = Modifier.padding(5.dp),
//                        color = MaterialTheme.colorScheme.tertiary,
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                }
//            }
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTextInput(
    searchViewModel: SearchViewModel
) {
    var searchWord by remember { mutableStateOf("") }
    androidx.compose.material3.TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(Color.Transparent),
        value = searchWord,
        onValueChange = { searchWord = it },
        placeholder = {
            androidx.compose.material3.Text(text = stringResource(id = R.string.fill_search_text))
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            if (searchWord != "") {
                searchViewModel.searchSongResponse(searchWord)
            }
        }),
    )
}

@Composable
fun ShowSearchSongs(songs: List<Song>, onClick: (Song) -> Unit) {
    LazyColumn {
        items(songs, key = {
            it.neteaseId
        }) { item ->
            SongItemRow(
                item, showBottomSheet = {
                    onClick(it)
                }, onClick = {
//                    onClick(item)
                }
            )
        }
    }
}