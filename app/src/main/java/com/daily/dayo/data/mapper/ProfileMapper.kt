package com.daily.dayo.data.mapper

import com.daily.dayo.data.datasource.remote.member.MemberOtherProfileResponse
import com.daily.dayo.domain.model.Profile

fun MemberOtherProfileResponse.toProfile() : Profile =
    Profile(
        memberId = memberId,
        email = email?:"null",
        nickname = nickname,
        profileImg = profileImg,
        postCount = postCount,
        followerCount = followerCount,
        followingCount = followingCount,
        follow = follow
    )