package io.github.mumu12641.lark.ui.theme.page.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import io.github.mumu12641.lark.entity.Route
import io.github.mumu12641.lark.ui.theme.component.AnimationComposable
import io.github.mumu12641.lark.ui.theme.page.details.SongListDetailsPage
import io.github.mumu12641.lark.ui.theme.page.details.SongListDetailsViewModel
import io.github.mumu12641.lark.ui.theme.page.function.FunctionPage
import io.github.mumu12641.lark.ui.theme.page.function.FunctionViewModel
import io.github.mumu12641.lark.ui.theme.page.user.UserPage
import io.github.mumu12641.lark.ui.theme.page.user.UserViewModel

@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    functionViewModel: FunctionViewModel,
    userViewModel: UserViewModel
){
    val navController = rememberAnimatedNavController()

    AnimatedNavHost(
        navController = navController,
        startDestination = Route.ROUTE_HOME
    ){
        AnimationComposable(
            route = Route.ROUTE_HOME
        ){
            HomeScreen(navController,flow = mainViewModel.allSongList,
                { mainViewModel.playMedia() }) {
                mainViewModel.addSongList(it)
            }
        }
        AnimationComposable(
            Route.ROUTE_LOCAL
        ){
            FunctionPage(
                navController = navController, route = Route.ROUTE_LOCAL,
                functionViewModel
            )
        }
        AnimationComposable(
            Route.ROUTE_CLOUD
        ){
            FunctionPage(navController = navController, route = Route.ROUTE_CLOUD,
                functionViewModel
            )
        }
        AnimationComposable(
            Route.ROUTE_DOWNLOAD
        ){
            FunctionPage(navController = navController, route = Route.ROUTE_DOWNLOAD,
                functionViewModel
            )
        }
        AnimationComposable(
            Route.ROUTE_HISTORY
        ){
            FunctionPage(navController = navController, route = Route.ROUTE_HISTORY,
                functionViewModel
            )
        }
        AnimationComposable(
            Route.ROUTE_USER
        ){
            UserPage(navController = navController,userViewModel)
        }
        AnimationComposable(
            Route.ROUTE_SONG_LIST_DETAILS + "{songListId}"
        ){
            backStackEntry ->
            backStackEntry.arguments?.getString("songListId")
                ?.let { SongListDetailsPage(navController,SongListDetailsViewModel(), it) }
        }
    }


}