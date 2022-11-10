package io.github.mumu12641.lark.ui.theme.page.play

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.network.Repository
import io.github.mumu12641.lark.room.DataBaseUtils
import io.github.mumu12641.lark.service.MediaServiceConnection
import io.github.mumu12641.lark.service.MediaServiceConnection.Companion.regexTime
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PlayViewModel @Inject constructor() : ViewModel() {


    private val _playUiState = MutableStateFlow(PlayUiState())
    val playUiState = _playUiState

    @RequiresApi(Build.VERSION_CODES.N)
    fun initData(id: Long) {
        _playUiState.update {
            it.copy(isLoading = true, currentPlayId = id)
        }
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ ->
            _playUiState.update {
                it.copy(lyrics = listOf("", ""), isLoading = false)
            }
        }) {
            Repository.getLyric(id).lrc.lyric.let {
                DataBaseUtils.updateSong(
                    DataBaseUtils.querySongById(
                        DataBaseUtils.querySongIdByNeteaseId(id)
                    ).copy(lyrics = it)
                )
                val list = MediaServiceConnection.regex.split(it.replace("\\r|\\n".toRegex(), ""))
                var timingList = emptyList<Long>()
                with(regexTime.findAll(it)) {
                    timingList = this.map { match ->
                        val value = match.value.replace("[", "").replace("]", "")
                        dateFormat.parse(value).time - fixTime
                    }.toList()
                }
                _playUiState.update { state ->
                    state.copy(lyrics = list, lyricsTiming = timingList)
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
        var lyricsTiming: List<Long> = emptyList(),
        val currentPlayId: Long = 1L,
    )

    companion object {
        @SuppressLint("SimpleDateFormat")
        @RequiresApi(Build.VERSION_CODES.N)
        val dateFormat = SimpleDateFormat("mm:ss")
        const val fixTime = -28800000L
    }
}