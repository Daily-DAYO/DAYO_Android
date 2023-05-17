package com.daily.dayo.presentation.fragment.account.signin

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.daily.dayo.DayoApplication
import com.daily.dayo.R
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.dialog.LoadingAlertDialog
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentLoginBinding
import com.daily.dayo.presentation.activity.MainActivity
import com.daily.dayo.presentation.adapter.OnBoardingPagerStateAdapter
import com.daily.dayo.presentation.fragment.onboarding.OnBoardingFirstFragment
import com.daily.dayo.presentation.fragment.onboarding.OnBoardingFourthFragment
import com.daily.dayo.presentation.fragment.onboarding.OnBoardingSecondFragment
import com.daily.dayo.presentation.fragment.onboarding.OnBoardingThirdFragment
import com.daily.dayo.presentation.viewmodel.AccountViewModel
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var binding by autoCleared<FragmentLoginBinding>()
    private val loginViewModel by activityViewModels<AccountViewModel>()
    private lateinit var indicators: Array<ImageView?>
    private lateinit var pagerAdapter: OnBoardingPagerStateAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var loadingAlertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())

        loginSuccess()
        setPolicy()
        setKakaoLoginButtonClickListener()
        setEmailLoginButtonClickListener()
        setViewPager()
        setViewPagerChangeEvent()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }

    private fun setKakaoLoginButtonClickListener() {
        binding.btnLoginKakao.setOnDebounceClickListener {
            LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
                UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                    if (error != null) {
                        Log.e("kakao login", "카카오톡으로 로그인 실패", error)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
                            return@loginWithKakaoTalk
                        }

                        UserApiClient.instance.loginWithKakaoAccount(
                            requireContext(),
                            callback = callback
                        )
                    } else if (token != null) {
                        Log.i("kakao login", "카카오톡으로 로그인 성공 ${token.accessToken}")
                        loginViewModel.requestLoginKakao(accessToken = token.accessToken)
                        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
            }
        }
    }

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            when (error) {
                is ClientError ->
                    when (error.reason) {
                        ClientErrorCause.Cancelled ->
                            Log.d("kakao login", "취소됨 (back button)", error)
                        ClientErrorCause.NotSupported ->
                            Log.e("kakao login", "지원되지 않음 (카톡 미설치)", error)
                        else ->
                            Log.e("kakao login", "기타 클라이언트 에러", error)
                    }
                is AuthError ->
                    when (error.reason) {
                        AuthErrorCause.AccessDenied ->
                            Log.d("kakao login", "취소됨 (동의 취소)", error)
                        AuthErrorCause.Misconfigured ->
                            Log.e(
                                "kakao login",
                                "개발자사이트 앱 설정에 키해시를 등록하세요. 현재 값: ${KakaoSdk.keyHash}",
                                error
                            )
                        else ->
                            Log.e("kakao login", "기타 인증 에러", error)
                    }
                else ->
                    Log.e("kakao login", "기타 에러 (네트워크 장애 등..)", error)
            }
        } else if (token != null) {
            Log.i("kakao login", "로그인 성공 ${token.accessToken}")
            loginViewModel.requestLoginKakao(accessToken = token.accessToken)
        }
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }

    private fun loginSuccess() {
        loginViewModel.loginSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                // 카카오 로그인 첫 시도 시 닉네임 설정 화면으로 이동
                if (DayoApplication.preferences.getCurrentUser().nickname == "") {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToSignupEmailSetProfileFragment(
                            DayoApplication.preferences.getCurrentUser().email.toString(),
                            null
                        )
                    )
                } else {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    requireActivity().finish()
                }
            } else if (isSuccess.getContentIfNotHandled() == false) {
                Log.e(ContentValues.TAG, "로그인 실패")
            }
        }
    }

    private fun setEmailLoginButtonClickListener() {
        binding.btnLoginEmail.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_loginEmailFragment)
        }
    }

    private fun setViewPager() {
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

    private fun setViewPagerChangeEvent() {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
    }

    private fun setupIndicators(count: Int) {
        indicators = arrayOfNulls(count)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
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
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_indicator_inactive_gray
                    )
                )
            }
        }
    }

    private fun setPolicy() {
        var span = SpannableString(getString(R.string.login_policy_guide_message))
        setPolicyMessage(span = span, type = "terms", text = getString(R.string.policy_terms))
        setPolicyMessage(span = span, type = "privacy", text = getString(R.string.policy_privacy))
        span.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.gray_4_C5CAD2
                )
            ),
            0,
            span.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvLoginUseGuideMessage.movementMethod = LinkMovementMethod.getInstance()
        binding.tvLoginUseGuideMessage.text = span
    }

    private fun setPolicyMessage(span: SpannableString, type: String, text: String) {
        span.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    with(findNavController()) {
                        if (currentDestination?.id == R.id.LoginFragment) {
                            currentDestination?.getAction(R.id.action_loginFragment_to_policyFragment)
                                ?.let {
                                    navigate(
                                        LoginFragmentDirections.actionLoginFragmentToPolicyFragment(
                                            informationType = type
                                        )
                                    )
                                }
                        }
                    }
                }
            },
            span.indexOf(text), span.indexOf(text) + text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        span.setSpan(
            StyleSpan(Typeface.BOLD),
            span.indexOf(text),
            span.indexOf(text) + text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        span.setSpan(
            UnderlineSpan(),
            span.indexOf(text),
            span.indexOf(text) + text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}