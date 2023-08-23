package daily.dayo.data.datasource.remote.search

import com.google.gson.annotations.SerializedName

data class SearchResultResponse(
    @SerializedName("allCount")
    val totalCount: Int,
    @SerializedName("count")
    val count: Int,
    @SerializedName("last")
    val last: Boolean,
    @SerializedName("data")
    val data: List<SearchDto>,
)

data class SearchDto(
    @SerializedName("postId")
    val postId: Int,
    @SerializedName("thumbnailImage")
    val thumbnailImage: String,
)
