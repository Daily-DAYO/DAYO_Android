package daily.dayo.data.mapper

import daily.dayo.data.datasource.remote.follow.CreateFollowResponse
import daily.dayo.data.datasource.remote.follow.CreateFollowUpResponse
import daily.dayo.data.datasource.remote.follow.ListAllFollowerResponse
import daily.dayo.data.datasource.remote.follow.ListAllFollowingResponse
import daily.dayo.data.datasource.remote.follow.MyFollowerDto
import daily.dayo.domain.model.FollowCreateResponse
import daily.dayo.domain.model.FollowUpCreateResponse
import daily.dayo.domain.model.Followers
import daily.dayo.domain.model.Followings
import daily.dayo.domain.model.MyFollower

fun CreateFollowResponse.toFollowCreateResponse(): FollowCreateResponse =
    FollowCreateResponse(followerId = followerId, isAccept = isAccept, memberId = memberId)

fun CreateFollowUpResponse.toFollowUpCreateResponse(): FollowUpCreateResponse =
    FollowUpCreateResponse(followId = followId, isAccept = isAccept, memberId = memberId)

fun ListAllFollowingResponse.toFollowings(): Followings =
    Followings(count = count, data = data.map { it.toMyFollower() })

fun ListAllFollowerResponse.toFollowers(): Followers =
    Followers(count = count, data = data.map { it.toMyFollower() })

fun MyFollowerDto.toMyFollower(): MyFollower =
    MyFollower(
        isFollow = isFollow,
        memberId = memberId,
        nickname = nickname,
        profileImg = profileImg
    )