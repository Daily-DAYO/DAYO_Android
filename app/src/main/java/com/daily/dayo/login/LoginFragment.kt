package com.daily.dayo.login

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.daily.dayo.MainActivity
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentLoginBinding
import com.daily.dayo.login.model.LoginRequestKakao
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

        loginSuccess()
        setKakaoLoginButtonClickListener()
        setEmailLoginButtonClickListener()
        setSignupEmailButtonClickListener()
        return binding.root
    }

    private fun setKakaoLoginButtonClickListener(){
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
            Log.e(ContentValues.TAG, "카카오 로그인 실패", error)
        }
        else if (token != null) {
            Log.i(ContentValues.TAG, "카카오 로그인 성공 ${token.accessToken}")
            loginViewModel.requestLoginKakao(LoginRequestKakao(token.accessToken))
        }
    }

    private fun loginSuccess(){
        loginViewModel.loginSuccess.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                requireActivity().finish()
            } else if(isSuccess.getContentIfNotHandled() == false) {
                Log.e(ContentValues.TAG, "로그인 실패")
            }
        })
    }

    private fun setEmailLoginButtonClickListener(){
        binding.btnLoginEmail.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_loginEmailFragment)
        }
    }

    private fun setSignupEmailButtonClickListener() {
        binding.tvLoginSignupEmailGuideMessage.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupEmailSetEmailAddressFragment) }
    }

}