package com.daily.dayo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.daily.dayo.data.datasource.remote.post.*
import com.daily.dayo.data.mapper.toLikePostDeleteResponse
import com.daily.dayo.data.mapper.toPostCreateResponse
import com.daily.dayo.data.mapper.toPostDetail
import com.daily.dayo.data.mapper.toPostEditResponse
import com.daily.dayo.data.mapper.toPosts
import com.daily.dayo.data.mapper.toPostsCategorized
import com.daily.dayo.data.mapper.toPostsDayoPick
import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.model.LikePostDeleteResponse
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.model.PostCreateResponse
import com.daily.dayo.domain.model.PostDetail
import com.daily.dayo.domain.model.PostEditResponse
import com.daily.dayo.domain.model.Posts
import com.daily.dayo.domain.model.PostsCategorized
import com.daily.dayo.domain.model.PostsDayoPick
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
    ): NetworkResponse<PostCreateResponse> =
        when (val response =
            postApiService.requestUploadPost(category.name, contents, files, folderId, tags)) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toPostCreateResponse())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestEditPost(
        postId: Int,
        category: Category,
        contents: String,
        folderId: Int,
        hashtags: List<String>
    ): NetworkResponse<PostEditResponse> =
        when (
            val response =
                postApiService.requestEditPost(
                    postId,
                    EditPostRequest(category, contents, folderId, hashtags)
                )) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toPostEditResponse())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestNewPostList(): NetworkResponse<Posts> =
        when (val response = postApiService.requestNewPostList()) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toPosts())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestNewPostListCategory(category: Category): NetworkResponse<PostsCategorized> =
        when (val response = postApiService.requestNewPostListCategory(category)) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toPostsCategorized())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestDayoPickPostList(): NetworkResponse<PostsDayoPick> =
        when (val response = postApiService.requestDayoPickPostList()) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toPostsDayoPick())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestDayoPickPostListCategory(category: Category): NetworkResponse<PostsDayoPick> =
        when (val response = postApiService.requestDayoPickPostListCategory(category)) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toPostsDayoPick())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestPostDetail(postId: Int): NetworkResponse<PostDetail> =
        when (val response = postApiService.requestPostDetail(postId)) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toPostDetail())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestDeletePost(postId: Int): NetworkResponse<LikePostDeleteResponse> =
        when (val response = postApiService.requestDeletePost(postId)) {
            is NetworkResponse.Success -> NetworkResponse.Success(response.body?.toLikePostDeleteResponse())
            is NetworkResponse.NetworkError -> response
            is NetworkResponse.ApiError -> response
            is NetworkResponse.UnknownError -> response
        }

    override suspend fun requestFeedList() = Pager(PagingConfig(pageSize = FEED_PAGE_SIZE)) {
        FeedPagingSource(postApiService, FEED_PAGE_SIZE)
    }.flow

    companion object {
        private const val FEED_PAGE_SIZE = 10
    }
}
