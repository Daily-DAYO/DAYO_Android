package com.daily.dayo.presentation.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.daily.dayo.DayoApplication
import com.daily.dayo.databinding.ActivityLoginBinding
import com.daily.dayo.presentation.viewmodel.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<AccountViewModel>()
    private var isReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeNetworkException()
        observeApiException()
        autoLogin()
        loginSuccess()
        setSplash()
    }

    private fun setFCM() {
        GlobalScope.launch(Dispatchers.IO) {
            loginViewModel.getCurrentFcmToken().let { deviceToken ->
                loginViewModel.requestDeviceToken(deviceToken = deviceToken)
            }
        }
    }

    private fun autoLogin() {
        // refresh token 존재 여부 확인
        if (DayoApplication.preferences.getCurrentUser().refreshToken != null) {
            // refresh token 존재 시 유효성 확인
            loginViewModel.requestRefreshToken()
        }
    }

    private fun loginSuccess() {
        loginViewModel.loginSuccess.observe(this) { isSuccess ->
            if (isSuccess.peekContent()) {
                setFCM()
                if (DayoApplication.preferences.getCurrentUser().nickname == "") {
                    isReady = true
                } else {
                    // 로그인 성공 시 메인 화면으로 이동
                    isReady = false // 로그인이 성공한 경우에는 Splash 화면을 없애지 않고 바로 넘어가게 하기 위해 false 설정
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    this.finish()
                }
            } else {
                Log.e(ContentValues.TAG, "자동 로그인 실패")
                isReady = true // 로그인 실패할 경우, Splash 화면을 지우고 Login Activity 내용물이 보이도록 설정
            }
        }
    }

    private fun setSplash() {
        binding.root.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (isReady) {
                        binding.root.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else
                        false
                }
            }
        )
    }

    private fun observeNetworkException() {
        loginViewModel.isErrorExceptionOccurred.observe(this) { isOccurred ->
            if (isOccurred.getContentIfNotHandled() == true) {
                Toast.makeText(this, "네트워크 연결 상태가 불안정합니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeApiException() {
        loginViewModel.isApiErrorExceptionOccurred.observe(this) { isOccurred ->
            if (isOccurred.getContentIfNotHandled() == true)
                Toast.makeText(this, "서버와의 연결상태가 좋지 않습니다", Toast.LENGTH_SHORT).show()
        }
    }
}