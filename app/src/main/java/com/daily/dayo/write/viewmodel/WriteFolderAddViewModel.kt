package com.daily.dayo.write.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.profile.model.RequestCreateFolderInPost
import com.daily.dayo.repository.FolderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class WriteFolderAddViewModel @Inject constructor(
    private val folderRepository: FolderRepository
) : ViewModel(){

    fun requestCreateFolderInPost(name:String, privacy:String) = viewModelScope.launch {
        folderRepository.requestCreateFolderInPost(RequestCreateFolderInPost(name,privacy))
    }
}