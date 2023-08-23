package daily.dayo.data.datasource.remote.block

import com.google.gson.annotations.SerializedName

data class BlockRequest(
    @SerializedName("memberId")
    val memberId: String
)

data class UnblockRequest(
    @SerializedName("memberId")
    val memberId: String
)