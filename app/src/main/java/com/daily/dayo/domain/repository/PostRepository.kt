package com.daily.dayo.domain.repository

import com.daily.dayo.data.datasource.remote.post.*
import com.daily.dayo.domain.model.Category
import okhttp3.MultipartBody
import retrofit2.Response

interface PostRepository {

    suspend fun requestUploadPost(
        category: Category,
        contents: String,
        files: List<MultipartBody.Part>,
        folderId: Int,
        tags: Array<String>
    ): Response<CreatePostResponse>

    suspend fun requestNewPostList(): Response<ListAllPostResponse>
    suspend fun requestNewPostListCategory(category: Category): Response<ListCategoryPostResponse>
    suspend fun requestDayoPickPostList(): Response<DayoPickPostListResponse>
    suspend fun requestDayoPickPostListCategory(category: Category): Response<DayoPickPostListResponse>
    suspend fun requestFeedList(): Response<ListFeedResponse>
    suspend fun requestPostDetail(postId: Int): Response<DetailPostResponse>
    suspend fun requestDeletePost(postId: Int): Response<Void>
    suspend fun requestEditPost(postId: Int, request: EditPostRequest): Response<EditPostResponse>
}