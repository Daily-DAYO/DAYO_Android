package com.daily.dayo.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.daily.dayo.MainActivity
import com.daily.dayo.databinding.ActivityLoginBinding
import com.daily.dayo.network.login.LoginServiceImpl
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private val loginViewModel : LoginViewModel by viewModels(){
        LoginViewModelFactory(LoginRepository(LoginServiceImpl()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setKakaoLoginButtonClickListener()
        setEmailLoginButtonClickListener()
    }

    fun setKakaoLoginButtonClickListener(){
        binding.btnLoginKakao.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "로그인 실패", error)
        }
        else if (token != null) {
            Log.i(TAG, "로그인 성공 ${token.accessToken}")

            loginViewModel.loginRequest(LoginRequest(token.accessToken))

            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            this.finish()
        }
    }

    fun setEmailLoginButtonClickListener(){
        binding.btnLoginEmail.setOnClickListener {
            //타 기능 구현시 테스트를 위해 임의로 email버튼 클릭시 메인액티비티로 가도록 설정해두었습니다.
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }
}