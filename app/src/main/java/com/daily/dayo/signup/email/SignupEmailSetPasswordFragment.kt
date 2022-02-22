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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentSignupEmailSetPasswordBinding
import com.daily.dayo.util.ButtonActivation
import com.daily.dayo.util.HideKeyBoardUtil
import com.daily.dayo.util.SetTextInputLayout
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
        initEditText()
        setLimitEditTextInputType()
        setTextEditorActionListener()
        verifyPassword()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ ->
            HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetPasswordUserPassword)
            changeEditTextTitle()
            SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetPasswordUserPassword, binding.etSignupEmailSetPasswordUserPassword, binding.etSignupEmailSetPasswordUserPassword.text.isNullOrEmpty())
            true
        }
    }
    private fun setTextEditorActionListener() {
        binding.etSignupEmailSetPasswordUserPassword.setOnEditorActionListener {  _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetPasswordUserPassword)
                    changeEditTextTitle()
                    SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetPasswordUserPassword, binding.etSignupEmailSetPasswordUserPassword, binding.etSignupEmailSetPasswordUserPassword.text.isNullOrEmpty())
                    true
                } else -> false
            }
        }
    }

    private fun initEditText() {
        SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetPasswordUserPassword, binding.etSignupEmailSetPasswordUserPassword, binding.etSignupEmailSetPasswordUserPassword.text.isNullOrEmpty())
        with(binding.etSignupEmailSetPasswordUserPassword) {
            setOnFocusChangeListener { _, hasFocus -> // EditText Title 설정
                with(binding.layoutSignupEmailSetPasswordUserPassword) {
                    if(hasFocus){
                        hint = getString(R.string.password)
                        SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetPasswordUserPassword, binding.etSignupEmailSetPasswordUserPassword, false)
                    } else {
                        hint = getString(R.string.signup_email_set_password_message_length_fail_min)
                        SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetPasswordUserPassword, binding.etSignupEmailSetPasswordUserPassword, true)
                    }
                }
            }
        }
    }

    private fun changeEditTextTitle() {
        with(binding.layoutSignupEmailSetPasswordUserPassword) {
            if(binding.etSignupEmailSetPasswordUserPassword.text.isNullOrEmpty()) {
                hint = getString(R.string.signup_email_set_password_message_length_fail_min)
            } else {
                hint = getString(R.string.password)
            }
        }
    }

    private fun verifyPassword() {
        binding.etSignupEmailSetPasswordUserPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().trim().length < 8){ // 비밀번호 길이 검사 1에
                    SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetPasswordUserPassword, binding.etSignupEmailSetPasswordUserPassword, false)
                    // 8자 이하일 때에는 오류 검사 실패시, 에러메시지가 나오지 않도록 디자인 수정
                    ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetPasswordNext)
                } else if(s.toString().trim().length > 16) { // 비밀번호 길이 검사 2
                    SetTextInputLayout.setEditTextErrorTheme(requireContext(), binding.layoutSignupEmailSetPasswordUserPassword, binding.etSignupEmailSetPasswordUserPassword, getString(R.string.signup_email_set_password_message_length_fail_max), false)
                    ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetPasswordNext)
                } else if(!Pattern.matches("^[a-z|0-9|]+\$", s.toString().trim())) { // 비밀번호 양식 검사
                    SetTextInputLayout.setEditTextErrorTheme(requireContext(), binding.layoutSignupEmailSetPasswordUserPassword, binding.etSignupEmailSetPasswordUserPassword, getString(R.string.signup_email_set_password_message_format_fail), false)
                    ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetPasswordNext)
                } else {
                    SetTextInputLayout.setEditTextErrorTheme(requireContext(), binding.layoutSignupEmailSetPasswordUserPassword, binding.etSignupEmailSetPasswordUserPassword, null, true)
                    ButtonActivation.setSignupButtonActive(requireContext(), binding.btnSignupEmailSetPasswordNext)
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
            Navigation.findNavController(it).navigate(SignupEmailSetPasswordFragmentDirections.actionSignupEmailSetPasswordFragmentToSignupEmailSetPasswordConfirmationFragment(args.email,binding.etSignupEmailSetPasswordUserPassword.text.toString().trim()))
        }
    }
}