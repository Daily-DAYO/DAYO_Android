package daily.dayo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import daily.dayo.domain.model.Search
import daily.dayo.domain.usecase.search.*
import dagger.hilt.android.lifecycle.HiltViewModel
import daily.dayo.domain.model.SearchHistory
import daily.dayo.domain.model.SearchHistoryType
import daily.dayo.domain.model.SearchUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val deleteSearchKeywordRecentUseCase: DeleteSearchKeywordRecentUseCase,
    private val clearSearchKeywordRecentUseCase: ClearSearchKeywordRecentUseCase,
    private val requestSearchKeywordRecentUseCase: RequestSearchKeywordRecentUseCase,
    private val updateSearchKeywordRecentUseCase: UpdateSearchKeywordRecentUseCase,
    private val requestSearchTagUseCase: RequestSearchTagUseCase,
    private val requestSearchUserUseCase: RequestSearchKeywordUserUseCase,
    private val requestSearchTotalCountUseCase: RequestSearchTotalCountUseCase
) : ViewModel() {

    var searchKeyword = ""

    private val _searchTagTotalCount = MutableStateFlow(0)
    val searchTagTotalCount get() = _searchTagTotalCount

    private val _searchUserTotalCount = MutableStateFlow(0)
    val searchUserTotalCount get() = _searchUserTotalCount

    private val _searchTagList = MutableStateFlow<PagingData<Search>>(PagingData.empty())
    val searchTagList get() = _searchTagList.asStateFlow()

    private val _searchUserList = MutableStateFlow<PagingData<SearchUser>>(PagingData.empty())
    val searchUserList get() = _searchUserList.asStateFlow()

    private val _searchHistory = MutableStateFlow<SearchHistory>(SearchHistory(0, emptyList()))
    val searchHistory get() = _searchHistory

    suspend fun getSearchKeywordRecent() = requestSearchKeywordRecentUseCase().let {
        _searchHistory.emit(it)
    }

    fun searchKeyword(keyword: String, keywordType: SearchHistoryType = SearchHistoryType.TAG) =
        viewModelScope.launch {
            updateSearchKeywordRecentUseCase(keyword, keywordType)
            when (keywordType) {
                SearchHistoryType.TAG -> {
                    requestSearchTotalCountUseCase(keyword, SearchHistoryType.TAG).let {
                        _searchTagTotalCount.emit(it)
                    }
                    requestSearchTagUseCase(tag = keyword)
                        .cachedIn(viewModelScope)
                        .collectLatest {
                            _searchTagList.emit(it)
                            requestSearchKeywordRecentUseCase().let {
                                _searchHistory.emit(it)
                            }
                        }
                }

                SearchHistoryType.USER -> {
                    requestSearchTotalCountUseCase(keyword, SearchHistoryType.USER).let {
                        _searchUserTotalCount.emit(it)
                    }

                    requestSearchUserUseCase(nickname = keyword)
                        .cachedIn(viewModelScope)
                        .collectLatest {
                            _searchUserList.emit(it)
                            requestSearchKeywordRecentUseCase().let {
                                _searchHistory.emit(it)
                            }
                        }
                }
            }
        }

    suspend fun deleteSearchKeywordRecent(keyword: String, deleteKeywordType: SearchHistoryType) {
        deleteSearchKeywordRecentUseCase(keyword, deleteKeywordType).let {
            _searchHistory.emit(it)
        }
    }

    suspend fun clearSearchKeywordRecent() {
        clearSearchKeywordRecentUseCase().let {
            _searchHistory.emit(it)
        }
    }
}