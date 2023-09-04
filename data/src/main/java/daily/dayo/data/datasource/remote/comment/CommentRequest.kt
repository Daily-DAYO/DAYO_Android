package daily.dayo.data.datasource.remote.comment

import com.google.gson.annotations.SerializedName

data class CreateCommentRequest(
    @SerializedName("contents")
    val contents: String,
    @SerializedName("postId")
    val postId: Int
)