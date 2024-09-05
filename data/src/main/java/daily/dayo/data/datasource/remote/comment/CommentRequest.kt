package daily.dayo.data.datasource.remote.comment

import com.google.gson.annotations.SerializedName

data class CreateCommentRequest(
    @SerializedName("contents")
    val contents: String,
    @SerializedName("postId")
    val postId: Int,
    @SerializedName("mentionList")
    val mentionList: List<MentionUserDto>
)

data class CreateCommentReplyRequest(
    @SerializedName("commentId")
    val commentId: Long,
    @SerializedName("contents")
    val contents: String,
    @SerializedName("postId")
    val postId: Int,
    @SerializedName("mentionList")
    val mentionList: List<MentionUserDto>
)