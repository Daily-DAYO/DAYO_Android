package com.daily.dayo.signup.email

import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentSignupEmailSetEmailAddressCertificateBinding
import com.daily.dayo.util.ButtonActivation
import com.daily.dayo.util.HideKeyBoardUtil
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class SignupEmailSetEmailAddressCertificateFragment : Fragment() {
    private var binding by autoCleared<FragmentSignupEmailSetEmailAddressCertificateBinding>()
    private val args by navArgs<SignupEmailSetEmailAddressCertificateFragmentArgs>()
    private var currentCountDownTimer: CountDownTimer?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupEmailSetEmailAddressCertificateBinding.inflate(inflater, container, false)
        setBackClickListener()
        setNextClickListener()
        initEditText()
        setLimitEditTextInputType()
        setTextEditorActionListener()
        setEmailResendClickListener()
        certificateEmail()
        setUserPasswordEditText()

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ ->
            HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetEmailAddressCertificateUserInput)
            changeEditTextTitle()
            setEditTextTheme(binding.etSignupEmailSetEmailAddressCertificateUserInput, binding.etSignupEmailSetEmailAddressCertificateUserInput.text.isNullOrEmpty())
            true
        }
    }
    private fun setTextEditorActionListener() {
        binding.etSignupEmailSetEmailAddressCertificateUserInput.setOnEditorActionListener {  _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetEmailAddressCertificateUserInput)
                    changeEditTextTitle()
                    setEditTextTheme(binding.etSignupEmailSetEmailAddressCertificateUserInput, binding.etSignupEmailSetEmailAddressCertificateUserInput.text.isNullOrEmpty())
                    true
                } else -> false
            }
        }
    }

    private fun initEditText() {
        binding.etSignupEmailSetEmailAddressCertificateUserInput.setOnFocusChangeListener { _, hasFocus ->
            with(binding.layoutSignupEmailSetEmailAddressCertificateUserInput) {
                if(hasFocus){
                    hint = getString(R.string.email_address_certificate)
                    setEditTextTheme(binding.etSignupEmailSetEmailAddressCertificateUserInput, false)
                } else {
                    hint = getString(R.string.signup_email_set_email_address_certificate_title)
                    setEditTextTheme(binding.etSignupEmailSetEmailAddressCertificateUserInput, true)
                }
            }
        }
        currentCountDownTimer = countDownTimer
        currentCountDownTimer?.start()
    }

    private fun changeEditTextTitle() {
        with(binding.layoutSignupEmailSetEmailAddressCertificateUserInput) {
            if(binding.etSignupEmailSetEmailAddressCertificateUserInput.text.isNullOrEmpty()) {
                hint = getString(R.string.signup_email_set_email_address_certificate_title)
            } else {
                hint = getString(R.string.email_address_certificate)
            }
        }
    }

    private fun setEditTextTheme(editText: EditText, isEditTextEmpty: Boolean) {
        if(isEditTextEmpty) {
            binding.layoutSignupEmailSetEmailAddressCertificateUserInput.defaultHintTextColor = resources.getColorStateList(R.color.gray_5_D3D2D2, context?.theme)
            if(!binding.layoutSignupEmailSetEmailAddressCertificateUserInput.isErrorEnabled) {
                editText.backgroundTintList = resources.getColorStateList(R.color.gray_6_EDEDED, context?.theme)
            }
        } else {
            binding.layoutSignupEmailSetEmailAddressCertificateUserInput.defaultHintTextColor = resources.getColorStateList(R.color.gray_3_9C9C9C, context?.theme)
            if(!binding.layoutSignupEmailSetEmailAddressCertificateUserInput.isErrorEnabled) {
                editText.backgroundTintList = resources.getColorStateList(R.color.primary_green_23C882, context?.theme)
            }
        }
    }
    private fun setEditTextErrorTheme(errorMessage: String?, pass: Boolean) {
        with(binding.layoutSignupEmailSetEmailAddressCertificateUserInput) {
            error = errorMessage
            errorIconDrawable = null
            if(pass) {
                isErrorEnabled = false
                binding.etSignupEmailSetEmailAddressCertificateUserInput.backgroundTintList = resources.getColorStateList(R.color.primary_green_23C882, context?.theme)
            } else {
                isErrorEnabled = true
                binding.etSignupEmailSetEmailAddressCertificateUserInput.backgroundTintList = resources.getColorStateList(R.color.red_FF4545, context?.theme)
            }
        }
    }

    private fun setLimitEditTextInputType() {
        // InputFilter로 띄어쓰기 입력만 막고 나머지는 안내메시지로 띄워지도록 사용자에게 유도
        val filterInputCheck = InputFilter { source, start, end, dest, dstart, dend ->
            val ps = Pattern.compile("^[ ]+\$")
            if (ps.matcher(source).matches()) {
                return@InputFilter ""
            }
            null
        }
        binding.etSignupEmailSetEmailAddressCertificateUserInput.filters = arrayOf(filterInputCheck)
    }

    private val countDownTimer = object : CountDownTimer(1000 * 60 * 3, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            var remainMinutes = (millisUntilFinished / 1000) / 60
            var remainSeconds = millisUntilFinished / 1000 % 60
            binding.tvSignupEmailSetEmailAddressCertificateTimer.text = "${"%02d".format(remainMinutes)}:${"%02d".format(remainSeconds)}"
        }
        override fun onFinish() {
            setEditTextErrorTheme(getString(R.string.signup_email_set_email_address_certificate_alert_message_time_fail), false)
        }
    }

    private fun setEmailResendClickListener() {
        binding.tvSignupEmailSetEmailAddressCertificateResend.setOnClickListener {
            HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetEmailAddressCertificateUserInput) // 1. 키보드가 올라와 있는 경우 키보드가 내려감
            binding.etSignupEmailSetEmailAddressCertificateUserInput.setText("") // 2. 인증번호 입력창 초기화
            currentCountDownTimer?.start() // 3. 제한시간 초기화

            changeEditTextTitle()
            binding.layoutSignupEmailSetEmailAddressCertificateUserInput.isErrorEnabled = false
            setEditTextTheme(binding.etSignupEmailSetEmailAddressCertificateUserInput, true)

            Toast.makeText(requireContext(), R.string.signup_email_set_email_address_certificate_alert_message__resend, Toast.LENGTH_SHORT).show() // 4. 토스트 메시지 보여짐
        }
    }

    private fun certificateEmail() {
        /*
        binding.tvSignupEmailSetEmailAddressCertificateTitle.setOnClickListener {// TODO : 인증 체크 API 서버와 연동
            if(!binding.etSignupEmailSetEmailAddressCertificateUserInput.text.isNullOrEmpty()) {
                ButtonActivation.setSignupButtonActive(requireContext(), binding.btnSignupEmailSetEmailAddressCertificateNext)
                setEditTextErrorTheme(null, true)
                currentCountDownTimer?.cancel()
                currentCountDownTimer = null
            } else {
                ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetEmailAddressCertificateNext)
                setEditTextErrorTheme(getString(R.string.signup_email_set_email_address_certificate_alert_message_match_fail), false)
            }
        }
        */
    }

    private fun setUserPasswordEditText() {
        binding.email = args.email
    }

    private fun setBackClickListener(){
        binding.btnSignupEmailSetEmailAddressCertificateBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnSignupEmailSetEmailAddressCertificateNext.setOnClickListener {
            Navigation.findNavController(it).navigate(SignupEmailSetEmailAddressCertificateFragmentDirections.actionSignupEmailSetEmailAddressCertificateFragmentToSignupEmailSetPasswordFragment(args.email))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
    }
}