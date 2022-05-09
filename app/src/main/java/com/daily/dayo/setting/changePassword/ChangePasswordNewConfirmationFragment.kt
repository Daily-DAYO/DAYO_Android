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
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentChangePasswordNewConfirmationBinding
import com.daily.dayo.setting.viewmodel.SettingViewModel
import com.daily.dayo.util.ButtonActivation
import com.daily.dayo.util.HideKeyBoardUtil
import com.daily.dayo.util.SetTextInputLayout
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class ChangePasswordNewConfirmationFragment : Fragment() {
    private var binding by autoCleared<FragmentChangePasswordNewConfirmationBinding>()
    private val args by navArgs<ChangePasswordNewConfirmationFragmentArgs>()
    private val settingViewModel by activityViewModels<SettingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentChangePasswordNewConfirmationBinding.inflate(inflater, container, false)
        setBackClickListener()
        setNextClickListener()
        initEditText()
        setLimitEditTextInputType()
        setTextEditorActionListener()
        verifyPassword()
        setUserPasswordEditText()
        observeChangeStateCallback()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ ->
            HideKeyBoardUtil.hide(requireContext(),
                binding.etChangePasswordNewConfirmationUserInput)
            changeEditTextTitle()
            SetTextInputLayout.setEditTextTheme(requireContext(),
                binding.layoutChangePasswordNewConfirmationUserInput,
                binding.etChangePasswordNewConfirmationUserInput,
                binding.etChangePasswordNewConfirmationUserInput.text.isNullOrEmpty())
            true
        }
    }

    private fun setTextEditorActionListener() {
        binding.etChangePasswordNewConfirmationUserInput.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(),
                        binding.etChangePasswordNewConfirmationUserInput)
                    changeEditTextTitle()
                    SetTextInputLayout.setEditTextTheme(requireContext(),
                        binding.layoutChangePasswordNewConfirmationUserInput,
                        binding.etChangePasswordNewConfirmationUserInput,
                        binding.etChangePasswordNewConfirmationUserInput.text.isNullOrEmpty())
                    true
                }
                else -> false
            }
        }
    }

    private fun initEditText() {
        SetTextInputLayout.setEditTextTheme(requireContext(),
            binding.layoutChangePasswordNewConfirmationUserInput,
            binding.etChangePasswordNewConfirmationUserInput,
            binding.etChangePasswordNewConfirmationUserInput.text.isNullOrEmpty())
        binding.etChangePasswordNewConfirmationUserInput.setOnFocusChangeListener { _, hasFocus ->
            with(binding.layoutChangePasswordNewConfirmationUserInput) {
                if (hasFocus) {
                    hint = getString(R.string.password_confirmation)
                    SetTextInputLayout.setEditTextTheme(requireContext(),
                        binding.layoutChangePasswordNewConfirmationUserInput,
                        binding.etChangePasswordNewConfirmationUserInput,
                        false)
                } else {
                    hint = getString(R.string.signup_email_set_password_confirmation_edittext_hint)
                    SetTextInputLayout.setEditTextTheme(requireContext(),
                        binding.layoutChangePasswordNewConfirmationUserInput,
                        binding.etChangePasswordNewConfirmationUserInput,
                        true)
                }
            }
        }
    }

    private fun changeEditTextTitle() {
        with(binding.layoutChangePasswordNewConfirmationUserInput) {
            if (binding.etChangePasswordNewConfirmationUserInput.text.isNullOrEmpty()) {
                hint = getString(R.string.signup_email_set_password_confirmation_edittext_hint)
            } else {
                hint = getString(R.string.password_confirmation)
            }
        }
    }

    private fun verifyPassword() {
        binding.etChangePasswordNewConfirmationUserInput.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (args.password != binding.etChangePasswordNewConfirmationUserInput.text.toString().trim()) { // 동일 비밀번호 검사
                    ButtonActivation.setSignupButtonInactive(requireContext(),
                        binding.btnChangePasswordNewConfirmationNext)
                    SetTextInputLayout.setEditTextErrorTheme(requireContext(),
                        binding.layoutChangePasswordNewConfirmationUserInput,
                        binding.etChangePasswordNewConfirmationUserInput,
                        getString(R.string.signup_email_set_password_confirmation_message_fail),
                        false)
                } else {
                    ButtonActivation.setSignupButtonActive(requireContext(),
                        binding.btnChangePasswordNewConfirmationNext)
                    SetTextInputLayout.setEditTextErrorTheme(requireContext(),
                        binding.layoutChangePasswordNewConfirmationUserInput,
                        binding.etChangePasswordNewConfirmationUserInput,
                        null,
                        true)
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
        binding.etChangePasswordNewConfirmationUserInput.filters = arrayOf(filterInputCheck)
    }

    private fun setUserPasswordEditText() {
        binding.password = args.password
    }

    private fun setBackClickListener() {
        binding.btnChangePasswordNewConfirmationBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnChangePasswordNewConfirmationNext.setOnClickListener {
            settingViewModel.requestChangePassword(binding.etChangePasswordNewConfirmationUserInput.text.toString())
        }
    }

    private fun observeChangeStateCallback() {
        settingViewModel.changePasswordSuccess.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess.peekContent()) {
                findNavController().navigate(R.id.action_ChangePasswordNewConfirmationFragment_to_settingFragment)
                Toast.makeText(requireContext(),
                    R.string.change_password_complete,
                    Toast.LENGTH_SHORT).show()
            } else {
                // TODO : 비밀번호 변경 실패 시 메시지 작성
            }
        })
    }
}