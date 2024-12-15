package daily.dayo.data.mapper

import daily.dayo.data.datasource.remote.follow.CreateFollowResponse
import daily.dayo.data.datasource.remote.follow.CreateFollowUpResponse
import daily.dayo.data.datasource.remote.follow.ListAllFollowerResponse
import daily.dayo.data.datasource.remote.follow.ListAllFollowingResponse
import daily.dayo.data.datasource.remote.follow.MyFollowerDto
import daily.dayo.domain.model.Follow
import daily.dayo.domain.model.FollowCreateResponse
import daily.dayo.domain.model.FollowUpCreateResponse
import daily.dayo.domain.model.Follower
import daily.dayo.domain.model.Following

fun CreateFollowResponse.toFollowCreateResponse(): FollowCreateResponse =
    FollowCreateResponse(followerId = followerId, isAccept = isAccept, memberId = memberId)

fun CreateFollowUpResponse.toFollowUpCreateResponse(): FollowUpCreateResponse =
    FollowUpCreateResponse(followId = followId, isAccept = isAccept, memberId = memberId)

fun ListAllFollowingResponse.toFollowings(): Following =
    Following(count = count, data = data.map { it.toMyFollower() })

fun ListAllFollowerResponse.toFollowers(): Follower =
    Follower(count = count, data = data.map { it.toMyFollower() })

fun MyFollowerDto.toMyFollower(): Follow =
    Follow(
        isFollow = isFollow,
        memberId = memberId,
        nickname = nickname,
        profileImg = profileImg
    )