package daily.dayo.domain.repository

import androidx.paging.PagingData
import daily.dayo.domain.model.Category
import daily.dayo.domain.model.LikePostDeleteResponse
import daily.dayo.domain.model.NetworkResponse
import daily.dayo.domain.model.Post
import daily.dayo.domain.model.PostCreateResponse
import daily.dayo.domain.model.PostDetail
import daily.dayo.domain.model.PostEditResponse
import daily.dayo.domain.model.Posts
import daily.dayo.domain.model.PostsCategorized
import daily.dayo.domain.model.PostsDayoPick
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
    suspend fun requestDeletePost(postId: Int): NetworkResponse<Void>
    suspend fun requestEditPost(
        postId: Int,
        category: Category,
        contents: String,
        folderId: Int,
        hashtags: List<String>
    ): NetworkResponse<PostEditResponse>

    suspend fun requestFeedList(): Flow<PagingData<Post>>
}