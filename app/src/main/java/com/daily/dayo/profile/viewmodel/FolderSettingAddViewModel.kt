package com.daily.dayo.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.repository.FolderRepository
import com.daily.dayo.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FolderSettingAddViewModel@Inject constructor(
    private val folderRepository: FolderRepository
) : ViewModel(){

    private val _folderAddSuccess = MutableLiveData<Event<Boolean>>()
    val folderAddAccess : LiveData<Event<Boolean>> get() = _folderAddSuccess


    fun requestCreateFolder(name:String, privacy:String, subheading:String?, thumbnailImg: File?) = viewModelScope.launch {
        folderRepository.requestCreateFolder(name,privacy,subheading,thumbnailImg).let {
            if(it.isSuccessful) {
                _folderAddSuccess.postValue(Event(true))
            } else {
                _folderAddSuccess.postValue(Event(false))
            }
        }
    }
}