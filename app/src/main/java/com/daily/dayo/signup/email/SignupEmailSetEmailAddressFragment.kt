package com.daily.dayo.signup.email

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentSignupEmailSetEmailAddressBinding
import com.daily.dayo.login.LoginViewModel
import com.daily.dayo.util.ButtonActivation
import com.daily.dayo.util.HideKeyBoardUtil
import com.daily.dayo.util.SetTextInputLayout
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class SignupEmailSetEmailAddressFragment : Fragment() {
    private var binding by autoCleared<FragmentSignupEmailSetEmailAddressBinding>()
    private val loginViewModel by activityViewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupEmailSetEmailAddressBinding.inflate(inflater, container, false)
        setBackClickListener()
        setNextClickListener()
        initEditText()
        setLimitEditTextInputType()
        setTextEditorActionListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ ->
            HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetEmailAddressUserInput)
            changeEditTextTitle()
            SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetEmailAddressUserInput, binding.etSignupEmailSetEmailAddressUserInput, binding.etSignupEmailSetEmailAddressUserInput.text.isNullOrEmpty())
            verifyEmailAddress()
            true
        }
    }

    private fun setTextEditorActionListener() {
        binding.etSignupEmailSetEmailAddressUserInput.setOnEditorActionListener{ _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetEmailAddressUserInput)
                    changeEditTextTitle()
                    SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetEmailAddressUserInput, binding.etSignupEmailSetEmailAddressUserInput, binding.etSignupEmailSetEmailAddressUserInput.text.isNullOrEmpty())
                    verifyEmailAddress()
                    true
                } else -> false
            }
        }
    }

    private fun initEditText() {
        SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetEmailAddressUserInput, binding.etSignupEmailSetEmailAddressUserInput, binding.etSignupEmailSetEmailAddressUserInput.text.isNullOrEmpty())
        with(binding.etSignupEmailSetEmailAddressUserInput) {
            setOnFocusChangeListener { _, hasFocus -> // Title
                with(binding.layoutSignupEmailSetEmailAddressUserInput) {
                    if(hasFocus){
                        hint = getString(R.string.email)
                        SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetEmailAddressUserInput, binding.etSignupEmailSetEmailAddressUserInput, false)
                    } else {
                        hint = getString(R.string.email_address_example)
                        SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetEmailAddressUserInput, binding.etSignupEmailSetEmailAddressUserInput, true)
                    }
                }
            }
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {  }
                override fun afterTextChanged(s: Editable?) {
                    with(binding.layoutSignupEmailSetEmailAddressUserInput) {
                        isErrorEnabled = false
                        SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetEmailAddressUserInput, binding.etSignupEmailSetEmailAddressUserInput, false)
                    }
                }
            })
        }
    }

    private fun changeEditTextTitle() {
        with(binding.layoutSignupEmailSetEmailAddressUserInput) {
            if(binding.etSignupEmailSetEmailAddressUserInput.text.isNullOrEmpty()) {
                hint = getString(R.string.email_address_example)
            } else {
                hint = getString(R.string.email)
            }
        }
    }

    private fun verifyEmailAddress() {
        if(android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etSignupEmailSetEmailAddressUserInput.text.toString().trim()).matches()){ // 이메일 주소 양식 검사
            loginViewModel.requestCheckEmailDuplicate(binding.etSignupEmailSetEmailAddressUserInput.text.toString().trim())
            loginViewModel.isEmailDuplicate.observe(viewLifecycleOwner, Observer { isDuplicate ->
                if(isDuplicate) {
                    SetTextInputLayout.setEditTextErrorThemeWithIcon(requireContext(), binding.layoutSignupEmailSetEmailAddressUserInput, binding.etSignupEmailSetEmailAddressUserInput, null, true)
                    ButtonActivation.setSignupButtonActive(requireContext(), binding.btnSignupEmailSetEmailAddressNext)
                } else {
                    SetTextInputLayout.setEditTextErrorThemeWithIcon(requireContext(), binding.layoutSignupEmailSetEmailAddressUserInput, binding.etSignupEmailSetEmailAddressUserInput, getString(R.string.signup_email_set_email_address_message_duplicate_fail), false)
                    ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetEmailAddressNext)
                }
            })
        } else {
            SetTextInputLayout.setEditTextErrorThemeWithIcon(requireContext(), binding.layoutSignupEmailSetEmailAddressUserInput, binding.etSignupEmailSetEmailAddressUserInput, getString(R.string.signup_email_set_email_address_message_format_fail), false)
            ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetEmailAddressNext)
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
        binding.etSignupEmailSetEmailAddressUserInput.filters = arrayOf(filterInputCheck)
    }

    private fun setBackClickListener(){
        binding.btnSignupEmailSetEmailAddressBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnSignupEmailSetEmailAddressNext.setOnClickListener {
            Navigation.findNavController(it).navigate(SignupEmailSetEmailAddressFragmentDirections.actionSignupEmailSetEmailAddressFragmentToSignupEmailSetEmailAddressCertificateFragment(binding.etSignupEmailSetEmailAddressUserInput.text.toString().trim()))
        }
    }
}