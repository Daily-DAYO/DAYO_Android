package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.data.datasource.remote.report.CreateReportMemberRequest
import com.daily.dayo.data.datasource.remote.report.CreateReportPostRequest
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.usecase.report.RequestSaveMemberReportUseCase
import com.daily.dayo.domain.usecase.report.RequestSavePostReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val requestSaveMemberReportUseCase: RequestSaveMemberReportUseCase,
    private val requestSavePostReportUseCase: RequestSavePostReportUseCase
) : ViewModel() {

    private val _reportMemberSuccess = MutableLiveData<Boolean>()
    val reportMemberSuccess: LiveData<Boolean> get() = _reportMemberSuccess

    private val _reportPostSuccess = MutableLiveData<Boolean>()
    val reportPostSuccess: LiveData<Boolean> get() = _reportPostSuccess

    fun requestSaveMemberReport(comment: String, memberId: String) = viewModelScope.launch {
        requestSaveMemberReportUseCase(
            CreateReportMemberRequest(
                comment = comment,
                memberId = memberId
            )
        )?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _reportMemberSuccess.postValue(true) }
                else -> { _reportMemberSuccess.postValue(false) }
            }
        }
    }

    fun requestSavePostReport(comment: String, postId: Int) = viewModelScope.launch {
        requestSavePostReportUseCase(
            CreateReportPostRequest(
                comment = comment,
                postId = postId
            )
        )?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _reportPostSuccess.postValue(true) }
                else -> { _reportPostSuccess.postValue(false) }
            }
        }
    }
}
