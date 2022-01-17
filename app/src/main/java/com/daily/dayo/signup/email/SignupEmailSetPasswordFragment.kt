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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentSignupEmailSetPasswordBinding
import com.daily.dayo.util.ButtonActivation
import com.daily.dayo.util.HideKeyBoardUtil
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class SignupEmailSetPasswordFragment : Fragment() {
    private var binding by autoCleared<FragmentSignupEmailSetPasswordBinding>()
    private val args by navArgs<SignupEmailSetPasswordFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupEmailSetPasswordBinding.inflate(inflater, container, false)
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
            binding.etSignupEmailSetPasswordUserPassword.clearFocus()
            val inputMethodManager : InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
            setEditTextTitle()
            true
        }
    }

    private fun setTextEditorActionListener() {
        binding.etSignupEmailSetPasswordUserPassword.setOnEditorActionListener {  _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetPasswordUserPassword)
                    setEditTextTitle()
                    true
                } else -> false
            }
        }
        binding.etSignupEmailSetPasswordUserPassword.setOnFocusChangeListener { _, hasFocus ->
            with(binding.layoutSignupEmailSetPasswordUserPassword) {
                if(hasFocus){
                    hint = getString(R.string.password)
                } else {
                    hint = getString(R.string.signup_email_set_password_message_length_fail_min)
                }
            }
        }
    }

    private fun setEditTextTitle() {
        with(binding.layoutSignupEmailSetPasswordUserPassword) {
            if(binding.etSignupEmailSetPasswordUserPassword.text.isNullOrEmpty() && !binding.etSignupEmailSetPasswordUserPassword.isFocused) {
                hint = getString(R.string.signup_email_set_password_message_length_fail_min)
            } else {
                hint = getString(R.string.password)
            }
        }
    }

    private fun setEditTextErrorMessage() {
        binding.etSignupEmailSetPasswordUserPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                with(binding.layoutSignupEmailSetPasswordUserPassword) {
                    isErrorEnabled = true
                    isCounterEnabled = true
                    if(s.toString().trim().length < 8){ // 비밀번호 길이 검사 1
                        error = getString(R.string.signup_email_set_password_message_length_fail_min)
                    } else if(s.toString().trim().length > 16) { // 비밀번호 길이 검사 2
                        error = getString(R.string.signup_email_set_password_message_length_fail_max)
                    } else if(!Pattern.matches("^[a-z|0-9|]+\$", s.toString().trim())) { // 비밀번호 양식 검사
                        error = getString(R.string.signup_email_set_password_message_format_fail)
                    } else {
                        isErrorEnabled = false
                        isCounterEnabled = false
                    }

                    if(isErrorEnabled){
                        binding.layoutSignupEmailSetPasswordUserPassword.boxStrokeColor = resources.getColor(R.color.red_FF4545, context?.theme)
                        ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetPasswordNext)
                    } else {
                        binding.layoutSignupEmailSetPasswordUserPassword.boxStrokeColor = resources.getColor(R.color.primary_green_23C882, context?.theme)
                        ButtonActivation.setSignupButtonActive(requireContext(), binding.btnSignupEmailSetPasswordNext)
                    }
                }
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
        binding.etSignupEmailSetPasswordUserPassword.filters = arrayOf(filterInputCheck)
    }

    private fun setBackClickListener(){
        binding.btnSignupEmailSetPasswordBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnSignupEmailSetPasswordNext.setOnClickListener {
            if(!binding.etSignupEmailSetPasswordUserPassword.text.isNullOrBlank()) {
                Navigation.findNavController(it).navigate(SignupEmailSetPasswordFragmentDirections.actionSignupEmailSetPasswordFragmentToSignupEmailSetPasswordConfirmationFragment(args.email,binding.etSignupEmailSetPasswordUserPassword.text.toString().trim()))
            }
        }
    }
}