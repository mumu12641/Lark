package io.github.mumu12641.lark.ui.theme.page.user

import androidx.lifecycle.ViewModel
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel:ViewModel() {
    private val _userNameState = MutableStateFlow(MMKV.defaultMMKV().decodeString("UserName")!!)
    val userNameState: MutableStateFlow<String> = _userNameState

    fun saveInformation(){
        MMKV.defaultMMKV().encode("UserName",_userNameState.value)
    }

    fun changeNameValue(value:String){
        _userNameState.value = value
    }
}