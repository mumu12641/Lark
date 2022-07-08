package io.github.mumu12641.lark.ui.theme.page

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.mumu12641.lark.entity.Route

@Composable
fun MainScreen(mainViewModel: MainViewModel){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.ROUTE_HOME){
        composable(Route.ROUTE_HOME){
            HomeScreen(navController,flow = mainViewModel.allSongList) {
                mainViewModel.addSongList()
            }
        }
        composable(Route.ROUTE_LOCAL){
            LocalPage(navController = navController)
        }
    }


}