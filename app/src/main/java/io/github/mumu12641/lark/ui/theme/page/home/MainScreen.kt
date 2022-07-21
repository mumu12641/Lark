package io.github.mumu12641.lark.ui.theme.page.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.mumu12641.lark.entity.Route
import io.github.mumu12641.lark.ui.theme.page.function.FunctionPage
import io.github.mumu12641.lark.ui.theme.page.function.FunctionViewModel
import io.github.mumu12641.lark.ui.theme.page.user.UserPage
import io.github.mumu12641.lark.ui.theme.page.user.UserViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    functionViewModel: FunctionViewModel,
    userViewModel: UserViewModel
){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.ROUTE_HOME){
        composable(Route.ROUTE_HOME){
            HomeScreen(navController,flow = mainViewModel.allSongList) {
                mainViewModel.addSongList()
            }
        }
        composable(Route.ROUTE_LOCAL){
            FunctionPage(
                navController = navController, route = Route.ROUTE_LOCAL,
                functionViewModel
            )
        }
        composable(Route.ROUTE_CLOUD){
            FunctionPage(navController = navController, route = Route.ROUTE_CLOUD,
                functionViewModel
            )
        }
        composable(Route.ROUTE_DOWNLOAD){
            FunctionPage(navController = navController, route = Route.ROUTE_DOWNLOAD,
                functionViewModel
            )
        }
        composable(Route.ROUTE_HISTORY){
            FunctionPage(navController = navController, route = Route.ROUTE_HISTORY,
                functionViewModel
            )
        }
        composable(Route.ROUTE_USER){
            UserPage(navController = navController,userViewModel)
        }
    }


}