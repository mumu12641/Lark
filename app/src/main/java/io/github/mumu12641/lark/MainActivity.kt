package io.github.mumu12641.lark

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.github.mumu12641.lark.ui.theme.LarkTheme
import io.github.mumu12641.lark.ui.theme.page.HomeScreen
import io.github.mumu12641.lark.ui.theme.page.MainViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LarkTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen()
                }
            }
        }
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                mainViewModel.testResult.collect{
                    Log.d(TAG, it.toString())
                }
            }
            Log.d(TAG, "Lifecycle Scope done")
        }
        lifecycleScope.launch {
            mainViewModel.testList
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    Log.d("flowWithLifecycle", it.toString())
                }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LarkTheme {
        Greeting("Android")
    }
}