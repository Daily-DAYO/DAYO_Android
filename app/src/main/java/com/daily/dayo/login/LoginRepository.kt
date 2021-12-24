package com.daily.dayo.login

import com.daily.dayo.DayoApplication
import com.daily.dayo.SharedManager
import com.daily.dayo.User
import com.daily.dayo.network.login.LoginServiceImpl

class LoginRepository(private val loginServiceImpl: LoginServiceImpl) {
    private val sharedManager : SharedManager by lazy { SharedManager(DayoApplication.applicationContext()) }
    suspend fun requestLogin(request: LoginRequest): LoginResponse {
        val response = loginServiceImpl.requestLogin(request)
        val id = response.id
        sharedManager.saveCurrentUser(User(id))

        return response
    }
}

