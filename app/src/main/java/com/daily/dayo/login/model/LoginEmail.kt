package com.daily.dayo.login.model

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.http.Part

data class SignupEmailRequest (
    @SerializedName("email")
    val email : String,
    @SerializedName("nickname")
    val nickname : String,
    @SerializedName("password")
    val password : String,
    val profileImg : MultipartBody.Part
)

data class SignupEmailResponse (
    @SerializedName("memberId")
    val memberId : String
)