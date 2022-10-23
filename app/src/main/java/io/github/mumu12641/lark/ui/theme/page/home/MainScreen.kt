package io.github.mumu12641.lark.ui.theme.page.home

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.BaseApplication.Companion.kv
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.Route
import io.github.mumu12641.lark.ui.theme.component.animatedComposable
import io.github.mumu12641.lark.ui.theme.page.artist.ArtistDetailPage
import io.github.mumu12641.lark.ui.theme.page.artist.ArtistPage
import io.github.mumu12641.lark.ui.theme.page.artist.ArtistViewModel
import io.github.mumu12641.lark.ui.theme.page.details.SongListDetailsPage
import io.github.mumu12641.lark.ui.theme.page.details.SongListDetailsViewModel
import io.github.mumu12641.lark.ui.theme.page.function.FunctionPage
import io.github.mumu12641.lark.ui.theme.page.function.FunctionViewModel
import io.github.mumu12641.lark.ui.theme.page.play.PlayPage
import io.github.mumu12641.lark.ui.theme.page.play.PlayViewModel
import io.github.mumu12641.lark.ui.theme.page.search.SearchPage
import io.github.mumu12641.lark.ui.theme.page.search.SearchViewModel
import io.github.mumu12641.lark.ui.theme.page.settings.SettingPage
import io.github.mumu12641.lark.ui.theme.page.settings.UniversalSettingPage
import io.github.mumu12641.lark.ui.theme.page.settings.about.AboutPage
import io.github.mumu12641.lark.ui.theme.page.settings.diaplay.DisplayPage
import io.github.mumu12641.lark.ui.theme.page.splash.SplashPage
import io.github.mumu12641.lark.ui.theme.page.user.UserPage
import io.github.mumu12641.lark.ui.theme.page.user.UserViewModel
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil.SPLASH_SCREEN

@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    functionViewModel: FunctionViewModel,
    userViewModel: UserViewModel,
    songListDetailsViewModel: SongListDetailsViewModel,
    artistViewModel: ArtistViewModel,
    searchViewModel: SearchViewModel,
    playViewModel: PlayViewModel
) {
    val navController = rememberAnimatedNavController()

    val playMedia = { songListId: Long, songId: Long ->
        mainViewModel.playMedia(songListId, songId)
    }

    AnimatedNavHost(
        navController = navController,
        startDestination = if (kv.decodeBool(
                SPLASH_SCREEN,
                true
            )
        ) Route.ROUTE_SPLASH else Route.ROUTE_HOME
    ) {
        animatedComposable(
            route = Route.ROUTE_SPLASH
        ) {
            SplashPage(navController = navController)
        }
        animatedComposable(
            route = Route.ROUTE_HOME
        ) {
            HomeScreen(
                navController,
                mainViewModel
            )
        }
        animatedComposable(
            Route.ROUTE_LOCAL
        ) {
            FunctionPage(
                navController = navController, route = Route.ROUTE_LOCAL,
                viewModel = functionViewModel,
                playMedia = playMedia,
                refreshArtist = { mainViewModel.refreshArtist() }
            )
        }
        animatedComposable(
            Route.ROUTE_HISTORY
        ) {
            FunctionPage(
                navController = navController, route = Route.ROUTE_HISTORY,
                viewModel = functionViewModel,
                playMedia = playMedia
            )
        }
        animatedComposable(
            Route.ROUTE_DOWNLOAD
        ) {
            FunctionPage(
                navController = navController, route = Route.ROUTE_DOWNLOAD,
                viewModel = functionViewModel,
                playMedia = playMedia,
            )
        }
        animatedComposable(
            Route.ROUTE_CLOUD
        ) {
            FunctionPage(
                navController = navController, route = Route.ROUTE_CLOUD,
                viewModel = functionViewModel,
                playMedia = playMedia
            )
        }
        animatedComposable(
            Route.ROUTE_SUGGESTION
        ) {
            FunctionPage(
                navController = navController, route = Route.ROUTE_SUGGESTION,
                viewModel = functionViewModel,
                playMedia = playMedia
            )
        }
        animatedComposable(
            Route.ROUTE_USER
        ) {
            UserPage(navController = navController, userViewModel)
        }
        animatedComposable(
            Route.ROUTE_SONG_LIST_DETAILS + "{songListId}"
        ) {
            SongListDetailsPage(
                navController, songListDetailsViewModel,
                playMedia
            )
        }
        animatedComposable(
            Route.ROUTE_PLAY_PAGE
        ) {
            PlayPage(navController = navController, mainViewModel = mainViewModel, playViewModel)
        }
        animatedComposable(
            Route.ROUTE_ARTIST_PAGE
        ) {
            ArtistPage(
                navController = navController,
                artistViewModel = artistViewModel,
                refreshArtist = {
                    Toast.makeText(
                        context,
                        context.getString(R.string.get_artist_msg_text),
                        Toast.LENGTH_LONG
                    ).show()
                    mainViewModel.refreshArtist()
                }) {
                navController.navigate(Route.ROUTE_ARTIST_DETAIL_PAGE + it.toString())
            }
        }
        animatedComposable(
            Route.ROUTE_ARTIST_DETAIL_PAGE + "{songListId}"
        ) {
            ArtistDetailPage(
                navController = navController,
                artistViewModel = artistViewModel,
                playMedia = playMedia
            )

        }
        animatedComposable(Route.ROUTE_SETTING) {
            SettingPage(navController = navController)
        }
        animatedComposable(Route.ROUTE_DISPLAY) {
            DisplayPage(navController = navController)
        }
        animatedComposable(Route.ROUTE_ABOUT) {
            AboutPage(navController = navController)
        }
        animatedComposable(Route.ROUTE_UNIVERSAL) {
            UniversalSettingPage(navController = navController, mainViewModel)
        }
        animatedComposable(Route.ROUTE_SEARCH) {
            SearchPage(
                navController = navController,
                searchViewModel,
                addBannerSongToList = { mainViewModel.addSongToCurrentList(it) })
        }
    }
}