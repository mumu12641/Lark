package io.github.mumu12641.lark

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import io.github.mumu12641.lark.ui.theme.LarkTheme
import io.github.mumu12641.lark.ui.theme.page.function.FunctionViewModel
import io.github.mumu12641.lark.ui.theme.page.home.MainScreen
import io.github.mumu12641.lark.ui.theme.page.home.MainViewModel
import io.github.mumu12641.lark.ui.theme.page.user.UserViewModel

class MainActivity : ComponentActivity() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    private val mainViewModel by viewModels<MainViewModel>()
    private val functionViewModel by viewModels<FunctionViewModel>()
    private val userViewModel by viewModels<UserViewModel>()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this@MainActivity
        setContent {
            LarkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(mainViewModel,functionViewModel,userViewModel)
                }
            }
        }
    }
}

