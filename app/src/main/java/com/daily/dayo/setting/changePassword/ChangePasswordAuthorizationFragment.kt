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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentChangePasswordAuthorizationBinding
import com.daily.dayo.setting.viewmodel.SettingViewModel
import com.daily.dayo.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class ChangePasswordAuthorizationFragment : Fragment() {
    private var binding by autoCleared<FragmentChangePasswordAuthorizationBinding>()
    private val settingViewModel by activityViewModels<SettingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentChangePasswordAuthorizationBinding.inflate(inflater, container, false)
        setBackClickListener()
        setNextClickListener()
        initEditText()
        setLimitEditTextInputType()
        setTextEditorActionListener()
        verifyPassword()
        observeCheckCurrentPasswordStateCallback()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ ->
            HideKeyBoardUtil.hide(requireContext(),
                binding.etChangePasswordAuthorizationUserPassword)
            changeEditTextTitle()
            SetTextInputLayout.setEditTextTheme(requireContext(),
                binding.layoutChangePasswordAuthorizationUserPassword,
                binding.etChangePasswordAuthorizationUserPassword,
                binding.etChangePasswordAuthorizationUserPassword.text.isNullOrEmpty())
            true
        }
    }

    private fun setTextEditorActionListener() {
        binding.etChangePasswordAuthorizationUserPassword.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(),
                        binding.etChangePasswordAuthorizationUserPassword)
                    changeEditTextTitle()
                    SetTextInputLayout.setEditTextTheme(requireContext(),
                        binding.layoutChangePasswordAuthorizationUserPassword,
                        binding.etChangePasswordAuthorizationUserPassword,
                        binding.etChangePasswordAuthorizationUserPassword.text.isNullOrEmpty())
                    true
                }
                else -> false
            }
        }
    }

    private fun initEditText() {
        SetTextInputLayout.setEditTextTheme(requireContext(),
            binding.layoutChangePasswordAuthorizationUserPassword,
            binding.etChangePasswordAuthorizationUserPassword,
            binding.etChangePasswordAuthorizationUserPassword.text.isNullOrEmpty())
        with(binding.etChangePasswordAuthorizationUserPassword) {
            setOnFocusChangeListener { _, hasFocus -> // EditText Title 설정
                with(binding.layoutChangePasswordAuthorizationUserPassword) {
                    if (hasFocus) {
                        hint = getString(R.string.password)
                        SetTextInputLayout.setEditTextTheme(requireContext(),
                            binding.layoutChangePasswordAuthorizationUserPassword,
                            binding.etChangePasswordAuthorizationUserPassword,
                            false)
                    } else {
                        hint = getString(R.string.signup_email_set_password_message_length_fail_min)
                        SetTextInputLayout.setEditTextTheme(requireContext(),
                            binding.layoutChangePasswordAuthorizationUserPassword,
                            binding.etChangePasswordAuthorizationUserPassword,
                            true)
                    }
                }
            }
        }
    }

    private fun changeEditTextTitle() {
        with(binding.layoutChangePasswordAuthorizationUserPassword) {
            if (binding.etChangePasswordAuthorizationUserPassword.text.isNullOrEmpty()) {
                hint = getString(R.string.signup_email_set_password_message_length_fail_min)
            } else {
                hint = getString(R.string.password)
            }
        }
    }

    private fun verifyPassword() {
        binding.etChangePasswordAuthorizationUserPassword.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                settingViewModel.requestCheckCurrentPassword(binding.etChangePasswordAuthorizationUserPassword.text.toString())
            }
        })
    }

    private fun observeCheckCurrentPasswordStateCallback() {
        settingViewModel.checkCurrentPasswordSuccess.observe(viewLifecycleOwner,
            Observer { isSuccess ->
                if (isSuccess.peekContent()) {
                    SetTextInputLayout.setEditTextErrorTheme(requireContext(),
                        binding.layoutChangePasswordAuthorizationUserPassword,
                        binding.etChangePasswordAuthorizationUserPassword,
                        null,
                        true)
                    ButtonActivation.setSignupButtonActive(requireContext(),
                        binding.btnChangePasswordAuthorizationNext)
                } else {
                    SetTextInputLayout.setEditTextErrorTheme(requireContext(),
                        binding.layoutChangePasswordAuthorizationUserPassword,
                        binding.etChangePasswordAuthorizationUserPassword,
                        getString(R.string.change_password_authorization_message_fail),
                        false)
                    ButtonActivation.setSignupButtonInactive(requireContext(),
                        binding.btnChangePasswordAuthorizationNext)
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
        binding.etChangePasswordAuthorizationUserPassword.filters = arrayOf(filterInputCheck)
    }

    private fun setBackClickListener() {
        binding.btnChangePasswordAuthorizationBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnChangePasswordAuthorizationNext.setOnClickListener {
            findNavController().navigate(R.id.action_changePasswordAuthorizationFragment_to_changePasswordNewFragment)
        }
    }

    override fun onDestroy() {
        // TODO: 올바른 비밀번호 입력후 뒤로간뒤 다시 돌아오는 경우 조건을 통과시키는 경우를 피하기위하여 임시 조치
        settingViewModel.checkCurrentPasswordSuccess.postValue(Event(false))
        super.onDestroy()
    }
}