package com.daily.dayo.presentation.fragment.account.findAccount

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentFindAccountPasswordCheckEmailBinding
import com.daily.dayo.presentation.viewmodel.AccountViewModel
import com.daily.dayo.common.ButtonActivation
import com.daily.dayo.common.HideKeyBoardUtil
import com.daily.dayo.common.ReplaceUnicode.trimBlankText
import com.daily.dayo.common.SetTextInputLayout
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class FindAccountPasswordCheckEmailFragment : Fragment() {
    private var binding by autoCleared<FragmentFindAccountPasswordCheckEmailBinding>()
    private val loginViewModel by activityViewModels<AccountViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindAccountPasswordCheckEmailBinding.inflate(inflater, container, false)
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
            with(binding.etFindAccountPasswordCheckEmailUserInput) {
                HideKeyBoardUtil.hide(requireContext(), this)
                changeEditTextTitle()
                SetTextInputLayout.setEditTextTheme(
                    requireContext(),
                    binding.layoutFindAccountPasswordCheckEmailUserInput,
                    this,
                    this.text.isNullOrEmpty()
                )
            }

            verifyEmailAddress()
            true
        }
    }

    private fun setTextEditorActionListener() {
        with(binding.etFindAccountPasswordCheckEmailUserInput) {
            this.setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        HideKeyBoardUtil.hide(requireContext(), this)
                        changeEditTextTitle()
                        SetTextInputLayout.setEditTextTheme(
                            requireContext(),
                            binding.layoutFindAccountPasswordCheckEmailUserInput,
                            this,
                            this.text.isNullOrEmpty()
                        )
                        verifyEmailAddress()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun initEditText() {
        with(binding.etFindAccountPasswordCheckEmailUserInput) {
            SetTextInputLayout.setEditTextTheme(
                requireContext(),
                binding.layoutFindAccountPasswordCheckEmailUserInput,
                this,
                this.text.isNullOrEmpty()
            )

            setOnFocusChangeListener { _, hasFocus -> // Title
                with(binding.layoutFindAccountPasswordCheckEmailUserInput) {
                    if (hasFocus) {
                        hint = getString(R.string.email)
                        SetTextInputLayout.setEditTextTheme(
                            requireContext(),
                            binding.layoutFindAccountPasswordCheckEmailUserInput,
                            binding.etFindAccountPasswordCheckEmailUserInput,
                            false
                        )
                    } else {
                        hint = getString(R.string.email_address_example)
                        SetTextInputLayout.setEditTextTheme(
                            requireContext(),
                            binding.layoutFindAccountPasswordCheckEmailUserInput,
                            binding.etFindAccountPasswordCheckEmailUserInput,
                            true
                        )
                    }
                }
            }
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    with(binding.layoutFindAccountPasswordCheckEmailUserInput) {
                        isErrorEnabled = false
                        SetTextInputLayout.setEditTextTheme(
                            requireContext(),
                            binding.layoutFindAccountPasswordCheckEmailUserInput,
                            binding.etFindAccountPasswordCheckEmailUserInput,
                            false
                        )
                    }
                }
            })
        }
    }

    private fun changeEditTextTitle() {
        with(binding.layoutFindAccountPasswordCheckEmailUserInput) {
            if (binding.etFindAccountPasswordCheckEmailUserInput.text.isNullOrEmpty()) {
                hint = getString(R.string.email_address_example)
            } else {
                hint = getString(R.string.email)
            }
        }
    }

    private fun verifyEmailAddress() {
        if (Patterns.EMAIL_ADDRESS.matcher(
                trimBlankText(binding.etFindAccountPasswordCheckEmailUserInput.text)
            ).matches()
        ) { // 이메일 주소 양식 검사
            loginViewModel.requestCheckEmail(
                inputEmail = trimBlankText(binding.etFindAccountPasswordCheckEmailUserInput.text)
            )
            loginViewModel.checkEmailSuccess.observe(viewLifecycleOwner) { isSuccess ->
                if (isSuccess) { // 이메일 가입과 달리 중복검사해서 false 경우에는 있는 이메일 이므로 성공 처리
                    SetTextInputLayout.setEditTextErrorThemeWithIcon(
                        requireContext(),
                        binding.layoutFindAccountPasswordCheckEmailUserInput,
                        binding.etFindAccountPasswordCheckEmailUserInput,
                        null,
                        true
                    )
                    ButtonActivation.setSignupButtonActive(
                        requireContext(),
                        binding.btnFindAccountPasswordCheckEmailNext
                    )
                } else {
                    SetTextInputLayout.setEditTextErrorThemeWithIcon(
                        requireContext(),
                        binding.layoutFindAccountPasswordCheckEmailUserInput,
                        binding.etFindAccountPasswordCheckEmailUserInput,
                        getString(R.string.find_account_password_check_email_message_exist_fail),
                        false
                    )
                    ButtonActivation.setSignupButtonInactive(
                        requireContext(),
                        binding.btnFindAccountPasswordCheckEmailNext
                    )
                }
            }
        } else {
            SetTextInputLayout.setEditTextErrorThemeWithIcon(
                requireContext(),
                binding.layoutFindAccountPasswordCheckEmailUserInput,
                binding.etFindAccountPasswordCheckEmailUserInput,
                getString(R.string.signup_email_set_email_address_message_format_fail),
                false
            )
            ButtonActivation.setSignupButtonInactive(
                requireContext(),
                binding.btnFindAccountPasswordCheckEmailNext
            )
        }
    }

    private fun setLimitEditTextInputType() {
        val filterInputCheck = InputFilter { source, start, end, dest, dstart, dend ->
            val ps = Pattern.compile("^[ ]+\$")
            if (ps.matcher(source).matches()) {
                return@InputFilter ""
            }
            null
        }
        binding.etFindAccountPasswordCheckEmailUserInput.filters = arrayOf(filterInputCheck)
    }

    private fun setBackClickListener() {
        binding.btnFindAccountPasswordCheckEmailBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnFindAccountPasswordCheckEmailNext.setOnDebounceClickListener {
            Navigation.findNavController(it).navigate(
                FindAccountPasswordCheckEmailFragmentDirections.actionFindAccountPasswordCheckEmailFragmentToFindAccountPasswordCheckEmailCertificateFragment(
                    trimBlankText(binding.etFindAccountPasswordCheckEmailUserInput.text)
                )
            )
        }
    }
}