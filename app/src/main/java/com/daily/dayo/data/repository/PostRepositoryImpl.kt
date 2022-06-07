package com.daily.dayo.data.repository

import com.daily.dayo.data.datasource.remote.post.*
import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.repository.PostRepository
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postApiService: PostApiService
) : PostRepository {

    override suspend fun requestUploadPost(
        category: Category,
        contents: String,
        files: List<MultipartBody.Part>,
        folderId: Int,
        tags: Array<String>
    ): Response<CreatePostResponse> =
        postApiService.requestUploadPost(category.name, contents, files, folderId, tags)

    override suspend fun requestEditPost(
        postId: Int,
        request: EditPostRequest
    ): Response<EditPostResponse> = postApiService.requestEditPost(postId, request)

    override suspend fun requestNewPostList(): Response<ListAllPostResponse> =
        postApiService.requestNewPostList()

    override suspend fun requestNewPostListCategory(category: Category): Response<ListCategoryPostResponse> =
        postApiService.requestNewPostListCategory(category)

    override suspend fun requestDayoPickPostList(): Response<DayoPickPostListResponse> =
        postApiService.requestDayoPickPostList()

    override suspend fun requestDayoPickPostListCategory(category: Category): Response<DayoPickPostListResponse> =
        postApiService.requestDayoPickPostListCategory(category)

    override suspend fun requestFeedList(): Response<ListFeedResponse> =
        postApiService.requestFeedList()

    override suspend fun requestPostDetail(postId: Int): Response<DetailPostResponse> =
        postApiService.requestPostDetail(postId)

    override suspend fun requestDeletePost(postId: Int): Response<Void> =
        postApiService.requestDeletePost(postId)
}
