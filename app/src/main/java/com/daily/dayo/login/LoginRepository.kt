package com.daily.dayo.login

import com.daily.dayo.SharedManager
import com.daily.dayo.User
import com.daily.dayo.network.login.LoginServiceImpl
import javax.inject.Inject

class LoginRepository @Inject constructor(private val loginServiceImpl: LoginServiceImpl, private val sharedManager: SharedManager) {
    suspend fun requestLogin(request: LoginRequest): LoginResponse {
        val response = loginServiceImpl.requestLogin(request)
        val accessToken = response.accessToken
        val refreshToken = response.refreshToken
        sharedManager.saveCurrentUser(User("null", accessToken, refreshToken))

        return response
    }
}

