package daily.dayo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import daily.dayo.domain.model.Search
import daily.dayo.domain.model.SearchHistory
import daily.dayo.domain.model.SearchHistoryType
import daily.dayo.domain.model.SearchOrder
import daily.dayo.domain.model.SearchUser
import daily.dayo.domain.usecase.search.ClearSearchKeywordRecentUseCase
import daily.dayo.domain.usecase.search.DeleteSearchKeywordRecentUseCase
import daily.dayo.domain.usecase.search.RequestSearchFollowUserUseCase
import daily.dayo.domain.usecase.search.RequestSearchKeywordRecentUseCase
import daily.dayo.domain.usecase.search.RequestSearchKeywordUserUseCase
import daily.dayo.domain.usecase.search.RequestSearchTagUseCase
import daily.dayo.domain.usecase.search.RequestSearchTotalCountUseCase
import daily.dayo.domain.usecase.search.UpdateSearchKeywordRecentUseCase
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
    private val requestSearchFollowUserUseCase: RequestSearchFollowUserUseCase,
    private val requestSearchTotalCountUseCase: RequestSearchTotalCountUseCase
) : ViewModel() {

    var searchKeyword = ""

    private val _searchHashtagOrder = MutableStateFlow(SearchOrder.NEW)
    val searchHashtagOrder get() = _searchHashtagOrder

    private val _searchTagTotalCount = MutableStateFlow(0)
    val searchTagTotalCount get() = _searchTagTotalCount

    private val _searchUserTotalCount = MutableStateFlow(0)
    val searchUserTotalCount get() = _searchUserTotalCount

    private val _searchTagList = MutableStateFlow<PagingData<Search>>(PagingData.empty())
    val searchTagList get() = _searchTagList.asStateFlow()

    private val _searchUserList = MutableStateFlow<PagingData<SearchUser>>(PagingData.empty())
    val searchUserList get() = _searchUserList.asStateFlow()

    private val _searchFollowUserList = MutableStateFlow<PagingData<SearchUser>>(PagingData.empty())
    val searchFollowUserList get() = _searchFollowUserList.asStateFlow()

    private val _searchHistory = MutableStateFlow<SearchHistory>(SearchHistory(0, emptyList()))
    val searchHistory get() = _searchHistory

    suspend fun getSearchKeywordRecent() = requestSearchKeywordRecentUseCase().let {
        _searchHistory.emit(it)
    }

    fun searchKeyword(
        keyword: String,
        keywordType: SearchHistoryType = SearchHistoryType.TAG,
        searchOrder: SearchOrder = SearchOrder.NEW
    ) {
        viewModelScope.launch {
            updateSearchKeywordRecentUseCase(keyword, keywordType)
            when (keywordType) {
                SearchHistoryType.TAG -> {
                    requestSearchTotalCountUseCase(keyword, SearchHistoryType.TAG).let {
                        _searchTagTotalCount.emit(it)
                    }
                    requestSearchTagUseCase(tag = keyword, searchOrder = searchOrder)
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
    }

    suspend fun searchFollowUser(keyword: String) {
        requestSearchFollowUserUseCase(nickname = keyword)
            .cachedIn(viewModelScope)
            .collectLatest {
                _searchFollowUserList.emit(it)
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

    fun searchHashtag(hashtag: String, searchOrder: SearchOrder) {
        viewModelScope.launch {
            requestSearchTotalCountUseCase(tag = hashtag, searchHistoryType = SearchHistoryType.TAG).let {
                _searchTagTotalCount.emit(it)
            }

            requestSearchTagUseCase(tag = hashtag, searchOrder = searchOrder)
                .cachedIn(viewModelScope)
                .collectLatest {
                    _searchTagList.emit(it)
                }
        }
    }

    fun toggleSearchHashtagOrder(hashtag: String) {
        val newOrder = when (_searchHashtagOrder.value) {
            SearchOrder.NEW -> SearchOrder.OLD
            SearchOrder.OLD -> SearchOrder.NEW
        }
        _searchHashtagOrder.value = newOrder
        searchHashtag(hashtag, newOrder)
    }
}