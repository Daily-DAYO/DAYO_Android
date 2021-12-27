package com.daily.dayo.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.repository.FolderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FolderAddViewModel @Inject constructor(
    private val folderRepository: FolderRepository
) : ViewModel(){

    fun requestCreateFolder(memberId : String, name:String, subheading:String?, thumbnailImage: File?) = viewModelScope.launch {
        folderRepository.requestCreateFolder(memberId,name,subheading,thumbnailImage)
    }
}