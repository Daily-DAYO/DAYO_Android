package daily.dayo.data.datasource.remote.notice

import com.google.gson.annotations.SerializedName

data class NoticeListResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("last")
    val last: Boolean,
    @SerializedName("data")
    val data: List<NoticeDto>
)

data class NoticeDetailResponse(
    @SerializedName("contents")
    val contents: String
)

data class NoticeDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("createdDate")
    val createdDate: String
)