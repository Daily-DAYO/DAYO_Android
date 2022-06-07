package com.daily.dayo.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.common.Resource
import com.daily.dayo.data.mapper.toFolder
import com.daily.dayo.domain.model.Folder
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
        requestDetailListFolderUseCase(folderId = folderId).let {
            if(it.isSuccessful){
                _detailFolderList.postValue(Resource.success(it.body()?.toFolder()))
            } else{
                _detailFolderList.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }
}