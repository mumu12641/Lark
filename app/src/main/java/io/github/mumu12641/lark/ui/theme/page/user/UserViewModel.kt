package io.github.mumu12641.lark.ui.theme.page.user

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.BaseApplication.Companion.kv
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.entity.LoadState
import io.github.mumu12641.lark.network.NetworkCreator
import io.github.mumu12641.lark.network.NetworkCreator.networkService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
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
            Toast.makeText(context,"请先登录",Toast.LENGTH_LONG).show()
        }) {
            _loadState.value = LoadState.Loading()
            val async = async {
                Log.d(TAG, "logout: start")
                val s = networkService.logout()
                Log.d(TAG, "logout: $s")
                Log.d(TAG, "logout: end")
            }
            async.await()

            Log.d(TAG, "another start")
            changeNameValue(context.getString(R.string.user))
            changeIconValue("")
            changeBackgroundValue("")
            saveInformation()
            kv.removeValueForKey("cookie")
            kv.removeValueForKey("neteaseId")
            Log.d(TAG, "logout: " + kv.decodeStringSet("cookie") )
            Log.d(TAG, "logout: " + kv.decodeLong("neteaseId"))
            _loadState.value = LoadState.Success()
        }
    }

    fun loginUser(phoneNumber: String, password: String) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            _loadState.value = LoadState.Fail(e.message ?: "Load Fail")
            e.message?.let { Log.d(TAG, it) }
        }) {
            _loadState.value = LoadState.Loading()
            val user = networkService.cellphoneLogin(phoneNumber, password)
            kv.encode("neteaseId",user.account.id)
            getNeteaseUserDetail()
            _loadState.value = LoadState.Success()
        }
    }


    fun getNeteaseUserDetail() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            _loadState.value = LoadState.Fail(e.message ?: "Load Fail")
            e.message?.let { Log.d(TAG, it) }
            Toast.makeText(context,"请先登录",Toast.LENGTH_LONG).show()
        }) {
            _loadState.value = LoadState.Loading()
            val detail = networkService.getUserDetail(kv.decodeLong("neteaseId"))
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