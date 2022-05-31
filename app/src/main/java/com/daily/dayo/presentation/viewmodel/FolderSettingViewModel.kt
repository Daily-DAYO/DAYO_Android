package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.common.Event
import com.daily.dayo.common.Resource
import com.daily.dayo.data.mapper.toEditOrderDto
import com.daily.dayo.data.mapper.toFolder
import com.daily.dayo.domain.model.Folder
import com.daily.dayo.domain.model.FolderOrder
import com.daily.dayo.domain.model.Privacy
import com.daily.dayo.domain.usecase.folder.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FolderSettingViewModel @Inject constructor(
    private val requestCreateFolderUseCase: RequestCreateFolderUseCase,
    private val requestEditFolderUseCase: RequestEditFolderUseCase,
    private val requestDeleteFolderUseCase: RequestDeleteFolderUseCase,
    private val requestDetailListFolderUseCase: RequestDetailListFolderUseCase,
    private val requestAllMyFolderListUseCase: RequestAllMyFolderListUseCase,
    private val requestOrderFolderUseCase: RequestOrderFolderUseCase
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
    val folderAddAccess: LiveData<Event<Boolean>> get() = _folderAddSuccess

    private val _detailFolderList = MutableLiveData<Resource<Folder>>()
    val detailFolderList: LiveData<Resource<Folder>> get() = _detailFolderList

    fun requestCreateFolder(name: String, privacy: Privacy, subheading: String?, thumbnailImg: File?) = viewModelScope.launch {
        requestCreateFolderUseCase(name = name, privacy = privacy, subheading = subheading, thumbnailImg = thumbnailImg).let {
            if (it.isSuccessful) {
                _folderAddSuccess.postValue(Event(true))
            } else {
                _folderAddSuccess.postValue(Event(false))
            }
        }
    }

    fun requestEditFolder(folderId: Int, name: String, privacy: Privacy, subheading: String?, isFileChange: Boolean, thumbnailImage: File?) = viewModelScope.launch {
        requestEditFolderUseCase(folderId = folderId, name = name, privacy = privacy, subheading = subheading, isFileChange = isFileChange, thumbnailImage).let {
            if (it.isSuccessful) {
                _editSuccess.postValue(Event(true))
            } else {
                _editSuccess.postValue(Event(false))
            }
        }
    }

    fun requestDeleteFolder(folderId: Int) = viewModelScope.launch {
        requestDeleteFolderUseCase(folderId = folderId).let {
            if (it.isSuccessful) {
                _deleteSuccess.postValue(Event(true))
            } else {
                _deleteSuccess.postValue(Event(false))
            }
        }
    }

    fun requestDetailListFolder(folderId: Int) = viewModelScope.launch {
        _detailFolderList.postValue(Resource.loading(null))
        requestDetailListFolderUseCase(folderId = folderId).let {
            if (it.isSuccessful) {
                _detailFolderList.postValue(Resource.success(it.body()?.toFolder()))
                _thumbnailUri.postValue("http://117.17.198.45:8080/images/" + it.body()!!.thumbnailImage)
            } else {
                _detailFolderList.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestAllMyFolderList() = viewModelScope.launch {
        _folderList.postValue(Resource.loading(null))
        val response = requestAllMyFolderListUseCase()
        if (response.isSuccessful) {
            _folderList.postValue(Resource.success(response.body()?.data?.map { it.toFolder() }))
        } else {
            _folderList.postValue(Resource.error(response.errorBody().toString(), null))
        }
    }

    fun requestOrderFolder(folderOrder: List<FolderOrder>) = viewModelScope.launch {
        requestOrderFolderUseCase(folderOrder = folderOrder.map { it.toEditOrderDto() }).let {
            if (it.isSuccessful) {
                _orderFolderSuccess.postValue(Event(true))
            }
            else {
                _orderFolderSuccess.postValue(Event(false))
            }
        }
    }
}