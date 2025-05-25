package daily.dayo.domain.repository

import androidx.paging.PagingData
import daily.dayo.domain.model.LikePost
import daily.dayo.domain.model.LikePostResponse
import daily.dayo.domain.model.LikeUser
import daily.dayo.domain.model.NetworkResponse
import kotlinx.coroutines.flow.Flow

interface HeartRepository {

    suspend fun requestLikePost(postId: Long): NetworkResponse<LikePostResponse>
    suspend fun requestUnlikePost(postId: Long): NetworkResponse<LikePostResponse>
    suspend fun requestAllMyLikePostList(): Flow<PagingData<LikePost>>
    suspend fun requestPostLikeUsers(postId: Long): Flow<PagingData<LikeUser>>
}