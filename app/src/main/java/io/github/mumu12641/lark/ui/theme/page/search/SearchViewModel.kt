package io.github.mumu12641.lark.ui.theme.page.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.lark.network.NetworkCreator.networkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() :ViewModel(){

    private val _hotSearchWord = MutableStateFlow<List<HotData>>(emptyList())
    val hotSearchWord = _hotSearchWord

    data class HotData(
        val hotSearchWord:String,
        val hotScore:Int
    )


    init {
        viewModelScope.launch (Dispatchers.IO){
            val hot = networkService.getSearchHot()
            _hotSearchWord.value = hot.data.map {
                HotData(it.searchWord,it.score)
            }
        }
    }
}