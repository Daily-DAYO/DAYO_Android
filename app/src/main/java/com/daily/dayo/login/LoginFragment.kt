package com.daily.dayo.login

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daily.dayo.MainActivity
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentLoginBinding
import com.daily.dayo.util.autoCleared
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var binding by autoCleared<FragmentLoginBinding>()
    private val loginViewModel by activityViewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        setKakaoLoginButtonClickListener()
        setEmailLoginButtonClickListener()
        return binding.root
    }

    fun setKakaoLoginButtonClickListener(){
        binding.btnLoginKakao.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
                UserApiClient.instance.loginWithKakaoTalk(requireContext(), callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
            }
        }
    }

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(ContentValues.TAG, "로그인 실패", error)
        }
        else if (token != null) {
            Log.i(ContentValues.TAG, "로그인 성공 ${token.accessToken}")
            loginViewModel.requestLogin(LoginRequest(token.accessToken))
            loginViewModel.requestMemberInfo()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            requireActivity().finish()
        }
    }

    fun setEmailLoginButtonClickListener(){
        binding.btnLoginEmail.setOnClickListener {
            /*
            //타 기능 구현시 테스트를 위해 임의로 email버튼 클릭시 메인액티비티로 가도록 설정해두었습니다.
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
             */
            findNavController().navigate(R.id.action_loginFragment_to_signupEmailSetEmailAddressFragment)
        }
    }
}