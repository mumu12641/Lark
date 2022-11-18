package io.github.mumu12641.lark.ui.theme.page.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.BaseApplication.Companion.kv
import io.github.mumu12641.lark.R
import io.github.mumu12641.lark.network.LoadState
import io.github.mumu12641.lark.network.Repository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
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
        _userState.update {
            it.copy(name = value)
        }
    }

    fun changeBackgroundValue(uri: String?) {
        _userState.update {
            it.copy(backgroundImageUri = uri)
        }
    }

    fun changeIconValue(uri: String) {
        _userState.update {
            it.copy(iconImageUri = uri)
        }

    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            _loadState.value = LoadState.Fail(e.message ?: "Load Fail")
        }) {
            _loadState.value = LoadState.Loading()
            val async = async {
                val s = Repository.logout()
            }
            async.await()
            _userState.update {
                it.copy(
                    name = context.getString(R.string.user),
                    iconImageUri = "",
                    backgroundImageUri = null
                )
            }
            saveInformation()
            kv.encode("Cookie", "null")
            _loadState.value = LoadState.Success()
        }
    }

    fun loginUser(phoneNumber: String, password: String) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            _loadState.value = LoadState.Fail(e.message ?: "Load Fail")
            kv.encode("neteaseId", 416000474L)
            e.message?.let { Log.d(TAG, it) }
        }) {
            _loadState.value = LoadState.Loading()
            val user = Repository.cellphoneLogin(phoneNumber, password)
            Log.d(TAG, "loginUser: ${user.cookie}")
            kv.encode("Cookie", user.cookie)
            kv.encode("neteaseId", user.account.id.toLong())
            getNeteaseUserDetail()
            _loadState.value = LoadState.Success()
        }
    }


    fun getNeteaseUserDetail() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            _loadState.value = LoadState.Fail(e.message ?: "Load Fail")
            e.message?.let { Log.d(TAG, it) }
        }) {
            _loadState.value = LoadState.Loading()
            val detail = Repository.getUserDetail(kv.decodeLong("neteaseId"))
            _userState.update {
                it.copy(
                    name = detail.profile.nickname,
                    iconImageUri = detail.profile.avatarUrl,
                    backgroundImageUri = detail.profile.backgroundUrl
                )
            }
            saveInformation()
            _loadState.value = LoadState.Success()
        }
    }

    fun guestLogin() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            _loadState.value = LoadState.Fail(e.message ?: "Load Fail")
            e.message?.let { Log.d(TAG, it) }
        }) {
            _loadState.value = LoadState.Loading()
            val s = Repository.anonymousLogin()
            Log.d(TAG, "guestLogin: " + s.cookie)
            kv.encode("Cookie", s.cookie)
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