package com.daily.dayo.presentation.fragment.account.signup

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.R
import com.daily.dayo.common.ButtonActivation
import com.daily.dayo.common.HideKeyBoardUtil
import com.daily.dayo.common.SetTextInputLayout
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentSignupEmailSetEmailAddressCertificateBinding
import com.daily.dayo.presentation.viewmodel.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class SignupEmailSetEmailAddressCertificateFragment : Fragment() {
    private var binding by autoCleared<FragmentSignupEmailSetEmailAddressCertificateBinding>()
    private val loginViewModel by activityViewModels<AccountViewModel>()
    private val args by navArgs<SignupEmailSetEmailAddressCertificateFragmentArgs>()
    private var currentCountDownTimer: CountDownTimer?= null
    private var isCountDownDone: Boolean = false

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
        setUserAddressEditText()

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ ->
            HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetEmailAddressCertificateUserInput)
            changeEditTextTitle()
            SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetEmailAddressCertificateUserInput, binding.etSignupEmailSetEmailAddressCertificateUserInput, binding.etSignupEmailSetEmailAddressCertificateUserInput.text.isNullOrEmpty())
            true
        }
    }
    private fun setTextEditorActionListener() {
        binding.etSignupEmailSetEmailAddressCertificateUserInput.setOnEditorActionListener {  _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetEmailAddressCertificateUserInput)
                    changeEditTextTitle()
                    SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetEmailAddressCertificateUserInput, binding.etSignupEmailSetEmailAddressCertificateUserInput, binding.etSignupEmailSetEmailAddressCertificateUserInput.text.isNullOrEmpty())
                    true
                } else -> false
            }
        }
    }

    private fun initEditText() {
        SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetEmailAddressCertificateUserInput, binding.etSignupEmailSetEmailAddressCertificateUserInput, binding.etSignupEmailSetEmailAddressCertificateUserInput.text.isNullOrEmpty())
        binding.etSignupEmailSetEmailAddressCertificateUserInput.setOnFocusChangeListener { _, hasFocus ->
            with(binding.layoutSignupEmailSetEmailAddressCertificateUserInput) {
                if(hasFocus){
                    hint = getString(R.string.email_address_certification)
                    SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetEmailAddressCertificateUserInput, binding.etSignupEmailSetEmailAddressCertificateUserInput, false)
                } else {
                    hint = getString(R.string.email_address_certificate_title)
                    SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetEmailAddressCertificateUserInput, binding.etSignupEmailSetEmailAddressCertificateUserInput, true)
                }
            }
        }
        currentCountDownTimer = countDownTimer
        currentCountDownTimer?.start()
    }

    private fun changeEditTextTitle() {
        with(binding.layoutSignupEmailSetEmailAddressCertificateUserInput) {
            if(binding.etSignupEmailSetEmailAddressCertificateUserInput.text.isNullOrEmpty()) {
                hint = getString(R.string.email_address_certificate_title)
            } else {
                hint = getString(R.string.email_address_certification)
            }
        }
    }

    private fun certificateEmail() {
        // ??????????????? ?????? ????????? ?????? ??????
        loginViewModel.requestCertificateEmail(args.email)

        binding.etSignupEmailSetEmailAddressCertificateUserInput.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                if(!binding.etSignupEmailSetEmailAddressCertificateUserInput.text.isNullOrEmpty() && !isCountDownDone) {
                    ButtonActivation.setSignupButtonActive(requireContext(), binding.btnSignupEmailSetEmailAddressCertificateNext)
                } else {
                    ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetEmailAddressCertificateNext)
                }
            }
        })
        binding.btnSignupEmailSetEmailAddressCertificateNext.setOnClickListener {
            if(binding.etSignupEmailSetEmailAddressCertificateUserInput.text.toString() == loginViewModel.certificateEmailAuthCode.value) {
                ButtonActivation.setSignupButtonActive(requireContext(), binding.btnSignupEmailSetEmailAddressCertificateNext)
                SetTextInputLayout.setEditTextErrorTheme(requireContext(), binding.layoutSignupEmailSetEmailAddressCertificateUserInput, binding.etSignupEmailSetEmailAddressCertificateUserInput, null, true)
                currentCountDownTimer?.cancel()
                currentCountDownTimer = null
                binding.tvSignupEmailSetEmailAddressCertificateResend.isEnabled = false
                Navigation.findNavController(it).navigate(SignupEmailSetEmailAddressCertificateFragmentDirections.actionSignupEmailSetEmailAddressCertificateFragmentToSignupEmailSetPasswordFragment(args.email))
            } else {
                ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetEmailAddressCertificateNext)
                SetTextInputLayout.setEditTextErrorTheme(requireContext(), binding.layoutSignupEmailSetEmailAddressCertificateUserInput, binding.etSignupEmailSetEmailAddressCertificateUserInput, getString(R.string.email_address_certificate_alert_message_match_fail), false)
                Toast.makeText(requireContext(), "?????? ?????? ???????????????.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setLimitEditTextInputType() {
        // InputFilter??? ???????????? ????????? ?????? ???????????? ?????????????????? ??????????????? ??????????????? ??????
        val filterInputCheck = InputFilter { source, start, end, dest, dstart, dend ->
            val ps = Pattern.compile("^[ ]+\$")
            if (ps.matcher(source).matches()) {
                return@InputFilter ""
            }
            null
        }
        binding.etSignupEmailSetEmailAddressCertificateUserInput.filters = arrayOf(filterInputCheck)
    }

    private val countDownTimer = object : CountDownTimer((1000 * 60 * 3).toLong(), 1000) {
        override fun onTick(millisUntilFinished: Long) {
            isCountDownDone = false
            var remainMinutes = (millisUntilFinished / 1000) / 60
            var remainSeconds = millisUntilFinished / 1000 % 60
            binding.tvSignupEmailSetEmailAddressCertificateTimer.text = "${"%02d".format(remainMinutes)}:${"%02d".format(remainSeconds)}"
        }
        override fun onFinish() {
            isCountDownDone = true
            ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetEmailAddressCertificateNext)
            SetTextInputLayout.setEditTextErrorTheme(requireContext(), binding.layoutSignupEmailSetEmailAddressCertificateUserInput, binding.etSignupEmailSetEmailAddressCertificateUserInput, getString(R.string.email_address_certificate_alert_message_time_fail),false)
        }
    }

    private fun setEmailResendClickListener() {
        binding.tvSignupEmailSetEmailAddressCertificateResend.setOnClickListener {
            HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetEmailAddressCertificateUserInput) // 1. ???????????? ????????? ?????? ?????? ???????????? ?????????
            binding.etSignupEmailSetEmailAddressCertificateUserInput.setText("") // 2. ???????????? ????????? ?????????
            currentCountDownTimer?.start() // 3. ???????????? ?????????

            changeEditTextTitle()
            binding.layoutSignupEmailSetEmailAddressCertificateUserInput.isErrorEnabled = false
            SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetEmailAddressCertificateUserInput, binding.etSignupEmailSetEmailAddressCertificateUserInput, true)

            loginViewModel.requestCertificateEmail(args.email)
            Toast.makeText(requireContext(), R.string.email_address_certificate_alert_message_resend, Toast.LENGTH_SHORT).show() // 4. ????????? ????????? ?????????
        }
    }

    private fun setUserAddressEditText() {
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