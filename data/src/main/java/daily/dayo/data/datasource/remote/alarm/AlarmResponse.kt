package daily.dayo.data.datasource.remote.alarm

import com.google.gson.annotations.SerializedName
import daily.dayo.domain.model.Topic

data class ListAllAlarmResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("last")
    val last: Boolean,
    @SerializedName("data")
    val data: List<AlarmDto>
)

data class AlarmDto(
    @SerializedName("alarmId")
    val alarmId: Int?,
    @SerializedName("category")
    val category: Topic?,
    @SerializedName("check")
    val check: Boolean?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("createdTime")
    val createdTime: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("nickname")
    val nickname: String?,
    @SerializedName("memberId")
    val memberId: String?,
    @SerializedName("postId")
    val postId: Long?
)