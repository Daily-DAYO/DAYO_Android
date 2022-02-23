package com.daily.dayo.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.profile.model.ResponseDetailListFolder
import com.daily.dayo.repository.FolderRepository
import com.daily.dayo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FolderEditViewModel@Inject constructor(
    private val folderRepository: FolderRepository
) : ViewModel(){

    private val _detailFolderList = MutableLiveData<Resource<ResponseDetailListFolder>>()
    val detailFolderList : LiveData<Resource<ResponseDetailListFolder>> get() = _detailFolderList

    val _thumbnailUri = MutableLiveData<String>()
    val thumbnailUri : LiveData<String> get() = _thumbnailUri

    val editSuccess = MutableLiveData<Boolean>()

    fun requestEditFolder(folderId:Int, name:String, privacy:String, subheading:String?, isFileChange:Boolean, thumbnailImage: File?) = viewModelScope.launch {
        val response = folderRepository.requestEditFolder(folderId,name,privacy,subheading, isFileChange,thumbnailImage)
        if(response.isSuccessful){
            editSuccess.postValue(true)
        }else{
            editSuccess.postValue(false)
        }
    }

    fun requestDetailListFolder(folderId:Int) = viewModelScope.launch {
        _detailFolderList.postValue(Resource.loading(null))
        folderRepository.requestDetailListFolder(folderId).let {
            if(it.isSuccessful){
                _detailFolderList.postValue(Resource.success(it.body()))
                _thumbnailUri.postValue("http://117.17.198.45:8080/images/" + it.body()!!.thumbnailImage)
            } else{
                _detailFolderList.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }
}