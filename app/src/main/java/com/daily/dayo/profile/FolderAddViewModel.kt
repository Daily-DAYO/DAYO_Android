package com.daily.dayo.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.profile.model.RequestCreateFolder
import com.daily.dayo.repository.FolderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderAddViewModel @Inject constructor(
    private val folderRepository: FolderRepository
) : ViewModel(){

    fun requestCreateFolder(request: RequestCreateFolder) = viewModelScope.launch {
        folderRepository.requestCreateFolder(request)
    }
}