package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Notice
import daily.dayo.domain.usecase.notice.RequestAllNoticeListUseCase
import daily.dayo.domain.usecase.notice.RequestDetailNoticeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _detailNotice = MutableLiveData<String>()
    val detailNotice: LiveData<String> get() = _detailNotice

    fun requestAllNoticeList() = viewModelScope.launch {
        requestAllNoticeListUseCase()
            .cachedIn(viewModelScope)
            .collectLatest { _noticeList.emit(it) }
    }

    fun requestDetailNotice(noticeId: Int) = viewModelScope.launch {
        requestDetailNoticeUseCase(noticeId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> _detailNotice.postValue(ApiResponse.body?.contents.toString())
                else -> _detailNotice.postValue("")
            }
        }
    }
}