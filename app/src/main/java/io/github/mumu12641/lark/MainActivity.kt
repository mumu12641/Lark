package io.github.mumu12641.lark

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.mumu12641.lark.ui.theme.LarkTheme
import io.github.mumu12641.lark.ui.theme.page.artist.ArtistViewModel
import io.github.mumu12641.lark.ui.theme.page.details.SongListDetailsViewModel
import io.github.mumu12641.lark.ui.theme.page.function.FunctionViewModel
import io.github.mumu12641.lark.ui.theme.page.home.MainScreen
import io.github.mumu12641.lark.ui.theme.page.home.MainViewModel
import io.github.mumu12641.lark.ui.theme.page.search.SearchViewModel
import io.github.mumu12641.lark.ui.theme.page.user.UserViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    private val mainViewModel: MainViewModel by viewModels()
    private val functionViewModel: FunctionViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val songListDetailsViewModel: SongListDetailsViewModel by viewModels()
    private val artistViewModel: ArtistViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this@MainActivity
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PreferenceProvider {
                LarkTheme(
                    seedColor = LocalSeedColor.current,
                    darkTheme = LocalDarkTheme.current,
                    dynamicColorEnable = DynamicColorSwitch.current.enable,
                    dynamicColor = DynamicColorSwitch.current.dynamicColorSwitch
                ) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        MainScreen(
                            mainViewModel,
                            functionViewModel,
                            userViewModel,
                            songListDetailsViewModel,
                            artistViewModel,
                            searchViewModel
                        )
                    }
                }
            }
        }
    }
}

