package io.github.mumu12641.lark.ui.theme.page.splash

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import io.github.mumu12641.lark.BaseApplication.Companion.kv
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.Route
import io.github.mumu12641.lark.network.LoadState
import io.github.mumu12641.lark.ui.theme.page.user.LoginDialog
import io.github.mumu12641.lark.ui.theme.page.user.UserViewModel
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil
import io.github.mumu12641.lark.ui.theme.util.suspendToast
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalPagerApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun SplashPage(navController: NavController, userViewModel: UserViewModel) {
    val loadState by userViewModel.loadState.collectAsState()
    val loginComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.login))
    val loginState by animateLottieCompositionAsState(
        composition = loginComposition,
        iterations = LottieConstants.IterateForever,
    )
    val listenComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.music))
    val listenState by animateLottieCompositionAsState(
        composition = listenComposition,
        iterations = LottieConstants.IterateForever,
    )
    val themeComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.theme))
    val themeState by animateLottieCompositionAsState(
        composition = themeComposition,
        iterations = LottieConstants.IterateForever,
    )
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0
    )
    var showLoginDialog by remember {
        mutableStateOf(false)
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (pagerState.currentPage < 2) {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    showLoginDialog = true
                }
            }) {
                AnimatedContent(targetState = pagerState.currentPage) { targetState: Int ->
                    if (targetState < 2) Icon(Icons.Default.NavigateNext, contentDescription = null)
                    else Icon(Icons.Default.Login, contentDescription = null)
                }
            }
        }) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalPager(count = 3, state = pagerState) { page: Int ->
                    when (page) {
                        0 -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Column(
                                    modifier = Modifier
                                        .height(200.dp)
                                        .padding(horizontal = 20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.listen_title),
                                        style = MaterialTheme.typography.displayMedium,
                                        modifier = Modifier.padding(5.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    )
                                    Text(
                                        text = stringResource(id = R.string.listen_desc),
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(vertical = 10.dp),
                                    )
                                }
                                LottieAnimation(
                                    composition = listenComposition,
                                    progress = { listenState },
                                    modifier = Modifier.size(350.dp)
                                )
                            }
                        }
                        1 -> {
                            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                                Column(modifier = Modifier
                                    .height(200.dp)
                                    .padding(horizontal = 20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center) {
                                    Text(
                                        text = stringResource(id = R.string.theme_title),
                                        style = MaterialTheme.typography.displayMedium,
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    )
                                    Text(
                                        text = stringResource(id = R.string.theme_desc),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                LottieAnimation(
                                    composition = themeComposition,
                                    progress = { themeState },
                                    modifier = Modifier.size(350.dp)
                                )
                            }
                        }
                        2 -> {
                            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                                Column(modifier = Modifier
                                    .height(200.dp)
                                    .padding(horizontal = 20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center) {
                                    Row {
                                        Text(
                                            text = stringResource(id = R.string.login_title),
                                            style = MaterialTheme.typography.displayMedium,
                                            modifier = Modifier.padding(vertical = 10.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        )
                                        AnimatedContent(targetState = loadState) { load ->
                                            when (load) {
                                                is LoadState.Loading -> CircularProgressIndicator()
                                                is LoadState.Success -> {
                                                    navController.navigate(Route.ROUTE_HOME){
                                                        popUpTo(0)
                                                    }
                                                    kv.encode(
                                                        PreferenceUtil.SPLASH_SCREEN,
                                                        false
                                                    )
                                                }
                                                else -> {
                                                    stringResource(id = R.string.check_network).suspendToast()
                                                }
                                            }
                                        }
                                    }
                                    Text(
                                        text = stringResource(id = R.string.login_desc),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                LottieAnimation(
                                    composition = loginComposition,
                                    progress = { loginState },
                                    modifier = Modifier.size(350.dp)
                                )
                            }
                        }
                    }
                }
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier.padding(16.dp),
                    activeColor = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
        if (showLoginDialog) {
            LoginDialog(
                showDialogFunc = { show -> showLoginDialog = show },
                login = { phone, password ->
                    userViewModel.loginUser(phone, password)
                }) {
                TextButton(
                    onClick = {
                        showLoginDialog = false
                        userViewModel.guestLogin()
                    }
                ) {
                    Text(stringResource(id = R.string.guest_login))
                }
            }
        }
    }
}