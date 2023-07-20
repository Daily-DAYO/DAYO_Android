package com.daily.dayo.presentation.fragment.account.findAccount

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
import com.daily.dayo.databinding.FragmentFindAccountPasswordNewPasswordBinding
import com.daily.dayo.common.ButtonActivation
import com.daily.dayo.common.HideKeyBoardUtil
import com.daily.dayo.common.ReplaceUnicode.trimBlankText
import com.daily.dayo.common.SetTextInputLayout
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class FindAccountPasswordNewPasswordFragment : Fragment() {
    private var binding by autoCleared<FragmentFindAccountPasswordNewPasswordBinding>()
    private val args by navArgs<FindAccountPasswordNewPasswordFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindAccountPasswordNewPasswordBinding.inflate(inflater, container, false)
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
            HideKeyBoardUtil.hide(
                requireContext(),
                binding.etFindAccountPasswordNewPasswordUserPassword
            )
            changeEditTextTitle()
            SetTextInputLayout.setEditTextTheme(
                requireContext(),
                binding.layoutFindAccountPasswordNewPasswordUserPassword,
                binding.etFindAccountPasswordNewPasswordUserPassword,
                binding.etFindAccountPasswordNewPasswordUserPassword.text.isNullOrEmpty()
            )
            true
        }
    }

    private fun setTextEditorActionListener() {
        binding.etFindAccountPasswordNewPasswordUserPassword.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(
                        requireContext(),
                        binding.etFindAccountPasswordNewPasswordUserPassword
                    )
                    changeEditTextTitle()
                    SetTextInputLayout.setEditTextTheme(
                        requireContext(),
                        binding.layoutFindAccountPasswordNewPasswordUserPassword,
                        binding.etFindAccountPasswordNewPasswordUserPassword,
                        binding.etFindAccountPasswordNewPasswordUserPassword.text.isNullOrEmpty()
                    )
                    true
                }
                else -> false
            }
        }
    }

    private fun initEditText() {
        SetTextInputLayout.setEditTextTheme(
            requireContext(),
            binding.layoutFindAccountPasswordNewPasswordUserPassword,
            binding.etFindAccountPasswordNewPasswordUserPassword,
            binding.etFindAccountPasswordNewPasswordUserPassword.text.isNullOrEmpty()
        )
        with(binding.etFindAccountPasswordNewPasswordUserPassword) {
            setOnFocusChangeListener { _, hasFocus -> // EditText Title 설정
                with(binding.layoutFindAccountPasswordNewPasswordUserPassword) {
                    if (hasFocus) {
                        hint = getString(R.string.password)
                        SetTextInputLayout.setEditTextTheme(
                            requireContext(),
                            binding.layoutFindAccountPasswordNewPasswordUserPassword,
                            binding.etFindAccountPasswordNewPasswordUserPassword,
                            false
                        )
                    } else {
                        hint =
                            getString(R.string.signup_email_set_password_message_length_fail_min)
                        SetTextInputLayout.setEditTextTheme(
                            requireContext(),
                            binding.layoutFindAccountPasswordNewPasswordUserPassword,
                            binding.etFindAccountPasswordNewPasswordUserPassword,
                            true
                        )
                    }
                }
            }
        }
    }

    private fun changeEditTextTitle() {
        with(binding.layoutFindAccountPasswordNewPasswordUserPassword) {
            if (binding.etFindAccountPasswordNewPasswordUserPassword.text.isNullOrEmpty()) {
                hint = getString(R.string.signup_email_set_password_message_length_fail_min)
            } else {
                hint = getString(R.string.password)
            }
        }
    }

    private fun verifyPassword() {
        binding.etFindAccountPasswordNewPasswordUserPassword.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (trimBlankText(s).length < 8) { // 비밀번호 길이 검사 1에
                    SetTextInputLayout.setEditTextTheme(
                        requireContext(),
                        binding.layoutFindAccountPasswordNewPasswordUserPassword,
                        binding.etFindAccountPasswordNewPasswordUserPassword,
                        false
                    )
                    // 8자 이하일 때에는 오류 검사 실패시, 에러메시지가 나오지 않도록 디자인 수정
                    ButtonActivation.setSignupButtonInactive(
                        requireContext(),
                        binding.btnFindAccountPasswordNewPasswordNext
                    )
                } else if (trimBlankText(s).length > 16) { // 비밀번호 길이 검사 2
                    SetTextInputLayout.setEditTextErrorTheme(
                        requireContext(),
                        binding.layoutFindAccountPasswordNewPasswordUserPassword,
                        binding.etFindAccountPasswordNewPasswordUserPassword,
                        getString(R.string.signup_email_set_password_message_length_fail_max),
                        false
                    )
                    ButtonActivation.setSignupButtonInactive(
                        requireContext(),
                        binding.btnFindAccountPasswordNewPasswordNext
                    )
                } else if (!Pattern.matches("^[a-z|0-9|]+\$", trimBlankText(s))) { // 비밀번호 양식 검사
                    SetTextInputLayout.setEditTextErrorTheme(
                        requireContext(),
                        binding.layoutFindAccountPasswordNewPasswordUserPassword,
                        binding.etFindAccountPasswordNewPasswordUserPassword,
                        getString(R.string.signup_email_set_password_message_format_fail),
                        false
                    )
                    ButtonActivation.setSignupButtonInactive(
                        requireContext(),
                        binding.btnFindAccountPasswordNewPasswordNext
                    )
                } else {
                    SetTextInputLayout.setEditTextErrorTheme(
                        requireContext(),
                        binding.layoutFindAccountPasswordNewPasswordUserPassword,
                        binding.etFindAccountPasswordNewPasswordUserPassword,
                        null,
                        true
                    )
                    ButtonActivation.setSignupButtonActive(
                        requireContext(),
                        binding.btnFindAccountPasswordNewPasswordNext
                    )
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
        binding.etFindAccountPasswordNewPasswordUserPassword.filters = arrayOf(filterInputCheck)
    }

    private fun setBackClickListener() {
        binding.btnFindAccountPasswordNewPasswordBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnFindAccountPasswordNewPasswordNext.setOnDebounceClickListener {
            Navigation.findNavController(it).navigate(
                FindAccountPasswordNewPasswordFragmentDirections.actionFindAccountPasswordNewPasswordFragmentToFindAccountPasswordNewPasswordConfirmationFragment(
                    args.email,
                    trimBlankText(binding.etFindAccountPasswordNewPasswordUserPassword.text)
                )
            )
        }
    }
}