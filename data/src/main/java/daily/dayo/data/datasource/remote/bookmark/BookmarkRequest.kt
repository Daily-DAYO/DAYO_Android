package daily.dayo.data.datasource.remote.bookmark

import com.google.gson.annotations.SerializedName

data class CreateBookmarkRequest(
    @SerializedName("postId")
    val postId: Int
)