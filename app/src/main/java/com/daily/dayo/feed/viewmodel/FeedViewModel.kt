package com.daily.dayo.feed.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.dayo.feed.model.ResponseFeedList
import com.daily.dayo.post.model.RequestBookmarkPost
import com.daily.dayo.post.model.RequestLikePost
import com.daily.dayo.post.model.ResponseBookmarkPost
import com.daily.dayo.post.model.ResponseLikePost
import com.daily.dayo.repository.FeedRepository
import com.daily.dayo.repository.PostRepository
import com.daily.dayo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(private val feedRepository: FeedRepository, private val postRepository: PostRepository):ViewModel(){
    private val _feedList = MutableLiveData<Resource<ResponseFeedList>>()
    val feedList: LiveData<Resource<ResponseFeedList>> get() = _feedList

    private val _postliked = MutableLiveData<Resource<ResponseLikePost>>()
    val postLiked: LiveData<Resource<ResponseLikePost>> get() = _postliked

    private val _postBookmarked = MutableLiveData<Resource<ResponseBookmarkPost>>()
    val postBookmarked: LiveData<Resource<ResponseBookmarkPost>> get() = _postBookmarked

    fun requestFeedList() = viewModelScope.launch {
        _feedList.postValue(Resource.loading(null))
        feedRepository.requestFeedList().let {
            if(it.isSuccessful){
                _feedList.postValue(Resource.success(it.body()))
            } else{
                _feedList.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestLikePost(request: RequestLikePost) = viewModelScope.launch {
        feedRepository.requestLikePost(request).let {
            if(it.isSuccessful) {
                _postliked.postValue(Resource.success(it.body()))
            } else {
                _postliked.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestUnlikePost(postId: Int) = viewModelScope.launch {
        feedRepository.requestUnlikePost(postId)
    }

    fun requestBookmarkPost(request: RequestBookmarkPost) = viewModelScope.launch {
        postRepository.requestBookmarkPost(request).let {
            if(it.isSuccessful) {
                _postBookmarked.postValue(Resource.success(it.body()))
            } else {
                _postBookmarked.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

    fun requestDeleteBookmarkPost(postId: Int) = viewModelScope.launch {
        postRepository.requestDeleteBookmarkPost(postId)
    }
}