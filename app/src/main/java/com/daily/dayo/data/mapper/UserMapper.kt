package com.daily.dayo.data.mapper

import com.daily.dayo.data.datasource.remote.member.BlockDto
import com.daily.dayo.data.datasource.remote.member.MemberBlockResponse
import com.daily.dayo.data.datasource.remote.member.MemberOAuthResponse
import com.daily.dayo.data.datasource.remote.member.MemberSignInResponse
import com.daily.dayo.domain.model.UserBlocked
import com.daily.dayo.domain.model.UserTokens
import com.daily.dayo.domain.model.UsersBlocked

fun MemberOAuthResponse.toUserTokenResponse(): UserTokens =
    UserTokens(accessToken = accessToken, refreshToken = refreshToken)

fun MemberSignInResponse.toUserTokenResponse(): UserTokens =
    UserTokens(accessToken = accessToken, refreshToken = refreshToken)

fun BlockDto.toUserBlocked(): UserBlocked =
    UserBlocked(memberId = memberId, profileImg = profileImg, nickname = nickname)
fun MemberBlockResponse.toUsersBlocked(): UsersBlocked =
    UsersBlocked(count = count, data = data.map { it.toUserBlocked() })