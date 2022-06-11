package com.daily.dayo.presentation.fragment.account.signin

import android.content.ContentValues
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.daily.dayo.R
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentLoginBinding
import com.daily.dayo.presentation.adapter.OnBoardingPagerStateAdapter
import com.daily.dayo.presentation.activity.MainActivity
import com.daily.dayo.presentation.fragment.onboarding.OnBoardingFirstFragment
import com.daily.dayo.presentation.fragment.onboarding.OnBoardingFourthFragment
import com.daily.dayo.presentation.fragment.onboarding.OnBoardingSecondFragment
import com.daily.dayo.presentation.fragment.onboarding.OnBoardingThirdFragment
import com.daily.dayo.presentation.viewmodel.AccountViewModel
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var binding by autoCleared<FragmentLoginBinding>()
    private val loginViewModel by activityViewModels<AccountViewModel>()
    private lateinit var indicators : Array<ImageView?>
    private lateinit var pagerAdapter : OnBoardingPagerStateAdapter
    private lateinit var viewPager : ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        loginSuccess()
        setTextUnderline()
        setKakaoLoginButtonClickListener()
        setEmailLoginButtonClickListener()
        setViewPager()
        setViewPagerChangeEvent()
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
            loginViewModel.requestLoginKakao(accessToken = token.accessToken)
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

    private fun setTextUnderline(){
        binding.tvLoginUseGuideMessage2TermsOfService.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.tvLoginUseGuideMessage4Privacy.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }

    private fun setViewPager(){
        viewPager = binding.vpLoginOnboarding
        viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        pagerAdapter = OnBoardingPagerStateAdapter(requireActivity())
        pagerAdapter.addFragment(OnBoardingFirstFragment())
        pagerAdapter.addFragment(OnBoardingSecondFragment())
        pagerAdapter.addFragment(OnBoardingThirdFragment())
        pagerAdapter.addFragment(OnBoardingFourthFragment())
        viewPager.adapter = pagerAdapter
        setupIndicators(pagerAdapter.itemCount)
    }

    private fun setViewPagerChangeEvent(){
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
    }

    private fun setupIndicators(count: Int) {
        indicators = arrayOfNulls(count)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(16, 8, 16, 8)
        for (i in indicators.indices) {
            indicators[i] = ImageView(requireContext())
            indicators[i]!!.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_indicator_inactive_gray)
            )
            indicators[i]!!.layoutParams = params
            binding.viewLoginOnboardingIndicators.addView(indicators[i])
        }
        setCurrentIndicator(0)
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount: Int = binding.viewLoginOnboardingIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = binding.viewLoginOnboardingIndicators.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_indicator_active)
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_indicator_inactive_gray)
                )
            }
        }
    }
}