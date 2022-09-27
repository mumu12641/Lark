package io.github.mumu12641.lark.ui.theme.page.play

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.network.NetworkCreator.networkService
import io.github.mumu12641.lark.room.DataBaseUtils
import io.github.mumu12641.lark.service.MediaServiceConnection
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PlayViewModel"

@HiltViewModel
class PlayViewModel @Inject constructor() : ViewModel() {


    private val _playUiState = MutableStateFlow(PlayUiState())
    val playUiState = _playUiState

    fun initData(id: Long) {
        _playUiState.update {
            it.copy(isLoading = true, currentPlayId = id)
        }
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            Log.d(TAG, "initData: " + e.message)
            _playUiState.update {
                it.copy(lyrics = listOf("", ""), isLoading = false)
            }
        }) {
            networkService.getLyric(id).lrc.lyric.let {
                DataBaseUtils.updateSong(
                    DataBaseUtils.querySongById(
                        DataBaseUtils.querySongIdByNeteaseId(id)
                    ).copy(lyrics = it)
                )
                val list = MediaServiceConnection.regex.split(it.replace("\\r|\\n".toRegex(), ""))
                _playUiState.update { state ->
                    state.copy(lyrics = list)
                }
            }
            _playUiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    data class PlayUiState(
        val isLoading: Boolean = true,
        val lyrics: List<String> = emptyList(),
        val currentPlayId: Long = 1L
    )
}