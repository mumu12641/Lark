package io.github.mumu12641.lark.ui.theme.page.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.network.NetworkCreator
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {

    fun saveInformation() {
        MMKV.defaultMMKV().encode("userName", _userState.value.name)
        MMKV.defaultMMKV().encode("iconImageUri", _userState.value.iconImageUri)
        MMKV.defaultMMKV().encode("backgroundImageUri", _userState.value.backgroundImageUri)
        INIT_USER = UserState(
            MMKV.defaultMMKV().decodeString("userName")!!,
            MMKV.defaultMMKV().decodeString("iconImageUri"),
            MMKV.defaultMMKV().decodeString("backgroundImageUri"),
        )
    }

    private val _userState = MutableStateFlow(INIT_USER)
    val userState: MutableStateFlow<UserState> = _userState


    fun changeNameValue(value: String) {
        _userState.value = _userState.value.copy(name = value)
    }

    fun changeBackgroundValue(uri: String) {
        _userState.value = _userState.value.copy(backgroundImageUri = uri)
    }

    fun changeIconValue(uri: String) {
        _userState.value = _userState.value.copy(iconImageUri = uri)

    }

    fun getNeteaseUserDetail() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            e.message?.let { Log.d(TAG, it) }
        }){
            val detail  = NetworkCreator.networkService.getUserDetail(416000474)
            changeNameValue(detail.profile.nickname)
            changeIconValue(detail.profile.avatarUrl)
            changeBackgroundValue(detail.profile.backgroundUrl)
            saveInformation()
        }
    }

    data class UserState(
        val name: String,
        val iconImageUri: String?,
        val backgroundImageUri: String?
    )


    companion object {
        var INIT_USER = UserState(
            MMKV.defaultMMKV().decodeString("userName")!!,
            MMKV.defaultMMKV().decodeString("iconImageUri"),
            MMKV.defaultMMKV().decodeString("backgroundImageUri"),
        )
    }

    private val TAG = "UserViewModel"
}