package com.daily.dayo.profile.model

import com.google.gson.annotations.SerializedName

data class ResponseMyProfile(
    @SerializedName("followerCount")
    val followerCount: Int,
    @SerializedName("followingCount")
    val followingCount: Int,
    @SerializedName("email")
    val email:String,
    @SerializedName("nickname")
    val nickname:String,
    @SerializedName("postCount")
    val postCount:	Int,
    @SerializedName("profileImg")
    val profileImg:	String
)

data class ResponseOtherProfile(
    @SerializedName("follow")
    val follow: Boolean,
    @SerializedName("followerCount")
    val followerCount: Int,
    @SerializedName("followingCount")
    val followingCount: Int,
    @SerializedName("memberId")
    val memberId:String,
    @SerializedName("nickname")
    val nickname:String,
    @SerializedName("postCount")
    val postCount:	Int,
    @SerializedName("profileImg")
    val profileImg:	String
)

data class ResponseLikePostList(
    @SerializedName("count")
    val count:Int,
    @SerializedName("data")
    val data:List<LikePostListData>
)

data class LikePostListData(
    @SerializedName("postId")
    val postId: Int,
    @SerializedName("thumbnailImage")
    val thumbnailImage: String
)

data class ResponseBookmarkPostList(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val data: List<BookmarkPostListData>
)

data class BookmarkPostListData(
    @SerializedName("postId")
    val postId:Int,
    @SerializedName("thumbnailImage")
    val thumbnailImage: String
)