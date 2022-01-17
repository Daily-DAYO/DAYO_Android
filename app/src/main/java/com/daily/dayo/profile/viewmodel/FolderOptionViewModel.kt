package com.daily.dayo.profile.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.repository.FolderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderOptionViewModel @Inject constructor(private val folderRepository: FolderRepository) : ViewModel() {

    val deleteSuccess = MutableLiveData<Boolean>()

    fun requestDeleteFolder(folderId : Int) = viewModelScope.launch {
        val response = folderRepository.requestDeleteFolder(folderId)
        if(response.isSuccessful){
            deleteSuccess.postValue(true)
        }
        else{
            deleteSuccess.postValue(false)
        }
    }

}