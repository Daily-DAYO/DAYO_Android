package com.daily.dayo.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.DayoApplication
import com.daily.dayo.SharedManager
import com.daily.dayo.profile.model.ResponseAllFolderList
import com.daily.dayo.repository.FolderRepository
import com.daily.dayo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderSettingViewModel @Inject constructor(
    private val folderRepository: FolderRepository
) : ViewModel(){

    private val _folderList = MutableLiveData<Resource<ResponseAllFolderList>>()
    val folderList : LiveData<Resource<ResponseAllFolderList>> get() = _folderList

    init {
        requestAllFolderList()
    }

    fun requestAllFolderList() = viewModelScope.launch {
        val memberId:String = SharedManager(DayoApplication.applicationContext()).getCurrentUser().memberId.toString()
        _folderList.postValue(Resource.loading(null))
        folderRepository.requestAllFolderList(memberId).let {
            if(it.isSuccessful){
                _folderList.postValue(Resource.success(it.body()))
            } else{
                _folderList.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }
}