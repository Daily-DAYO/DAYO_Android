package com.daily.dayo.domain.repository

import androidx.paging.PagingData
import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.model.LikePostDeleteResponse
import com.daily.dayo.domain.model.NetworkResponse
import com.daily.dayo.domain.model.Post
import com.daily.dayo.domain.model.PostCreateResponse
import com.daily.dayo.domain.model.PostDetail
import com.daily.dayo.domain.model.PostEditResponse
import com.daily.dayo.domain.model.Posts
import com.daily.dayo.domain.model.PostsCategorized
import com.daily.dayo.domain.model.PostsDayoPick
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface PostRepository {

    suspend fun requestUploadPost(
        category: Category,
        contents: String,
        files: List<MultipartBody.Part>,
        folderId: Int,
        tags: Array<String>
    ): NetworkResponse<PostCreateResponse>

    suspend fun requestNewPostList(): NetworkResponse<Posts>
    suspend fun requestNewPostListCategory(category: Category): NetworkResponse<PostsCategorized>
    suspend fun requestDayoPickPostList(): NetworkResponse<PostsDayoPick>
    suspend fun requestDayoPickPostListCategory(category: Category): NetworkResponse<PostsDayoPick>
    suspend fun requestPostDetail(postId: Int): NetworkResponse<PostDetail>
    suspend fun requestDeletePost(postId: Int): NetworkResponse<LikePostDeleteResponse>
    suspend fun requestEditPost(
        postId: Int,
        category: Category,
        contents: String,
        folderId: Int,
        hashtags: List<String>
    ): NetworkResponse<PostEditResponse>

    suspend fun requestFeedList(): Flow<PagingData<Post>>
}