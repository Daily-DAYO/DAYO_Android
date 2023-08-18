package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.daily.dayo.common.Event
import com.daily.dayo.common.Resource
import com.daily.dayo.data.mapper.toEditOrderDto
import com.daily.dayo.data.mapper.toFolder
import com.daily.dayo.domain.model.*
import com.daily.dayo.domain.usecase.folder.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val requestCreateFolderUseCase: RequestCreateFolderUseCase,
    private val requestEditFolderUseCase: RequestEditFolderUseCase,
    private val requestDeleteFolderUseCase: RequestDeleteFolderUseCase,
    private val requestOrderFolderUseCase: RequestOrderFolderUseCase,
    private val requestAllMyFolderListUseCase: RequestAllMyFolderListUseCase,
    private val requestFolderInfoUseCase: RequestFolderInfoUseCase,
    private val requestFolderPostListUseCase: RequestFolderPostListUseCase
) : ViewModel() {

    private val _deleteSuccess = MutableLiveData<Event<Boolean>>()
    val deleteSuccess: LiveData<Event<Boolean>> get() = _deleteSuccess

    private val _editSuccess = MutableLiveData<Event<Boolean>>()
    val editSuccess: LiveData<Event<Boolean>> get() = _editSuccess

    private val _thumbnailUri = MutableLiveData<String>()
    val thumbnailUri: LiveData<String> get() = _thumbnailUri

    private val _folderList = MutableLiveData<Resource<List<Folder>>>()
    val folderList: LiveData<Resource<List<Folder>>> get() = _folderList

    private val _orderFolderSuccess = MutableLiveData<Event<Boolean>>()
    val orderFolderSuccess: LiveData<Event<Boolean>> get() = _orderFolderSuccess

    private val _folderAddSuccess = MutableLiveData<Event<Boolean>>()
    val folderAddSuccess: LiveData<Event<Boolean>> get() = _folderAddSuccess

    private val _folderInfo = MutableLiveData<Resource<Folder>>()
    val folderInfo: LiveData<Resource<Folder>> get() = _folderInfo

    private val _folderPostList = MutableLiveData<PagingData<FolderPost>>()
    val folderPostList: LiveData<PagingData<FolderPost>> get() = _folderPostList

    fun requestCreateFolder(name: String, privacy: Privacy, subheading: String?, thumbnailImg: File?) = viewModelScope.launch {
        requestCreateFolderUseCase(name = name, privacy = privacy, subheading = subheading, thumbnailImg = thumbnailImg).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _folderAddSuccess.postValue(Event(true))
                }
                else -> {
                    _folderAddSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestEditFolder(folderId: Int, name: String, privacy: Privacy, subheading: String?, isFileChange: Boolean, thumbnailImage: File?) = viewModelScope.launch {
        requestEditFolderUseCase(folderId = folderId, name = name, privacy = privacy, subheading = subheading, isFileChange = isFileChange, thumbnailImage).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _editSuccess.postValue(Event(true))
                }
                else -> {
                    _editSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestDeleteFolder(folderId: Int) = viewModelScope.launch {
        requestDeleteFolderUseCase(folderId = folderId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _deleteSuccess.postValue(Event(true))
                }
                else -> {
                    _deleteSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestOrderFolder(folderOrder: List<FolderOrder>) = viewModelScope.launch {
        requestOrderFolderUseCase(folderOrder = folderOrder.map { it.toEditOrderDto() }).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _orderFolderSuccess.postValue(Event(true))
                }
                else -> {
                    _orderFolderSuccess.postValue(Event(false))
                }
            }
        }
    }

    fun requestAllMyFolderList() = viewModelScope.launch {
        _folderList.postValue(Resource.loading(null))
        requestAllMyFolderListUseCase().let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _folderList.postValue(Resource.success(ApiResponse.body?.data?.map { it.toFolder() }))
                }
                is NetworkResponse.NetworkError -> {
                    _folderList.postValue(Resource.error(ApiResponse.exception.toString(), null))
                }
                is NetworkResponse.ApiError -> {
                    _folderList.postValue(Resource.error(ApiResponse.error.toString(), null))
                }
                is NetworkResponse.UnknownError -> {
                    _folderList.postValue(Resource.error(ApiResponse.throwable.toString(), null))
                }
            }
        }
    }

    fun requestFolderInfo(folderId: Int) = viewModelScope.launch {
        requestFolderInfoUseCase(folderId).let { ApiResponse ->
            when (ApiResponse) {
                is NetworkResponse.Success -> {
                    _folderInfo.postValue(Resource.success(ApiResponse.body?.toFolder()))
                }
                is NetworkResponse.NetworkError -> {
                    _folderInfo.postValue(Resource.error(ApiResponse.exception.toString(), null))
                }
                is NetworkResponse.ApiError -> {
                    _folderInfo.postValue(Resource.error(ApiResponse.error.toString(), null))
                }
                is NetworkResponse.UnknownError -> {
                    _folderInfo.postValue(Resource.error(ApiResponse.throwable.toString(), null))
                }
            }
        }
    }

    fun requestFolderPostList(folderId: Int) = viewModelScope.launch {
        requestFolderPostListUseCase(folderId)
            .cachedIn(viewModelScope)
            .collectLatest { _folderPostList.postValue(it) }
    }
}