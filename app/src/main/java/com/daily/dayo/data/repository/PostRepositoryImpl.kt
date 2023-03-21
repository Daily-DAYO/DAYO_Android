package com.daily.dayo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.daily.dayo.data.datasource.remote.post.*
import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.repository.PostRepository
import okhttp3.MultipartBody
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
    ): NetworkResponse<CreatePostResponse> =
        postApiService.requestUploadPost(category.name, contents, files, folderId, tags)

    override suspend fun requestEditPost(
        postId: Int,
        request: EditPostRequest
    ): NetworkResponse<EditPostResponse> = postApiService.requestEditPost(postId, request)

    override suspend fun requestNewPostList(): NetworkResponse<ListAllPostResponse> =
        postApiService.requestNewPostList()

    override suspend fun requestNewPostListCategory(category: Category): NetworkResponse<ListCategoryPostResponse> =
        postApiService.requestNewPostListCategory(category)

    override suspend fun requestDayoPickPostList(): NetworkResponse<DayoPickPostListResponse> =
        postApiService.requestDayoPickPostList()

    override suspend fun requestDayoPickPostListCategory(category: Category): NetworkResponse<DayoPickPostListResponse> =
        postApiService.requestDayoPickPostListCategory(category)

    override suspend fun requestPostDetail(postId: Int): NetworkResponse<DetailPostResponse> =
        postApiService.requestPostDetail(postId)

    override suspend fun requestDeletePost(postId: Int): NetworkResponse<Void> =
        postApiService.requestDeletePost(postId)

    override fun requestFeedList() = Pager(PagingConfig(pageSize = FEED_PAGE_SIZE)) {
        FeedPagingSource(postApiService, FEED_PAGE_SIZE)
    }.flow

    companion object {
        private const val FEED_PAGE_SIZE = 10
    }
}
