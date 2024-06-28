package daily.dayo.presentation.fragment.account.signup

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.presentation.R
import daily.dayo.presentation.common.ButtonActivation
import daily.dayo.presentation.common.HideKeyBoardUtil
import daily.dayo.presentation.common.ReplaceUnicode.trimBlankText
import daily.dayo.presentation.common.SetTextInputLayout
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentSignupEmailSetPasswordConfirmationBinding
import java.util.regex.Pattern

@AndroidEntryPoint
class SignupEmailSetPasswordConfirmationFragment : Fragment() {
    private var binding by autoCleared<FragmentSignupEmailSetPasswordConfirmationBinding>()
    private val args by navArgs<SignupEmailSetPasswordConfirmationFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupEmailSetPasswordConfirmationBinding.inflate(inflater, container, false)
        setBackClickListener()
        setNextClickListener()
        initEditText()
        setLimitEditTextInputType()
        setTextEditorActionListener()
        verifyPassword()
        setUserPasswordEditText()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ ->
            HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetPasswordConfirmationUserInput)
            changeEditTextTitle()
            SetTextInputLayout.setEditTextTheme(
                requireContext(),
                binding.layoutSignupEmailSetPasswordConfirmationUserInput,
                binding.etSignupEmailSetPasswordConfirmationUserInput,
                binding.etSignupEmailSetPasswordConfirmationUserInput.text.isNullOrEmpty()
            )
            true
        }
    }

    private fun setTextEditorActionListener() {
        binding.etSignupEmailSetPasswordConfirmationUserInput.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetPasswordConfirmationUserInput)
                    changeEditTextTitle()
                    SetTextInputLayout.setEditTextTheme(
                        requireContext(),
                        binding.layoutSignupEmailSetPasswordConfirmationUserInput,
                        binding.etSignupEmailSetPasswordConfirmationUserInput,
                        binding.etSignupEmailSetPasswordConfirmationUserInput.text.isNullOrEmpty()
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
            binding.layoutSignupEmailSetPasswordConfirmationUserInput,
            binding.etSignupEmailSetPasswordConfirmationUserInput,
            binding.etSignupEmailSetPasswordConfirmationUserInput.text.isNullOrEmpty()
        )
        binding.etSignupEmailSetPasswordConfirmationUserInput.setOnFocusChangeListener { _, hasFocus ->
            with(binding.layoutSignupEmailSetPasswordConfirmationUserInput) {
                if (hasFocus) {
                    hint = getString(R.string.password_confirmation)
                    SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetPasswordConfirmationUserInput, binding.etSignupEmailSetPasswordConfirmationUserInput, false)
                } else {
                    hint = getString(R.string.signup_email_set_password_confirmation_edittext_hint)
                    SetTextInputLayout.setEditTextTheme(requireContext(), binding.layoutSignupEmailSetPasswordConfirmationUserInput, binding.etSignupEmailSetPasswordConfirmationUserInput, true)
                }
            }
        }
    }

    private fun changeEditTextTitle() {
        with(binding.layoutSignupEmailSetPasswordConfirmationUserInput) {
            if (binding.etSignupEmailSetPasswordConfirmationUserInput.text.isNullOrEmpty()) {
                hint = getString(R.string.signup_email_set_password_confirmation_edittext_hint)
            } else {
                hint = getString(R.string.password_confirmation)
            }
        }
    }

    private fun verifyPassword() {
        binding.etSignupEmailSetPasswordConfirmationUserInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (args.password != trimBlankText(binding.etSignupEmailSetPasswordConfirmationUserInput.text)) { // 동일 비밀번호 검사
                    ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetPasswordConfirmationNext)
                    SetTextInputLayout.setEditTextErrorTheme(
                        requireContext(),
                        binding.layoutSignupEmailSetPasswordConfirmationUserInput,
                        binding.etSignupEmailSetPasswordConfirmationUserInput,
                        getString(R.string.signup_email_set_password_confirmation_message_fail),
                        false
                    )
                } else {
                    ButtonActivation.setSignupButtonActive(requireContext(), binding.btnSignupEmailSetPasswordConfirmationNext)
                    SetTextInputLayout.setEditTextErrorTheme(
                        requireContext(),
                        binding.layoutSignupEmailSetPasswordConfirmationUserInput,
                        binding.etSignupEmailSetPasswordConfirmationUserInput,
                        null,
                        true
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
        binding.etSignupEmailSetPasswordConfirmationUserInput.filters = arrayOf(filterInputCheck)
    }

    private fun setUserPasswordEditText() {
        binding.password = args.password
    }

    private fun setBackClickListener() {
        binding.btnSignupEmailSetPasswordConfirmationBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnSignupEmailSetPasswordConfirmationNext.setOnDebounceClickListener {
            Navigation.findNavController(it).navigateSafe(
                currentDestinationId = R.id.SignupEmailSetPasswordConfirmationFragment,
                action = R.id.action_signupEmailSetPasswordConfirmationFragment_to_signupEmailSetProfileFragment,
                args = SignupEmailSetPasswordConfirmationFragmentDirections.actionSignupEmailSetPasswordConfirmationFragmentToSignupEmailSetProfileFragment(args.email, args.password).arguments
            )
        }
    }
}