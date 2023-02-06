package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.post.*
import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.model.NetworkResponse
import okhttp3.MultipartBody

interface PostRepository {

    suspend fun requestUploadPost(
        category: Category,
        contents: String,
        files: List<MultipartBody.Part>,
        folderId: Int,
        tags: Array<String>
    ): NetworkResponse<CreatePostResponse>

    suspend fun requestNewPostList(): NetworkResponse<ListAllPostResponse>
    suspend fun requestNewPostListCategory(category: Category): NetworkResponse<ListCategoryPostResponse>
    suspend fun requestDayoPickPostList(): NetworkResponse<DayoPickPostListResponse>
    suspend fun requestDayoPickPostListCategory(category: Category): NetworkResponse<DayoPickPostListResponse>
    suspend fun requestFeedList(): NetworkResponse<ListFeedResponse>
    suspend fun requestPostDetail(postId: Int): NetworkResponse<DetailPostResponse>
    suspend fun requestDeletePost(postId: Int): NetworkResponse<Void>
    suspend fun requestEditPost(postId: Int, request: EditPostRequest): NetworkResponse<EditPostResponse>
}