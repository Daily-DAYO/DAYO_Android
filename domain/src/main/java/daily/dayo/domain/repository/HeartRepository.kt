package daily.dayo.domain.repository

import androidx.paging.PagingData
import daily.dayo.domain.model.LikePost
import daily.dayo.domain.model.LikePostDeleteResponse
import daily.dayo.domain.model.LikePostResponse
import daily.dayo.domain.model.LikeUser
import daily.dayo.domain.model.NetworkResponse
import kotlinx.coroutines.flow.Flow

interface HeartRepository {

    suspend fun requestLikePost(postId: Int): NetworkResponse<LikePostResponse>
    suspend fun requestUnlikePost(postId: Int): NetworkResponse<LikePostDeleteResponse>
    suspend fun requestAllMyLikePostList(): Flow<PagingData<LikePost>>
    suspend fun requestPostLikeUsers(postId: Int): Flow<PagingData<LikeUser>>
}