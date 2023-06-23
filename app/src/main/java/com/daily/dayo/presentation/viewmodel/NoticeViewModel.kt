package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.daily.dayo.domain.model.Notice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

//@HiltViewModel // TODO 서버연결로 인해 Data Layer 구현시 Hilt사용하므로 잊지 않고 추가하기 위해 미리 남김
class NoticeViewModel: ViewModel() {
    private val _noticeList = MutableStateFlow<PagingData<Notice>>(PagingData.empty())
    val noticeList = _noticeList.asStateFlow()
}