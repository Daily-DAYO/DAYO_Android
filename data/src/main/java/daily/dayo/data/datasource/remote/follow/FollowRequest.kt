package daily.dayo.data.datasource.remote.follow

import com.google.gson.annotations.SerializedName

data class CreateFollowRequest(
    @SerializedName("followerId")
    val followerId: String
)

data class CreateFollowUpRequest(
    @SerializedName("followerId")
    val followerId: String
)