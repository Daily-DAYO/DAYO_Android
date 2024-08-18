package daily.dayo.domain.repository

import daily.dayo.domain.model.Comments
import daily.dayo.domain.model.MentionUser
import daily.dayo.domain.model.NetworkResponse

interface CommentRepository {

    suspend fun requestCreatePostComment(contents: String, postId: Int, mentionList: List<MentionUser>): NetworkResponse<Int>
    suspend fun requestCreatePostCommentReply(commentId: Long, contents: String, postId: Int, mentionList: List<MentionUser>): NetworkResponse<Int>
    suspend fun requestPostComment(postId: Int): NetworkResponse<Comments>
    suspend fun requestDeletePostComment(commentId: Long): NetworkResponse<Void>
}