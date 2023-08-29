package daily.dayo.data.mapper

import daily.dayo.data.datasource.remote.member.MemberMyProfileResponse
import daily.dayo.data.datasource.remote.member.MemberOtherProfileResponse
import daily.dayo.domain.model.Profile

fun MemberMyProfileResponse.toProfile(currentUserMemberId: String): Profile =
    Profile(
        memberId = currentUserMemberId,
        email = email,
        nickname = nickname,
        profileImg = profileImg,
        postCount = postCount,
        followerCount = followerCount,
        followingCount = followingCount,
        follow = null
    )

fun MemberOtherProfileResponse.toProfile(): Profile =
    Profile(
        memberId = memberId,
        email = email ?: "",
        nickname = nickname,
        profileImg = profileImg,
        postCount = postCount,
        followerCount = followerCount,
        followingCount = followingCount,
        follow = follow
    )