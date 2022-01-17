package com.daily.dayo.signup.email

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentSignupEmailSetEmailAddressBinding
import com.daily.dayo.util.ButtonActivation
import com.daily.dayo.util.HideKeyBoardUtil
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class SignupEmailSetEmailAddressFragment : Fragment() {
    private var binding by autoCleared<FragmentSignupEmailSetEmailAddressBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupEmailSetEmailAddressBinding.inflate(inflater, container, false)
        setBackClickListener()
        setNextClickListener()
        setLimitEditTextInputType()
        setEditTextErrorMessage()
        setTextEditorActionListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ ->
            val inputMethodManager : InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
            setEditTextTitle()
            setVerifyEmailAddress()
            true
        }
    }

    private fun setTextEditorActionListener() {
        binding.etSignupEmailSetEmailAddressUserInput.setOnEditorActionListener{ _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetEmailAddressUserInput)
                    setEditTextTitle()
                    setVerifyEmailAddress()
                    true
                } else -> false
            }
        }
        binding.etSignupEmailSetEmailAddressUserInput.setOnFocusChangeListener { _, hasFocus ->
            with(binding.layoutSignupEmailSetEmailAddressUserInput) {
                if(hasFocus){
                    hint = getString(R.string.email)
                } else {
                    hint = getString(R.string.email_address)
                }
            }
        }
    }

    private fun setEditTextTitle() {
        with(binding.layoutSignupEmailSetEmailAddressUserInput) {
            if(binding.etSignupEmailSetEmailAddressUserInput.text.isNullOrEmpty() && !binding.etSignupEmailSetEmailAddressUserInput.isFocused) {
                hint = getString(R.string.email_address)
            } else {
                hint = getString(R.string.email)
            }
        }
    }

    private fun setVerifyEmailAddress() {
        with(binding.layoutSignupEmailSetEmailAddressUserInput) {
            isErrorEnabled = true
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etSignupEmailSetEmailAddressUserInput.text.toString().trim()).matches()){ // 이메일 주소 양식 검사
                if(true) { // TODO : 서버와 통신하여 이메일 중복 체크 필요
                    ButtonActivation.setSignupButtonActive(requireContext(), binding.btnSignupEmailSetEmailAddressNext)
                    isErrorEnabled = false
                } else {
                    ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetEmailAddressNext)
                    error = getString(R.string.signup_email_set_email_address_message_duplicate_fail)
                }
            } else {
                ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetEmailAddressNext)
                error = getString(R.string.signup_email_set_email_address_message_format_fail)
            }

            if(isErrorEnabled){
                boxStrokeColor = resources.getColor(R.color.red_FF4545, context?.theme)
                endIconDrawable = null
            } else {
                boxStrokeColor = resources.getColor(R.color.primary_green_23C882, context?.theme)
                endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_ok_sign)
            }
        }
    }

    private fun setEditTextErrorMessage() {
        binding.etSignupEmailSetEmailAddressUserInput.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {

            }
        })
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
            if(!binding.etSignupEmailSetEmailAddressUserInput.text.isNullOrBlank()) {
                Navigation.findNavController(it).navigate(SignupEmailSetEmailAddressFragmentDirections.actionSignupEmailSetEmailAddressFragmentToSignupEmailSetPasswordFragment(binding.etSignupEmailSetEmailAddressUserInput.text.toString().trim()))
            }
        }
    }
}