package io.github.mumu12641.lark.ui.theme.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class MainViewModel:ViewModel() {
    val testList: Flow<List<Int>> = flow {
        while (true){
            val list = listOf(1,2,3,4)
            emit(list)
            delay(5000)
        }
    }
    val testResult:StateFlow<List<Int>> = testList.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf(1)
    )
}