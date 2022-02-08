package com.daily.dayo.login

import com.daily.dayo.SharedManager
import com.daily.dayo.User
import com.daily.dayo.login.model.SignupEmailRequest
import com.daily.dayo.login.model.SignupEmailResponse
import com.daily.dayo.network.login.LoginApiHelperImpl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class LoginRepository @Inject constructor(private val loginApiHelperImpl: LoginApiHelperImpl, private val sharedManager: SharedManager) {
    suspend fun requestLogin(request: LoginRequest): LoginResponse {
        val response = loginApiHelperImpl.requestLogin(request)
        sharedManager.saveCurrentUser(response)
        return response
    }

    suspend fun requestMemberInfo(): Response<MemberResponse> {
        val response = loginApiHelperImpl.requestMemberInfo()
        sharedManager.saveCurrentUser(response.body())
        return response
    }

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
        return loginApiHelperImpl.requestSignupEmail(email, nickname, password, uploadFile)
    }
}

