package com.daily.dayo.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentLoginEmailBinding
import com.daily.dayo.util.ButtonActivation
import com.daily.dayo.util.HideKeyBoardUtil
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginEmailFragment : Fragment() {
    private var binding by autoCleared<FragmentLoginEmailBinding>()
    private val loginViewModel by activityViewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginEmailBinding.inflate(inflater, container, false)
        setBackClickListener()
        setNextClickListener()
        initEditText()
        setTextEditorActionListener()
        activationNextButton()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       view.setOnTouchListener { _, _ ->
            HideKeyBoardUtil.hide(requireContext(), binding.etLoginEmailAddress)
            HideKeyBoardUtil.hide(requireContext(), binding.etLoginEmailPassword)
            true
        }
    }
    private fun setTextEditorActionListener() {
        binding.etLoginEmailAddress.setOnEditorActionListener{ _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(), binding.etLoginEmailAddress)
                    HideKeyBoardUtil.hide(requireContext(), binding.etLoginEmailPassword)
                    true
                } else -> false
            }
        }
    }

    private fun setBackClickListener() {
        binding.btnLoginEmailBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnLoginEmailNext.setOnClickListener {
            // TODO : 로그인 요청 API 연동
            if(true) {
                binding.tvLoginEmailGuideMessage.visibility = View.INVISIBLE
            } else {
                binding.tvLoginEmailGuideMessage.visibility = View.VISIBLE
            }
        }
    }

    private fun initEditText() {
        with(binding.etLoginEmailAddress) {
            setOnFocusChangeListener { _, hasFocus -> //
                with(binding.layoutLoginEmailAddress) {
                    if(hasFocus){
                        hint = ""
                        binding.etLoginEmailAddress.backgroundTintList = resources.getColorStateList(R.color.primary_green_23C882, context?.theme)
                    } else if(!binding.etLoginEmailAddress.text.isNullOrEmpty()){
                        hint = ""
                        binding.etLoginEmailAddress.backgroundTintList = null
                    } else {
                        hint = getString(R.string.hint_login_email_address)
                        binding.etLoginEmailAddress.backgroundTintList = null
                    }
                }
            }
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {  }
                override fun afterTextChanged(s: Editable?) {
                    with(binding.layoutLoginEmailAddress) {
                        boxStrokeColor = resources.getColor(R.color.primary_green_23C882, context?.theme)
                    }
                }
            })
        }
        with(binding.etLoginEmailPassword) {
            setOnFocusChangeListener { _, hasFocus -> // Title
                with(binding.layoutLoginEmailPassword) {
                    if(hasFocus){
                        hint = ""
                        binding.etLoginEmailPassword.backgroundTintList = resources.getColorStateList(R.color.primary_green_23C882, context?.theme)
                    } else if(!binding.etLoginEmailPassword.text.isNullOrEmpty()){
                        hint = ""
                        binding.etLoginEmailPassword.backgroundTintList = null
                    } else {
                        hint = getString(R.string.hint_login_email_password)
                        binding.etLoginEmailPassword.backgroundTintList = null
                    }
                }
            }
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {  }
                override fun afterTextChanged(s: Editable?) {
                    with(binding.layoutLoginEmailPassword) {
                        boxStrokeColor = resources.getColor(R.color.primary_green_23C882, context?.theme)
                    }
                }
            })
        }
    }

    private fun activationNextButton() {
        with(binding) {
            etLoginEmailAddress.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
                override fun afterTextChanged(s: Editable?) {
                    if(!s.toString().isNullOrEmpty() && !etLoginEmailPassword.text.isNullOrEmpty()) {
                        ButtonActivation.setSignupButtonActive(requireContext(), binding.btnLoginEmailNext)
                    } else {
                        ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnLoginEmailNext)
                    }
                }
            })
            etLoginEmailPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
                override fun afterTextChanged(s: Editable?) {
                    if(!s.toString().isNullOrEmpty() && !etLoginEmailAddress.text.isNullOrEmpty()) {
                        ButtonActivation.setSignupButtonActive(requireContext(), binding.btnLoginEmailNext)
                    } else {
                        ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnLoginEmailNext)
                    }
                }
            })
        }
    }
}