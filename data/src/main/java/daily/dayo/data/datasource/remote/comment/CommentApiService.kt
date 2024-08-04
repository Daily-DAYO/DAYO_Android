package daily.dayo.data.datasource.remote.comment

import daily.dayo.domain.model.NetworkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentApiService {

    /*** v2 ***/
    @POST("/api/v2/comments")
    suspend fun requestCreatePostComment(@Body body: CreateCommentRequest): NetworkResponse<CreateCommentResponse>

    @POST("/api/v2/comments/reply")
    suspend fun requestCreatePostCommentReply(@Body body: CreateCommentReplyRequest): NetworkResponse<CreateCommentResponse>

    /*** v1 ***/
    @GET("/api/v1/comments/{postId}")
    suspend fun requestPostComment(@Path("postId") postId: Int): NetworkResponse<ListAllCommentResponse>

    @POST("/api/v1/comments/delete/{commentId}")
    suspend fun requestDeletePostComment(@Path("commentId") commentId: Int): NetworkResponse<Void>

}