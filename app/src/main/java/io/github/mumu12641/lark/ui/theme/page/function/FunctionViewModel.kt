package io.github.mumu12641.lark.ui.theme.page.function

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import io.github.mumu12641.lark.BaseApplication.Companion.context

class FunctionViewModel:ViewModel() {
    var uiState = mutableStateOf(UIState())

    fun test(){
        uiState.value = UIState(true)
    }

}

data class UIState(
    var checkPermission:Boolean = XXPermissions.isGranted(context,Permission.ACCESS_MEDIA_LOCATION)
)

fun UIState.test(){
    checkPermission = true
}