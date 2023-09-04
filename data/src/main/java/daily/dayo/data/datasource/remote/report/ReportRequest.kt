package daily.dayo.data.datasource.remote.report

import com.google.gson.annotations.SerializedName

data class CreateReportMemberRequest(
    @SerializedName("comment")
    val comment: String,
    @SerializedName("memberId")
    val memberId: String
)

data class CreateReportPostRequest(
    @SerializedName("comment")
    val comment: String,
    @SerializedName("postId")
    val postId: Int
)