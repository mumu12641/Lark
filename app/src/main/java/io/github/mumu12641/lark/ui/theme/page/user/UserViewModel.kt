package io.github.mumu12641.lark.ui.theme.page.user

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.BaseApplication.Companion.kv
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.LoadState
import io.github.mumu12641.lark.network.NetworkCreator
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {

    fun saveInformation() {
        kv.encode("userName", _userState.value.name)
        kv.encode("iconImageUri", _userState.value.iconImageUri)
        kv.encode("backgroundImageUri", _userState.value.backgroundImageUri)
        INIT_USER = UserState(
            kv.decodeString("userName")!!,
            kv.decodeString("iconImageUri"),
            kv.decodeString("backgroundImageUri"),
        )
    }

    private val _userState = MutableStateFlow(INIT_USER)
    val userState: MutableStateFlow<UserState> = _userState
    private val _loadState = MutableStateFlow<LoadState>(LoadState.None())
    val loadState = _loadState


    fun changeNameValue(value: String) {
        _userState.value = _userState.value.copy(name = value)
    }

    fun changeBackgroundValue(uri: String) {
        _userState.value = _userState.value.copy(backgroundImageUri = uri)
    }

    fun changeIconValue(uri: String) {
        _userState.value = _userState.value.copy(iconImageUri = uri)

    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            _loadState.value = LoadState.Fail(e.message ?: "Load Fail")
            e.message?.let { Log.d(TAG, it) }
        }) {
            _loadState.value = LoadState.Loading()
            changeNameValue(context.getString(R.string.user))
            changeIconValue("")
            changeBackgroundValue("")
            saveInformation()
            kv.removeValueForKey("cookie")
            _loadState.value = LoadState.Success()
        }
    }

    fun loginUser(phoneNumber: String, password: String) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            _loadState.value = LoadState.Fail(e.message ?: "Load Fail")
            e.message?.let { Log.d(TAG, it) }
        }) {
            _loadState.value = LoadState.Loading()
            val user = NetworkCreator.networkService.cellphoneLogin(phoneNumber, password)
            delay(1000)
            _loadState.value = LoadState.Success()
        }
    }

    fun getNeteaseUserDetail() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            _loadState.value = LoadState.Fail(e.message ?: "Load Fail")
            e.message?.let {
                Log.d(TAG, it)
                Toast.makeText(context,"登录成功",Toast.LENGTH_LONG).show()
            }
        }) {
            _loadState.value = LoadState.Loading()
            val detail = NetworkCreator.networkService.getUserDetail(416000474)
            changeNameValue(detail.profile.nickname)
            changeIconValue(detail.profile.avatarUrl)
            changeBackgroundValue(detail.profile.backgroundUrl)
            saveInformation()
            _loadState.value = LoadState.Success()
        }
    }

    data class UserState(
        val name: String,
        val iconImageUri: String?,
        val backgroundImageUri: String?
    )


    companion object {
        var INIT_USER = UserState(
            kv.decodeString("userName")!!,
            kv.decodeString("iconImageUri"),
            kv.decodeString("backgroundImageUri"),
        )
    }

    private val TAG = "UserViewModel"
}