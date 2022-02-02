package com.daily.dayo.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.profile.model.FolderOrder
import com.daily.dayo.profile.model.ResponseAllMyFolderList
import com.daily.dayo.repository.FolderRepository
import com.daily.dayo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderSettingViewModel @Inject constructor(
    private val folderRepository: FolderRepository
) : ViewModel(){

    private val _folderList = MutableLiveData<Resource<ResponseAllMyFolderList>>()
    val folderList : LiveData<Resource<ResponseAllMyFolderList>> get() = _folderList

    val orderFolderSuccess = MutableLiveData<Boolean>()

    init {
        requestAllMyFolderList()
    }

    fun requestAllMyFolderList() = viewModelScope.launch {
        _folderList.postValue(Resource.loading(null))
        folderRepository.requestAllMyFolderList().let {
            if(it.isSuccessful){
                _folderList.postValue(Resource.success(it.body()))
            } else{
                _folderList.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestOrderFolder(body: List<FolderOrder>) = viewModelScope.launch {
        folderRepository.requestOrderFolder(body).let {
            if(it.isSuccessful)
                orderFolderSuccess.postValue(true)
            else
                orderFolderSuccess.postValue(false)
        }
    }
}