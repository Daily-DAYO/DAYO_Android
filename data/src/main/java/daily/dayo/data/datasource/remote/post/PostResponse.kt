package daily.dayo.data.datasource.remote.post

import com.google.gson.annotations.SerializedName
import daily.dayo.domain.model.Category

data class ListFeedResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("last")
    val last: Boolean,
    @SerializedName("data")
    val data: List<FeedDto>
)

data class EditPostResponse(
    @SerializedName("postId")
    val postId: Long
)

data class CreatePostResponse(
    @SerializedName("id")
    val id: Long
)

data class ListAllPostResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val data: List<PostDto>
)

data class ListCategoryPostResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val data: List<PostDto>
)

data class DayoPickPostListResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val data: List<DayoPick>
)

data class DetailPostResponse(
    @SerializedName("bookmark")
    val bookmark: Boolean,
    @SerializedName("category")
    val category: Category,
    @SerializedName("contents")
    val contents: String,
    @SerializedName("createDateTime")
    val createDateTime: String,
    @SerializedName("folderId")
    val folderId: Int,
    @SerializedName("folderName")
    val folderName: String,
    @SerializedName("hashtags")
    val hashtags: List<String>,
    @SerializedName("heart")
    val heart: Boolean,
    @SerializedName("heartCount")
    val heartCount: Int,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImg")
    val profileImg: String
)

data class PostDto(
    @SerializedName("commentCount")
    val commentCount: Int,
    @SerializedName("heart")
    val heart: Boolean,
    @SerializedName("heartCount")
    val heartCount: Int,
    @SerializedName("id")
    val postId: Long,
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("thumbnailImage")
    val thumbnailImage: String,
    @SerializedName("userProfileImage")
    val userProfileImage: String
)

data class DayoPick(
    @SerializedName("commentCount")
    val commentCount: Int,
    @SerializedName("heart")
    val heart: Boolean,
    @SerializedName("heartCount")
    val heartCount: Int,
    @SerializedName("id")
    val postId: Long,
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("thumbnailImage")
    val thumbnailImage: String,
    @SerializedName("userProfileImage")
    val userProfileImage: String
)

data class FeedDto(
    @SerializedName("bookmark")
    val bookmark: Boolean,
    @SerializedName("category")
    val category: Category,
    @SerializedName("commentCount")
    val commentCount: Int,
    @SerializedName("contents")
    val contents: String,
    @SerializedName("createTime")
    val createTime: String,
    @SerializedName("hashtags")
    val hashtags: List<String>,
    @SerializedName("heart")
    val heart: Boolean,
    @SerializedName("heartCount")
    val heartCount: Int,
    @SerializedName("id")
    val postId: Long,
    @SerializedName("memberId")
    val memberId: String,
    @SerializedName("nickname")
    val nickname: String?,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("userProfileImage")
    val userProfileImage: String
)