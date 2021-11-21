package com.daily.dayo.login

class LoginRepository(private val loginServiceImpl: LoginServiceImpl) {

    suspend fun requestLogin(request: LoginRequest): LoginResponse =
        loginServiceImpl.requestLogin(request)
}

