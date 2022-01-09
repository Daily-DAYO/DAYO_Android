package com.daily.dayo.login

import com.daily.dayo.SharedManager
import com.daily.dayo.User
import com.daily.dayo.network.login.LoginApiHelperImpl
import retrofit2.Response
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

}

