package io.github.mumu12641.lark.ui.theme.component

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.animatedComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
) = composable(
    route = route,
    arguments = arguments,
    deepLinks = deepLinks,
    enterTransition = {
        fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(220, delayMillis = 90)
                )
    },
    exitTransition = {
        fadeOut(animationSpec = tween(90))
    },
    popEnterTransition = {
        fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(220, delayMillis = 90)
                )
    },
    popExitTransition = {
        fadeOut(animationSpec = tween(90))
    },
    content = content
)

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.animatedPlayPageComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
) = composable(
    route = route,
    arguments = arguments,
    deepLinks = deepLinks,
    enterTransition = {
//        fadeIn(animationSpec = tween(220, delayMillis = 90)) +
//                scaleIn(
//                    initialScale = 0.92f,
//                    animationSpec = tween(220, delayMillis = 90)
//                )
        slideInVertically(animationSpec = tween(220, delayMillis = 90),initialOffsetY = {it/2})
    },
    exitTransition = {
        slideOutVertically(targetOffsetY = { it / 2 })
    },
    popEnterTransition = {
//        fadeIn(animationSpec = tween(220, delayMillis = 90)) +
//                scaleIn(
//                    initialScale = 0.92f,
//                    animationSpec = tween(220, delayMillis = 90)
//                )
        slideInVertically(animationSpec = tween(220, delayMillis = 90),initialOffsetY = {it/2})
    },
    popExitTransition = {
        slideOutVertically(targetOffsetY = { it / 2 })
                        },
    content = content
)