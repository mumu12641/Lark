package io.github.mumu12641.lark

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.mumu12641.lark.ui.theme.LarkTheme
import io.github.mumu12641.lark.ui.theme.page.artist.ArtistViewModel
import io.github.mumu12641.lark.ui.theme.page.details.SongListDetailsViewModel
import io.github.mumu12641.lark.ui.theme.page.function.FunctionViewModel
import io.github.mumu12641.lark.ui.theme.page.home.MainScreen
import io.github.mumu12641.lark.ui.theme.page.home.MainViewModel
import io.github.mumu12641.lark.ui.theme.page.play.PlayViewModel
import io.github.mumu12641.lark.ui.theme.page.search.SearchViewModel
import io.github.mumu12641.lark.ui.theme.page.user.UserViewModel
import io.github.mumu12641.lark.ui.theme.util.PreferenceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        fun setLanguage(locale: String) {
            val localeListCompat =
                if (locale.isEmpty()) LocaleListCompat.getEmptyLocaleList()
                else LocaleListCompat.forLanguageTags(locale)
            BaseApplication.applicationScope.launch(Dispatchers.Main) {
                AppCompatDelegate.setApplicationLocales(localeListCompat)
            }
        }
    }

    private val mainViewModel: MainViewModel by viewModels()
    private val functionViewModel: FunctionViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val songListDetailsViewModel: SongListDetailsViewModel by viewModels()
    private val artistViewModel: ArtistViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()
    private val playViewModel: PlayViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this@MainActivity
        WindowCompat.setDecorFitsSystemWindows(window, false)

        runBlocking {
            if (Build.VERSION.SDK_INT < 33)
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(PreferenceUtil.getLanguageConfiguration())
                )
        }

        setContent {
            PreferenceProvider {
                LarkTheme(
                    seedColor = LocalSeedColor.current,
                    darkTheme = LocalDarkTheme.current,
                    dynamicColorEnable = DynamicColorSwitch.current.enable,
                    dynamicColor = DynamicColorSwitch.current.dynamicColorSwitch,
                ) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        MainScreen(
                            mainViewModel,
                            functionViewModel,
                            userViewModel,
                            songListDetailsViewModel,
                            artistViewModel,
                            searchViewModel,
                            playViewModel
                        )
                    }
                }
            }
        }
    }


}

