package com.daily.dayo.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.daily.dayo.MainActivity
import com.daily.dayo.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setKakaoLoginButtonClickListener()
        setEmailLoginButtonClickListener()
    }

    fun setKakaoLoginButtonClickListener(){
        binding.btnLoginKakao.setOnClickListener {

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