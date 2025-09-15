package daily.dayo.data.datasource.remote.comment

import com.google.gson.annotations.SerializedName

data class CreateCommentResponse(
    @SerializedName("commentId")
    val commentId: Int
)

data class ListAllCommentResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val data: List<CommentDto>
)

data class CommentDto(
    @SerializedName("commentId")
    val commentId: Long,
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImg")
    val profileImg: String,
    @SerializedName("contents")
    val contents: String,
    @SerializedName("createTime")
    val createTime: String,
    @SerializedName("replyList")
    val replyList: List<CommentDto>,
    @SerializedName("mentionList")
    val mentionList: List<MentionUserDto>
)

data class MentionUserDto(
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("nickname")
    val nickname: String
)