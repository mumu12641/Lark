package io.github.mumu12641.lark.ui.theme.page.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.Route
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun SplashPage(navController: NavController) {
    val loginComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.login))
    val loginState by animateLottieCompositionAsState(composition = loginComposition,iterations = LottieConstants.IterateForever,)
    val listenComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.music))
    val listenState by animateLottieCompositionAsState(composition = listenComposition,iterations = LottieConstants.IterateForever,)
    val themeComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.theme))
    val themeState by animateLottieCompositionAsState(composition = themeComposition,iterations = LottieConstants.IterateForever,)
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0
    )
    Scaffold(topBar = { TopAppBar(title = { Text(text = "") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                scope.launch {
                    if (pagerState.currentPage < 2) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    } else {
                        navController.navigate(Route.ROUTE_HOME)
                    }
                }
            }) {
                Icon(Icons.Default.NavigateNext, contentDescription = null)
            }
        }) {
        Box(modifier = Modifier
            .padding(it)
            .fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                HorizontalPager(count = 3, state = pagerState,) { page: Int ->
                    when (page) {
                        0 -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(
                                    text = "听你想听",
                                    style = MaterialTheme.typography.displayMedium,
                                    modifier = Modifier.padding(5.dp),
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                )
                                Text(
                                    text = "导入个性歌单，获取个性化推荐，听你想听！",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                )
                                LottieAnimation(
                                    composition = listenComposition,
                                    progress = { listenState },
                                    modifier = Modifier.size(350.dp))
                            }
                        }
                        1 -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(
                                    text = "主题",
                                    style = MaterialTheme.typography.displayMedium,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    fontFamily = FontFamily.SansSerif,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                )
                                Text(
                                    text = "定制您的播放主题",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                LottieAnimation(
                                    composition = themeComposition,
                                    progress = { themeState },
                                modifier = Modifier.size(350.dp))
                            }
                        }
                        2 -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text(
                                    text = "登录",
                                    style = MaterialTheme.typography.displayMedium,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    fontFamily = FontFamily.SansSerif,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                )
                                Text(
                                    text = "登录您的网易云账号！",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                LottieAnimation(
                                    composition = loginComposition,
                                    progress = { loginState },
                                    modifier = Modifier.size(350.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}