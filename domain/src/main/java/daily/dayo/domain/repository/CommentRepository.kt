package daily.dayo.domain.repository

import daily.dayo.domain.model.Comments
import daily.dayo.domain.model.NetworkResponse

interface CommentRepository {

    suspend fun requestCreatePostComment(contents: String, postId: Int): NetworkResponse<Int>
    suspend fun requestPostComment(postId: Int): NetworkResponse<Comments>
    suspend fun requestDeletePostComment(commentId: Int): NetworkResponse<Void>
}