package com.daily.dayo.presentation.fragment.setting.findAccount

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentFindAccountPasswordNewPasswordConfirmationBinding
import com.daily.dayo.common.ButtonActivation
import com.daily.dayo.common.HideKeyBoardUtil
import com.daily.dayo.common.SetTextInputLayout
import com.daily.dayo.common.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class FindAccountPasswordNewPasswordConfirmationFragment : Fragment() {
    private var binding by autoCleared<FragmentFindAccountPasswordNewPasswordConfirmationBinding>()
    private val args by navArgs<FindAccountPasswordNewPasswordConfirmationFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindAccountPasswordNewPasswordConfirmationBinding.inflate(
            inflater,
            container,
            false
        )
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
            HideKeyBoardUtil.hide(
                requireContext(),
                binding.etFindAccountPasswordNewPasswordConfirmationUserInput
            )
            changeEditTextTitle()
            SetTextInputLayout.setEditTextTheme(
                requireContext(),
                binding.layoutFindAccountPasswordNewPasswordConfirmationUserInput,
                binding.etFindAccountPasswordNewPasswordConfirmationUserInput,
                binding.etFindAccountPasswordNewPasswordConfirmationUserInput.text.isNullOrEmpty()
            )
            true
        }
    }


    private fun setTextEditorActionListener() {
        binding.etFindAccountPasswordNewPasswordConfirmationUserInput.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(
                        requireContext(),
                        binding.etFindAccountPasswordNewPasswordConfirmationUserInput
                    )
                    changeEditTextTitle()
                    SetTextInputLayout.setEditTextTheme(
                        requireContext(),
                        binding.layoutFindAccountPasswordNewPasswordConfirmationUserInput,
                        binding.etFindAccountPasswordNewPasswordConfirmationUserInput,
                        binding.etFindAccountPasswordNewPasswordConfirmationUserInput.text.isNullOrEmpty()
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
            binding.layoutFindAccountPasswordNewPasswordConfirmationUserInput,
            binding.etFindAccountPasswordNewPasswordConfirmationUserInput,
            binding.etFindAccountPasswordNewPasswordConfirmationUserInput.text.isNullOrEmpty()
        )
        binding.etFindAccountPasswordNewPasswordConfirmationUserInput.setOnFocusChangeListener { _, hasFocus ->
            with(binding.layoutFindAccountPasswordNewPasswordConfirmationUserInput) {
                if (hasFocus) {
                    hint = getString(R.string.password_confirmation)
                    SetTextInputLayout.setEditTextTheme(
                        requireContext(),
                        binding.layoutFindAccountPasswordNewPasswordConfirmationUserInput,
                        binding.etFindAccountPasswordNewPasswordConfirmationUserInput,
                        false
                    )
                } else {
                    hint =
                        getString(R.string.signup_email_set_password_confirmation_edittext_hint)
                    SetTextInputLayout.setEditTextTheme(
                        requireContext(),
                        binding.layoutFindAccountPasswordNewPasswordConfirmationUserInput,
                        binding.etFindAccountPasswordNewPasswordConfirmationUserInput,
                        true
                    )
                }
            }
        }
    }

    private fun changeEditTextTitle() {
        with(binding.layoutFindAccountPasswordNewPasswordConfirmationUserInput) {
            if (binding.etFindAccountPasswordNewPasswordConfirmationUserInput.text.isNullOrEmpty()) {
                hint =
                    getString(R.string.signup_email_set_password_confirmation_edittext_hint)
            } else {
                hint = getString(R.string.password_confirmation)
            }
        }
    }

    private fun verifyPassword() {
        binding.etFindAccountPasswordNewPasswordConfirmationUserInput.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (args.password != binding.etFindAccountPasswordNewPasswordConfirmationUserInput.text.toString()
                        .trim()
                ) { // ?????? ???????????? ??????
                    ButtonActivation.setSignupButtonInactive(
                        requireContext(),
                        binding.btnFindAccountPasswordNewPasswordConfirmationNext
                    )
                    SetTextInputLayout.setEditTextErrorTheme(
                        requireContext(),
                        binding.layoutFindAccountPasswordNewPasswordConfirmationUserInput,
                        binding.etFindAccountPasswordNewPasswordConfirmationUserInput,
                        getString(R.string.signup_email_set_password_confirmation_message_fail),
                        false
                    )
                } else {
                    ButtonActivation.setSignupButtonActive(
                        requireContext(),
                        binding.btnFindAccountPasswordNewPasswordConfirmationNext
                    )
                    SetTextInputLayout.setEditTextErrorTheme(
                        requireContext(),
                        binding.layoutFindAccountPasswordNewPasswordConfirmationUserInput,
                        binding.etFindAccountPasswordNewPasswordConfirmationUserInput,
                        null,
                        true
                    )
                }
            }
        })
    }

    private fun setLimitEditTextInputType() {
        // InputFilter??? ???????????? ????????? ?????? ???????????? ?????????????????? ??????????????? ??????????????? ??????
        val filterInputCheck = InputFilter { source, start, end, dest, dstart, dend ->
            val ps = Pattern.compile("^[ ]+\$")
            if (ps.matcher(source).matches()) {
                return@InputFilter ""
            }
            null
        }
        binding.etFindAccountPasswordNewPasswordConfirmationUserInput.filters =
            arrayOf(filterInputCheck)
    }

    private fun setUserPasswordEditText() {
        binding.password = args.password
    }

    private fun setBackClickListener() {
        binding.btnFindAccountPasswordNewPasswordConfirmationBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        // TODO: ????????? ???????????? ?????? ??????
        binding.btnFindAccountPasswordNewPasswordConfirmationNext.setOnClickListener {
            findNavController().navigate(R.id.action_findAccountPasswordNewPasswordConfirmationFragment_to_findAccountPasswordCompleteFragment)
        }
    }
}