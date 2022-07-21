package io.github.mumu12641.lark.ui.theme.page.user

import androidx.lifecycle.ViewModel
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableStateFlow

class UserViewModel:ViewModel() {
    private val _userNameState = MutableStateFlow(
        INIT_USER
    )
    val userNameState: MutableStateFlow<UserState> = _userNameState

    fun saveInformation(){
        MMKV.defaultMMKV().encode("userName",_userNameState.value.name)
        MMKV.defaultMMKV().encode("iconImageUri",_userNameState.value.iconImageUri)
        MMKV.defaultMMKV().encode("backgroundImageUri",_userNameState.value.backgroundImageUri)
    }

    fun changeNameValue(value:String){
        _userNameState.value.name = value
    }

    fun changeBackgroundValue(uri:String){
        _userNameState.value.backgroundImageUri = uri
    }

    fun changeIconValue(uri:String){
        _userNameState.value.iconImageUri = uri
    }

    data class UserState(
        var name:String,
        var iconImageUri:String?,
        var backgroundImageUri:String?
    )
    companion object{
        val INIT_USER = UserState(
            MMKV.defaultMMKV().decodeString("userName")!!,
            MMKV.defaultMMKV().decodeString("iconImageUri"),
            MMKV.defaultMMKV().decodeString("backgroundImageUri"),
        )
    }
}