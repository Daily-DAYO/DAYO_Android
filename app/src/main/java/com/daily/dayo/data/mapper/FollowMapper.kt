package com.daily.dayo.data.mapper

import com.daily.dayo.data.datasource.remote.follow.MyFollowerDto
import com.daily.dayo.domain.model.Follow

fun MyFollowerDto.toFollow() : Follow =
    Follow(
        isFollow = isFollow,
        memberId = memberId,
        nickname = nickname,
        profileImg = profileImg
    )