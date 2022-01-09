package com.daily.dayo.write.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.repository.WriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class WriteOptionViewModel @Inject constructor(private val writeRepository: WriteRepository) : ViewModel() {

    val writePostId = MutableLiveData<Int>()
    val writeSuccess = MutableLiveData<Boolean>()

    fun requestUploadPost(postCategory: String, postContents: String, files: Array<File>,
                   postFolderId: Int, memberId: String, postPrivacy: String, postTags: Array<String>) = viewModelScope.launch {
        val response = writeRepository.requestUploadPost(postCategory, postContents, files, postFolderId, memberId, postPrivacy, postTags)
        if(response.isSuccessful) {
            writePostId.postValue(response.body()?.id)
            writeSuccess.postValue(true)
        } else {
            writeSuccess.postValue(false)
        }
    }
}