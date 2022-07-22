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
        _userState.value = UserState(value,_userState.value.iconImageUri,_userState.value.backgroundImageUri)
    }
    fun changeBackgroundValue(uri:String){
        _userState.value = UserState(_userState.value.name,_userState.value.iconImageUri,uri)
    }
    fun changeIconValue(uri:String){
        _userState.value = UserState(_userState.value.name,uri,_userState.value.backgroundImageUri)
    }

    data class UserState(
        var name:String,
        var iconImageUri:String?,
        var backgroundImageUri:String?
    )

    companion object{
        var INIT_USER = UserState(
            MMKV.defaultMMKV().decodeString("userName")!!,
            MMKV.defaultMMKV().decodeString("iconImageUri"),
            MMKV.defaultMMKV().decodeString("backgroundImageUri"),
        )
    }
}