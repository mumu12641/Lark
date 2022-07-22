package io.github.mumu12641.lark.ui.theme.page.user

import androidx.lifecycle.ViewModel
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableStateFlow

class UserViewModel:ViewModel() {

    fun saveInformation(){
        MMKV.defaultMMKV().encode("userName",_userState.value.name)
        MMKV.defaultMMKV().encode("iconImageUri",_userState.value.iconImageUri)
        MMKV.defaultMMKV().encode("backgroundImageUri",_userState.value.backgroundImageUri)
        INIT_USER = UserState(
            MMKV.defaultMMKV().decodeString("userName")!!,
            MMKV.defaultMMKV().decodeString("iconImageUri"),
            MMKV.defaultMMKV().decodeString("backgroundImageUri"),
        )
    }

    private val _userState = MutableStateFlow(INIT_USER)
    val userState:MutableStateFlow<UserState> = _userState


    fun changeNameValue(value:String){
        _userState.value = _userState.value.copy(name = value)
    }
    fun changeBackgroundValue(uri:String){
        _userState.value = _userState.value.copy(backgroundImageUri = uri)
    }
    fun changeIconValue(uri:String){
        _userState.value = _userState.value.copy(iconImageUri = uri)

    }

    data class UserState(
        val name:String,
        val iconImageUri:String?,
        val backgroundImageUri:String?
    )

    companion object{
        var INIT_USER = UserState(
            MMKV.defaultMMKV().decodeString("userName")!!,
            MMKV.defaultMMKV().decodeString("iconImageUri"),
            MMKV.defaultMMKV().decodeString("backgroundImageUri"),
        )
    }
}