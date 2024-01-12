package daily.dayo.presentation.fragment.account.signin

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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.presentation.R
import daily.dayo.presentation.activity.MainActivity
import daily.dayo.presentation.adapter.OnBoardingPagerStateAdapter
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.dialog.LoadingAlertDialog
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentLoginBinding
import daily.dayo.presentation.fragment.onboarding.OnBoardingFirstFragment
import daily.dayo.presentation.fragment.onboarding.OnBoardingFourthFragment
import daily.dayo.presentation.fragment.onboarding.OnBoardingSecondFragment
import daily.dayo.presentation.fragment.onboarding.OnBoardingThirdFragment
import daily.dayo.presentation.theme.Gray1_313131
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.PrimaryGreen_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.b2
import daily.dayo.presentation.theme.b5
import daily.dayo.presentation.theme.caption3
import daily.dayo.presentation.view.FilledRoundedCornerButton
import daily.dayo.presentation.view.TextButton
import daily.dayo.presentation.viewmodel.AccountViewModel

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var binding by autoCleared<FragmentLoginBinding> {
        onDestroyBindingView()
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }
    private val loginViewModel by activityViewModels<AccountViewModel>()
    private lateinit var indicators: Array<ImageView?>
    private var pagerAdapter: OnBoardingPagerStateAdapter? = null
    private lateinit var loadingAlertDialog: AlertDialog
    private val pageChangeCallBack = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            setCurrentIndicator(position)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())

        loginSuccess()
        setPolicy()
        setKakaoLoginButtonClickListener()
        setEmailLoginButtonClickListener()
        setViewPager()
        setViewPagerChangeEvent()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    LoginScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun LoginScreen() {
        Column(
            modifier = Modifier
                .background(White_FFFFFF)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.ic_dayo_logo_onbording),
                contentDescription = stringResource(id = R.string.app_logo),
                modifier = Modifier.padding(start = 18.dp, top = 8.dp)
            )

            // Onboarding Pager
            Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
                val pagerState = rememberPagerState(pageCount = { 4 })
                HorizontalPager(state = pagerState) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OnBoardingItem(page = it)
                    }
                }

                // Indicator
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color = if (pagerState.currentPage == iteration) Gray1_313131 else Gray6_F0F1F3
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.5.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(6.dp)
                        )
                    }
                }
            }

            Column {
                // Login
                FilledRoundedCornerButton(
                    onClick = {},
                    label = stringResource(id = R.string.login_select_method_kakao),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .padding(horizontal = 18.dp),
                    color = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBE44D), contentColor = Color(0xFF3B1F1E)),
                    icon = { Icon(painter = painterResource(id = R.drawable.ic_kakao), "Kakao") },
                    textStyle = MaterialTheme.typography.b5
                )

                Spacer(modifier = Modifier.height(12.dp))

                FilledRoundedCornerButton(
                    onClick = {},
                    label = stringResource(id = R.string.login_select_method_email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .padding(horizontal = 18.dp),
                    color = ButtonDefaults.buttonColors(containerColor = Gray1_313131, contentColor = White_FFFFFF),
                    icon = { Icon(Icons.Filled.Email, "Email") },
                    textStyle = MaterialTheme.typography.b5
                )

                // Policy Text
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 19.dp, vertical = 19.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "가입 시 DAYO의 ", style = MaterialTheme.typography.caption3.copy(Gray4_C5CAD2))
                    TextButton(onClick = {}, text = "이용약관", textStyle = MaterialTheme.typography.caption3.copy(Gray4_C5CAD2), underline = true)
                    Text(text = " 및 ", style = MaterialTheme.typography.caption3.copy(Gray4_C5CAD2))
                    TextButton(onClick = {}, text = "개인정보", textStyle = MaterialTheme.typography.caption3.copy(Gray4_C5CAD2), underline = true)
                    Text(text = " 취급방침에 동의하게 됩니다.", style = MaterialTheme.typography.caption3.copy(Gray4_C5CAD2))
                }
            }
        }
    }

    @Composable
    private fun OnBoardingItem(
        page: Int
    ) {
        // Show Lottie
        val onboardingLottieSpec: LottieCompositionSpec? = when (page) {
            0 -> LottieCompositionSpec.RawRes(R.raw.onboarding_first)
            1 -> LottieCompositionSpec.RawRes(R.raw.onboarding_second)
            2 -> LottieCompositionSpec.RawRes(R.raw.onboarding_third)
            else -> null
        }

        if (onboardingLottieSpec != null) {
            val onboardingLottie by rememberLottieComposition(
                onboardingLottieSpec
            )

            // TODO FIX : 특정 해상도에서 애니메이션이 깨지는 현상 수정 필요
            LottieAnimation(
                composition = onboardingLottie,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .size(240.dp, 200.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Show Text
        val annotatedString = when (page) {
            0 -> buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("인기가 많은 다꾸")
                }
                append("들을\n 구경할 수 있어요")
            }

            1 -> buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("내 취향의 다꾸러")
                }
                append("들을\n 팔로우하고 모아볼 수 있어요")
            }

            2 -> buildAnnotatedString {
                append("내 다꾸들을 ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("폴더를 만들어\n 분류")
                }
                append("할 수 있어요")
            }

            3 -> buildAnnotatedString {
                append("내가 열심히 꾸민 다이어리,\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = PrimaryGreen_23C882)) {
                    append("DAYO")
                }
                append("에 공유하러 가볼까요?")
            }

            else -> buildAnnotatedString { }
        }

        Text(
            text = annotatedString,
            modifier = if (page == 3) Modifier.padding(vertical = 116.dp) else Modifier,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.b2
        )
    }

    private fun onDestroyBindingView() {
        pagerAdapter = null
        with(binding.vpLoginOnboarding) {
            unregisterOnPageChangeCallback(pageChangeCallBack)
            adapter = null
        }
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
                if (loginViewModel.getCurrentUserInfo().nickname == "") {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToSignupEmailSetProfileFragment(
                            loginViewModel.getCurrentUserInfo().email.toString(),
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
        binding.vpLoginOnboarding.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        pagerAdapter =
            OnBoardingPagerStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        pagerAdapter?.addFragment(OnBoardingFirstFragment())
        pagerAdapter?.addFragment(OnBoardingSecondFragment())
        pagerAdapter?.addFragment(OnBoardingThirdFragment())
        pagerAdapter?.addFragment(OnBoardingFourthFragment())
        binding.vpLoginOnboarding.adapter = pagerAdapter
        setupIndicators(pagerAdapter?.itemCount ?: 0)
    }

    private fun setViewPagerChangeEvent() {
        binding.vpLoginOnboarding.registerOnPageChangeCallback(pageChangeCallBack)
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
        with(binding.viewLoginOnboardingIndicators) {
            for (i in 0 until this.childCount) {
                val imageView = this.getChildAt(i) as ImageView
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

    @Composable
    @Preview(showBackground = true)
    private fun PreviewLoginScreen() {
        MaterialTheme {
            LoginScreen()
        }
    }
}
