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
import com.daily.dayo.databinding.FragmentSignupEmailSetPasswordConfirmationBinding
import com.daily.dayo.util.ButtonActivation
import com.daily.dayo.util.HideKeyBoardUtil
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
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
        setLimitEditTextInputType()
        setEditTextErrorMessage()
        setTextEditorActionListener()
        setUserPasswordEditText()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ ->
            binding.etSignupEmailSetPasswordConfirmationUserInput.clearFocus()
            val inputMethodManager : InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
            setEditTextTitle()
            true
        }
    }

    private fun setTextEditorActionListener() {
        binding.etSignupEmailSetPasswordConfirmationUserInput.setOnEditorActionListener {  _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetPasswordConfirmationUserInput)
                    setEditTextTitle()
                    true
                } else -> false
            }
        }
        binding.etSignupEmailSetPasswordConfirmationUserInput.setOnFocusChangeListener { _, hasFocus ->
            with(binding.layoutSignupEmailSetPasswordConfirmationUserInput) {
                if(hasFocus){
                    hint = getString(R.string.password_confirmation)
                } else {
                    hint = getString(R.string.signup_email_set_password_confirmation_edittext_hint)
                }
            }
        }
    }


    private fun setEditTextTitle() {
        with(binding.layoutSignupEmailSetPasswordConfirmationUserInput) {
            if(binding.etSignupEmailSetPasswordConfirmationUserInput.text.isNullOrEmpty() && !binding.etSignupEmailSetPasswordConfirmationUserInput.isFocused) {
                hint = getString(R.string.signup_email_set_password_confirmation_edittext_hint)
            } else {
                hint = getString(R.string.password_confirmation)
            }
        }
    }

    private fun setEditTextErrorMessage() {
        binding.etSignupEmailSetPasswordConfirmationUserInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                with(binding.layoutSignupEmailSetPasswordConfirmationUserInput) {
                    isErrorEnabled = true
                    if(args.password != binding.etSignupEmailSetPasswordConfirmationUserInput.text.toString().trim() ){ // 동일 비밀번호 검사
                        error = getString(R.string.signup_email_set_password_confirmation_message_fail)
                    } else {
                        isErrorEnabled = false
                    }

                    if(isErrorEnabled){
                        binding.layoutSignupEmailSetPasswordConfirmationUserInput.boxStrokeColor = resources.getColor(R.color.red_FF4545, context?.theme)
                        ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetPasswordConfirmationNext)
                    } else {
                        binding.layoutSignupEmailSetPasswordConfirmationUserInput.boxStrokeColor = resources.getColor(R.color.primary_green_23C882, context?.theme)
                        ButtonActivation.setSignupButtonActive(requireContext(), binding.btnSignupEmailSetPasswordConfirmationNext)
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
        binding.etSignupEmailSetPasswordConfirmationUserInput.filters = arrayOf(filterInputCheck)
    }

    private fun setUserPasswordEditText() {
        binding.etSignupEmailSetPasswordConfirmationUserPassword.setText(args.password)
    }

    private fun setBackClickListener(){
        binding.btnSignupEmailSetPasswordConfirmationBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnSignupEmailSetPasswordConfirmationNext.setOnClickListener {
            if(!binding.etSignupEmailSetPasswordConfirmationUserInput.text.isNullOrBlank()) {
                Navigation.findNavController(it).navigate(SignupEmailSetPasswordConfirmationFragmentDirections.actionSignupEmailSetPasswordConfirmationFragmentToSignupEmailSetProfileFragment(args.email,args.password))
            }
        }
    }
}