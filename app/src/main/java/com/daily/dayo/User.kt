package com.daily.dayo

data class User(
    var accessToken:String?=null,
    var refreshToken:String?=null,
    var email: String?=null,
    var memberId: String?=null,
    var nickname: String?=null,
    var profileImg: String?=null
)