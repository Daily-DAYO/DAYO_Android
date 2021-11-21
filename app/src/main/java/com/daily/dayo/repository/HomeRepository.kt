package com.daily.dayo.repository

import com.daily.dayo.network.home.HomeApiHelperImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class HomeRepository @Inject constructor(private val homeApiHelperImpl: HomeApiHelperImpl) {
    suspend fun requestPostList() = flow {
        runCatching {
            homeApiHelperImpl.requestPostList()
        }.getOrNull()?.let { postList ->
            emit(postList.posts)
        } ?: emit(null)
    }.flowOn(Dispatchers.IO)
}