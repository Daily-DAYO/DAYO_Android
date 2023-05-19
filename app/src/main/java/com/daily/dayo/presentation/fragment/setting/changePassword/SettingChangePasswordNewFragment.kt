package com.daily.dayo.presentation.fragment.setting.changePassword

import android.app.AlertDialog
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.common.ButtonActivation
import com.daily.dayo.common.HideKeyBoardUtil
import com.daily.dayo.common.SetTextInputLayout
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.dialog.LoadingAlertDialog
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentSettingChangePasswordNewBinding
import com.daily.dayo.presentation.viewmodel.AccountViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class SettingChangePasswordNewFragment : Fragment() {
    private var binding by autoCleared<FragmentSettingChangePasswordNewBinding>()
    private val accountViewModel by activityViewModels<AccountViewModel>()
    private var isNewConfirmation = MutableLiveData<Boolean>(false)
    private lateinit var verifyPasswordTextWatcher: TextWatcher
    private lateinit var loadingAlertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingChangePasswordNewBinding.inflate(inflater, container, false)
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackClickListener()
        setLimitEditTextInputType(binding.etSettingChangePasswordNewUserInput)
        setLimitEditTextInputType(binding.etSettingChangePasswordNewConfirmation)
        setTextEditorActionListener(
            binding.layoutSettingChangePasswordNewUserInput,
            binding.etSettingChangePasswordNewUserInput,
            getString(R.string.password),
            getString(R.string.signup_email_set_password_message_length_fail_min)
        )
        setTextEditorActionListener(
            binding.layoutSettingChangePasswordNewConfirmation,
            binding.etSettingChangePasswordNewConfirmation,
            getString(R.string.password_confirmation),
            getString(R.string.signup_email_set_password_confirmation_edittext_hint)
        )
        initEditText(
            binding.layoutSettingChangePasswordNewUserInput,
            binding.etSettingChangePasswordNewUserInput,
            getString(R.string.password),
            getString(R.string.signup_email_set_password_message_length_fail_min)
        )
        initEditText(
            binding.layoutSettingChangePasswordNewConfirmation,
            binding.etSettingChangePasswordNewConfirmation,
            getString(R.string.password_confirmation),
            getString(R.string.signup_email_set_password_confirmation_edittext_hint)
        )
        setViewClickListener(
            binding.layoutSettingChangePasswordNewUserInput,
            binding.etSettingChangePasswordNewUserInput,
            getString(R.string.password),
            getString(R.string.signup_email_set_password_message_length_fail_min)
        )
        setViewClickListener(
            binding.layoutSettingChangePasswordNewConfirmation,
            binding.etSettingChangePasswordNewConfirmation,
            getString(R.string.password_confirmation),
            getString(R.string.signup_email_set_password_confirmation_edittext_hint)
        )
        observePasswordChange()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }

    private fun setBackClickListener() {
        binding.btnSettingChangePasswordNewBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observePasswordChange() {
        isNewConfirmation.observe(viewLifecycleOwner) {
            if (it) {
                binding.layoutSettingChangePasswordNewUserInput.hint = getString(R.string.password)
                checkNewPassword()
                with(binding.btnSettingChangePasswordNewNext) {
                    text = getString(R.string.change_password)
                    setOnDebounceClickListener {
                        LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
                        accountViewModel.requestChangePassword(
                            newPassword = binding.etSettingChangePasswordNewUserInput.text.toString()
                                .trim()
                        )
                        accountViewModel.changePasswordSuccess.observe(viewLifecycleOwner) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.change_password_success_message),
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.action_settingChangePasswordNewFragment_to_settingFragment)
                        }
                    }
                }
            } else {
                verifyPassword()
                with((binding.btnSettingChangePasswordNewNext)) {
                    text = getString(R.string.next)
                    setOnDebounceClickListener {
                        binding.etSettingChangePasswordNewUserInput.removeTextChangedListener(
                            verifyPasswordTextWatcher
                        )
                        binding.etSettingChangePasswordNewUserInput.backgroundTintList =
                            ContextCompat.getColorStateList(requireContext(), R.color.gray_6_F0F1F3)
                        binding.etSettingChangePasswordNewUserInput.isEnabled = false
                        ButtonActivation.setSignupButtonInactive(
                            requireContext(),
                            binding.btnSettingChangePasswordNewNext
                        )
                        binding.layoutSettingChangePasswordNewConfirmation.visibility = View.VISIBLE
                        isNewConfirmation.postValue(true)
                    }
                }
            }
        }
    }

    private fun initEditText(
        layout: TextInputLayout,
        editText: TextInputEditText,
        titleText: String,
        hintText: String
    ) {
        with(editText) {
            setOnFocusChangeListener { _, hasFocus -> // Title
                with(layout) {
                    if (hasFocus) {
                        hint = titleText
                        SetTextInputLayout.setEditTextTheme(
                            requireContext(), layout, editText, false
                        )
                    } else {
                        hint = hintText
                        SetTextInputLayout.setEditTextTheme(
                            requireContext(), layout, editText, true
                        )
                    }
                }
            }
        }
    }

    private fun changeEditTextTitle(
        layout: TextInputLayout,
        editText: TextInputEditText,
        titleText: String,
        hintText: String
    ) {
        with(layout) {
            if (editText.text.isNullOrEmpty()) {
                hint = hintText
            } else {
                hint = titleText
            }
        }
    }

    private fun checkNewPassword() {
        binding.etSettingChangePasswordNewConfirmation.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (binding.etSettingChangePasswordNewUserInput.text.toString()
                        .trim() != binding.etSettingChangePasswordNewConfirmation.text.toString()
                        .trim()
                ) { // 동일 비밀번호 검사
                    ButtonActivation.setSignupButtonInactive(
                        requireContext(),
                        binding.btnSettingChangePasswordNewNext
                    )
                    SetTextInputLayout.setEditTextErrorTheme(
                        requireContext(),
                        binding.layoutSettingChangePasswordNewConfirmation,
                        binding.etSettingChangePasswordNewConfirmation,
                        getString(R.string.signup_email_set_password_confirmation_message_fail),
                        false
                    )
                } else {
                    ButtonActivation.setSignupButtonActive(
                        requireContext(),
                        binding.btnSettingChangePasswordNewNext
                    )
                    SetTextInputLayout.setEditTextErrorTheme(
                        requireContext(),
                        binding.layoutSettingChangePasswordNewConfirmation,
                        binding.etSettingChangePasswordNewConfirmation,
                        null,
                        true
                    )
                }
            }
        })
    }

    private fun verifyPassword() {
        verifyPasswordTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().trim().length < 8) { // 비밀번호 길이 검사 1에
                    SetTextInputLayout.setEditTextTheme(
                        requireContext(),
                        binding.layoutSettingChangePasswordNewUserInput,
                        binding.etSettingChangePasswordNewUserInput,
                        false
                    )
                    // 8자 이하일 때에는 오류 검사 실패시, 에러메시지가 나오지 않도록 디자인 수정
                    ButtonActivation.setSignupButtonInactive(
                        requireContext(),
                        binding.btnSettingChangePasswordNewNext
                    )
                } else if (s.toString().trim().length > 16) { // 비밀번호 길이 검사 2
                    SetTextInputLayout.setEditTextErrorTheme(
                        requireContext(),
                        binding.layoutSettingChangePasswordNewUserInput,
                        binding.etSettingChangePasswordNewUserInput,
                        getString(R.string.signup_email_set_password_message_length_fail_max),
                        false
                    )
                    ButtonActivation.setSignupButtonInactive(
                        requireContext(),
                        binding.btnSettingChangePasswordNewNext
                    )
                } else if (!Pattern.matches("^[a-z|0-9|]+\$", s.toString().trim())) { // 비밀번호 양식 검사
                    SetTextInputLayout.setEditTextErrorTheme(
                        requireContext(),
                        binding.layoutSettingChangePasswordNewUserInput,
                        binding.etSettingChangePasswordNewUserInput,
                        getString(R.string.signup_email_set_password_message_format_fail),
                        false
                    )
                    ButtonActivation.setSignupButtonInactive(
                        requireContext(),
                        binding.btnSettingChangePasswordNewNext
                    )
                } else {
                    SetTextInputLayout.setEditTextErrorTheme(
                        requireContext(),
                        binding.layoutSettingChangePasswordNewUserInput,
                        binding.etSettingChangePasswordNewUserInput,
                        null,
                        true
                    )
                    ButtonActivation.setSignupButtonActive(
                        requireContext(),
                        binding.btnSettingChangePasswordNewNext
                    )
                }
            }
        }
        binding.etSettingChangePasswordNewUserInput.addTextChangedListener(verifyPasswordTextWatcher)
    }

    private fun setLimitEditTextInputType(editText: TextInputEditText) {
        // InputFilter로 띄어쓰기 입력만 막고 나머지는 안내메시지로 띄워지도록 사용자에게 유도
        val filterInputCheck = InputFilter { source, start, end, dest, dstart, dend ->
            val ps = Pattern.compile("^[ ]+\$")
            if (ps.matcher(source).matches()) {
                return@InputFilter ""
            }
            null
        }
        editText.filters = arrayOf(filterInputCheck)
    }

    private fun setViewClickListener(
        layout: TextInputLayout,
        editText: TextInputEditText,
        titleText: String,
        hintText: String
    ) {
        requireView().setOnTouchListener { _, _ ->
            HideKeyBoardUtil.hide(requireContext(), editText)
            changeEditTextTitle(layout, editText, titleText, hintText)
            SetTextInputLayout.setEditTextTheme(
                requireContext(),
                layout,
                editText,
                editText.text.isNullOrEmpty()
            )
            true
        }
    }

    private fun setTextEditorActionListener(
        layout: TextInputLayout,
        editText: TextInputEditText,
        titleText: String,
        hintText: String
    ) {
        editText.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(), editText)
                    changeEditTextTitle(layout, editText, titleText, hintText)
                    SetTextInputLayout.setEditTextTheme(
                        requireContext(),
                        layout,
                        editText,
                        editText.text.isNullOrEmpty()
                    )
                    true
                }
                else -> false
            }
        }
    }
}