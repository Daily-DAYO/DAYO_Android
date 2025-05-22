package daily.dayo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Notice
import daily.dayo.domain.usecase.notice.RequestAllNoticeListUseCase
import daily.dayo.domain.usecase.notice.RequestDetailNoticeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import daily.dayo.presentation.common.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val requestAllNoticeListUseCase: RequestAllNoticeListUseCase,
    private val requestDetailNoticeUseCase: RequestDetailNoticeUseCase
) : ViewModel() {

    private val _noticeList = MutableStateFlow<PagingData<Notice>>(PagingData.empty())
    val noticeList = _noticeList.asStateFlow()

    private val _detailNotice = MutableStateFlow<Resource<String>?>(null)
    val detailNotice: StateFlow<Resource<String>?> get() = _detailNotice

    private val _selectedNotice = MutableStateFlow<Notice?>(null)
    val selectedNotice: StateFlow<Notice?> get() = _selectedNotice

    fun selectNotice(notice: Notice) {
        _selectedNotice.value = notice
    }

    fun requestAllNoticeList() = viewModelScope.launch {
        requestAllNoticeListUseCase()
            .cachedIn(viewModelScope)
            .collectLatest { _noticeList.emit(it) }
    }

    fun requestDetailNotice(noticeId: Long) = viewModelScope.launch {
        requestDetailNoticeUseCase(noticeId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> _detailNotice.emit(Resource.success(ApiResponse.body?.contents.toString()))
                else -> _detailNotice.emit(Resource.error(ApiResponse.toString(), null))
            }
        }
    }
}