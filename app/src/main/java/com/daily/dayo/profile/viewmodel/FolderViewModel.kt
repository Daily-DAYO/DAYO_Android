package com.daily.dayo.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.util.Resource
import com.daily.dayo.profile.model.ResponseDetailListFolder
import com.daily.dayo.repository.FolderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel@Inject constructor(
    private val folderRepository: FolderRepository
) : ViewModel() {

    private val _detailFolderList = MutableLiveData<Resource<ResponseDetailListFolder>>()
    val detailFolderList : LiveData<Resource<ResponseDetailListFolder>> get() = _detailFolderList

    fun requestDetailListFolder(folderId:Int) = viewModelScope.launch {
        _detailFolderList.postValue(Resource.loading(null))
        folderRepository.requestDetailListFolder(folderId).let {
            if(it.isSuccessful){
                _detailFolderList.postValue(Resource.success(it.body()))
            } else{
                _detailFolderList.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }
}