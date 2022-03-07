package com.daily.dayo.repository

import com.daily.dayo.login.model.*
import com.daily.dayo.network.login.LoginApiHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class LoginRepository @Inject constructor(private val loginApiHelper: LoginApiHelper) {
    suspend fun requestLoginKakao(request: LoginRequestKakao): Response<LoginResponse> = loginApiHelper.requestLoginKakao(request)
    suspend fun requestLoginEmail(request: LoginRequestEmail): Response<LoginResponse> = loginApiHelper.requestLoginEmail(request)
    suspend fun requestMemberInfo(): Response<MemberResponse> = loginApiHelper.requestMemberInfo()
    suspend fun requestRefreshToken(): Response<ResponseRefreshToken> = loginApiHelper.requestRefreshToken()

    suspend fun requestSignupEmail(
        email: String,
        nickname: String,
        password: String,
        profileImg: File?
    ): Response<SignupEmailResponse> {
        var uploadFile : MultipartBody.Part
        val fileNameDivideList: List<String> = profileImg.toString().split("/")
        var requestBodyFile : RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), profileImg!!)
        uploadFile = MultipartBody.Part.createFormData("profileImg",fileNameDivideList[fileNameDivideList.size - 1], requestBodyFile)
        return loginApiHelper.requestSignupEmail(email, nickname, password, uploadFile)
    }
    suspend fun requestCheckEmailDuplicate(email: String): Response<Void> = loginApiHelper.requestCheckEmailDuplicate(email)
}

