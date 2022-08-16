package io.github.mumu12641.lark.ui.theme.page.search

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarScrollState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.mumu12641.lark.ui.theme.component.LarkTopBar
import io.github.mumu12641.lark.R

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(navController: NavController, searchViewModel: SearchViewModel) {

    val bottomSheetScaffoldState =
        rememberBottomSheetScaffoldState(bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed))
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        decayAnimationSpec,
        rememberTopAppBarScrollState(),
        canScroll = { true }
    )


    BottomSheetScaffold(
        sheetContent = {
        },
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 0.dp,
        sheetBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        backgroundColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LarkTopBar(
                title = stringResource(id = R.string.search_text),
                navIcon = Icons.Default.ArrowBack,
                scrollBehavior = scrollBehavior
            ) {
                navController.popBackStack()
            }
        },
        content = { paddingValues ->
            SearchPageContent(
                modifier = Modifier.padding(paddingValues),
                searchViewModel
            )
        }
    )
}

@Composable
fun SearchPageContent(modifier: Modifier, searchViewModel: SearchViewModel) {

    val hotSearchWord by searchViewModel.hotSearchWord.collectAsState(initial = emptyList())

    var searchWord by remember { mutableStateOf("") }

    LazyColumn(modifier = modifier) {
        item {
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
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "search")
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent
                )
            )
        }
        item {
            Text(
                text = stringResource(id = R.string.hot_search_text),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 20.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }
        repeat(hotSearchWord.size){
            item { 
                Row(modifier = Modifier.padding(10.dp)) {
                    Text(text = (it + 1).toString())
                    Text(text = hotSearchWord[it].hotSearchWord)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = hotSearchWord[it].hotScore.toString())
                }
            }
        }
    }
}