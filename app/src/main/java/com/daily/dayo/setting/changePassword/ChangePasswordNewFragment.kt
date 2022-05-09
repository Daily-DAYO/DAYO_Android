package com.daily.dayo.setting.changePassword

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
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentChangePasswordNewBinding
import com.daily.dayo.signup.email.SignupEmailSetPasswordFragmentDirections
import com.daily.dayo.util.ButtonActivation
import com.daily.dayo.util.HideKeyBoardUtil
import com.daily.dayo.util.SetTextInputLayout
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class ChangePasswordNewFragment : Fragment() {
    private var binding by autoCleared<FragmentChangePasswordNewBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentChangePasswordNewBinding.inflate(inflater, container, false)
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
            HideKeyBoardUtil.hide(requireContext(), binding.etChangePasswordNewUserPassword)
            changeEditTextTitle()
            SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutChangePasswordNewUserPassword, binding.etChangePasswordNewUserPassword, binding.etChangePasswordNewUserPassword.text.isNullOrEmpty())
            true
        }
    }
    private fun setTextEditorActionListener() {
        binding.etChangePasswordNewUserPassword.setOnEditorActionListener {  _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(), binding.etChangePasswordNewUserPassword)
                    changeEditTextTitle()
                    SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutChangePasswordNewUserPassword, binding.etChangePasswordNewUserPassword, binding.etChangePasswordNewUserPassword.text.isNullOrEmpty())
                    true
                } else -> false
            }
        }
    }

    private fun initEditText() {
        SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutChangePasswordNewUserPassword, binding.etChangePasswordNewUserPassword, binding.etChangePasswordNewUserPassword.text.isNullOrEmpty())
        with(binding.etChangePasswordNewUserPassword) {
            setOnFocusChangeListener { _, hasFocus -> // EditText Title 설정
                with(binding.layoutChangePasswordNewUserPassword) {
                    if(hasFocus){
                        hint = getString(R.string.password)
                        SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutChangePasswordNewUserPassword, binding.etChangePasswordNewUserPassword, false)
                    } else {
                        hint = getString(R.string.signup_email_set_password_message_length_fail_min)
                        SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutChangePasswordNewUserPassword, binding.etChangePasswordNewUserPassword, true)
                    }
                }
            }
        }
    }

    private fun changeEditTextTitle() {
        with(binding.layoutChangePasswordNewUserPassword) {
            if(binding.etChangePasswordNewUserPassword.text.isNullOrEmpty()) {
                hint = getString(R.string.signup_email_set_password_message_length_fail_min)
            } else {
                hint = getString(R.string.password)
            }
        }
    }

    private fun verifyPassword() {
        binding.etChangePasswordNewUserPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().trim().length < 8){ // 비밀번호 길이 검사 1에
                    SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutChangePasswordNewUserPassword, binding.etChangePasswordNewUserPassword, false)
                    // 8자 이하일 때에는 오류 검사 실패시, 에러메시지가 나오지 않도록 디자인 수정
                    ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnChangePasswordNewNext)
                } else if(s.toString().trim().length > 16) { // 비밀번호 길이 검사 2
                    SetTextInputLayout.setEditTextErrorTheme(requireContext(), binding.layoutChangePasswordNewUserPassword, binding.etChangePasswordNewUserPassword, getString(R.string.signup_email_set_password_message_length_fail_max), false)
                    ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnChangePasswordNewNext)
                } else if(!Pattern.matches("^[a-z|0-9|]+\$", s.toString().trim())) { // 비밀번호 양식 검사
                    SetTextInputLayout.setEditTextErrorTheme(requireContext(), binding.layoutChangePasswordNewUserPassword, binding.etChangePasswordNewUserPassword, getString(R.string.signup_email_set_password_message_format_fail), false)
                    ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnChangePasswordNewNext)
                } else {
                    SetTextInputLayout.setEditTextErrorTheme(requireContext(), binding.layoutChangePasswordNewUserPassword, binding.etChangePasswordNewUserPassword, null, true)
                    ButtonActivation.setSignupButtonActive(requireContext(), binding.btnChangePasswordNewNext)
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
        binding.etChangePasswordNewUserPassword.filters = arrayOf(filterInputCheck)
    }

    private fun setBackClickListener(){
        binding.btnChangePasswordNewBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnChangePasswordNewNext.setOnClickListener {
            Navigation.findNavController(it).navigate(ChangePasswordNewFragmentDirections.actionChangePasswordNewFragmentToChangePasswordNewConfirmationFragment(binding.etChangePasswordNewUserPassword.text.toString().trim()))
        }
    }
}