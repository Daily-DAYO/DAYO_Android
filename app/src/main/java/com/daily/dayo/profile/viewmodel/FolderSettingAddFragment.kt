package com.daily.dayo.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.repository.FolderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FolderSettingAddViewModel@Inject constructor(
    private val folderRepository: FolderRepository
) : ViewModel(){

    fun requestCreateFolder(name:String, privacy:String, subheading:String?, thumbnailImg: File?) = viewModelScope.launch {
        folderRepository.requestCreateFolder(name,privacy,subheading,thumbnailImg)
    }
}