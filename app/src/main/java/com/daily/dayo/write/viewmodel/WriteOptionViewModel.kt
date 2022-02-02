package com.daily.dayo.write.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.repository.WriteRepository
import com.daily.dayo.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class WriteOptionViewModel @Inject constructor(private val writeRepository: WriteRepository) : ViewModel() {

    private val _writePostId = MutableLiveData<Event<Int>>()
    val writePostId : LiveData<Event<Int>> get() = _writePostId
    private val _writeSuccess = MutableLiveData<Event<Boolean>>()
    val writeSuccess : LiveData<Event<Boolean>> get() =_writeSuccess

    fun requestUploadPost(postCategory: String, postContents: String, files: Array<File>,
                   postFolderId: Int, memberId: String, postPrivacy: String, postTags: Array<String>) = viewModelScope.launch {
        val response = writeRepository.requestUploadPost(postCategory, postContents, files, postFolderId, memberId, postPrivacy, postTags)
        if(response.isSuccessful) {
            _writePostId.postValue(Event(response.body()?.id) as Event<Int>?)
            _writeSuccess.postValue(Event(true))
        } else {
            _writeSuccess.postValue(Event(false))
        }
    }
}