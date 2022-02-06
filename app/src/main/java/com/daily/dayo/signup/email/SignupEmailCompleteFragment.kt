package com.daily.dayo.signup.email

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.daily.dayo.databinding.FragmentSignupEmailCompleteBinding
import com.daily.dayo.util.autoCleared

class SignupEmailCompleteFragment : Fragment() {
    private var binding by autoCleared<FragmentSignupEmailCompleteBinding>()
    private val args by navArgs<SignupEmailCompleteFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupEmailCompleteBinding.inflate(inflater, container, false)
        setCloseClickListener()
        setUserNickname()
        return binding.root
    }

    private fun setUserNickname() {
        binding.userNickname = args.nickname
    }

    private fun setCloseClickListener() {
        binding.btnSignupEmailCompleteClose.setOnClickListener {
            // TODO : 1. 가입한 정보를 통해 로그인
            //        2, MainActivity로 이동후 LoginActivity Destory
        }
    }
}