package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.common.Resource
import com.daily.dayo.data.mapper.toFolder
import com.daily.dayo.domain.model.Folder
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.usecase.folder.RequestDetailListFolderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val requestDetailListFolderUseCase: RequestDetailListFolderUseCase
) : ViewModel() {

    private val _detailFolderList = MutableLiveData<Resource<Folder>>()
    val detailFolderList : LiveData<Resource<Folder>> get() = _detailFolderList

    fun requestDetailListFolder(folderId:Int) = viewModelScope.launch {
        _detailFolderList.postValue(Resource.loading(null))
        requestDetailListFolderUseCase(folderId = folderId)?.let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> { _detailFolderList.postValue(Resource.success(ApiResponse.body?.toFolder())) }
                is NetworkResponse.NetworkError -> { _detailFolderList.postValue(Resource.error(ApiResponse.exception.toString(), null)) }
                is NetworkResponse.ApiError -> { _detailFolderList.postValue(Resource.error(ApiResponse.error.toString(), null)) }
                is NetworkResponse.UnknownError -> { _detailFolderList.postValue(Resource.error(ApiResponse.throwable.toString(), null)) }
            }
        }
    }
}