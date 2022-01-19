package com.daily.dayo.signup.email

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
import com.daily.dayo.databinding.FragmentSignupEmailSetProfileBinding
import com.daily.dayo.util.ButtonActivation
import com.daily.dayo.util.HideKeyBoardUtil
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class SignupEmailSetProfileFragment : Fragment() {
    private var binding by autoCleared<FragmentSignupEmailSetProfileBinding>()
    private val args by navArgs<SignupEmailSetProfileFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupEmailSetProfileBinding.inflate(inflater, container, false)
        setBackClickListener()
        setNextClickListener()
        setLimitEditTextInputType()
        setTextEditorActionListener()
        verifyNickname()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ ->
            HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetProfileNickname)
            true
        }
    }
    private fun setTextEditorActionListener() {
        binding.etSignupEmailSetProfileNickname.setOnEditorActionListener {  _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetProfileNickname)
                    true
                } else -> false
            }
        }
    }

    private fun verifyNickname() {
        binding.etSignupEmailSetProfileNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                with(binding) {
                    if(s.toString().length == 0){
                        tvSignupEmailSetProfileNicknameCount.text = "0${getString(R.string.my_profile_edit_nickname_edittext_count)}"
                    } else {
                        tvSignupEmailSetProfileNicknameCount.text = "${s.toString().trim().length}${getString(R.string.my_profile_edit_nickname_edittext_count)}"
                    }

                    if(s.toString().trim().length < 2) { // 닉네임 길이 검사 1
                        setEditTextTheme(getString(R.string.my_profile_edit_nickname_message_length_fail_min), false)
                        ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetProfileNext)
                    } else if(s.toString().trim().length > 10) { // 닉네임 길이 검사 2
                        setEditTextTheme(getString(R.string.my_profile_edit_nickname_message_length_fail_max), false)
                        ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetProfileNext)
                    } else {
                        if(Pattern.matches("^[ㄱ-ㅎ|ㅏ-ㅣ|가-힣|a-z|A-Z|0-9|]+\$", s.toString().trim())) { // 닉네임 양식 검사
                            if(true) { // TODO : 닉네임 중복검사 통과 코드 작성
                                setEditTextTheme(getString(R.string.my_profile_edit_nickname_message_success), true)
                                ButtonActivation.setSignupButtonActive(requireContext(), binding.btnSignupEmailSetProfileNext)
                            } else {
                                setEditTextTheme(getString(R.string.my_profile_edit_nickname_message_duplicate_fail), false)
                                ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetProfileNext)
                            }
                        } else {
                            setEditTextTheme(getString(R.string.my_profile_edit_nickname_message_format_fail), false)
                            ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetProfileNext)
                        }
                    }
                }
            }
        })
    }

    private fun setEditTextTheme(checkMessage: String?, pass: Boolean) {
        with(binding.tvSignupEmailSetProfileNicknameMessage) {
            visibility = View.VISIBLE
            if(pass) {
                text = checkMessage
                setTextColor(resources.getColor(R.color.primary_green_23C882, context?.theme))
                binding.etSignupEmailSetProfileNickname.backgroundTintList = resources.getColorStateList(R.color.primary_green_23C882, context?.theme)
            } else {
                text = checkMessage
                setTextColor(resources.getColor(R.color.red_FF4545, context?.theme))
                binding.etSignupEmailSetProfileNickname.backgroundTintList = resources.getColorStateList(R.color.red_FF4545, context?.theme)
            }
        }
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
        val lengthFilter = InputFilter.LengthFilter(10)
        binding.etSignupEmailSetProfileNickname.filters = arrayOf(filterInputCheck, lengthFilter)
    }

    private fun setBackClickListener(){
        binding.btnSignupEmailSetProfileBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnSignupEmailSetProfileNext.setOnClickListener {
            //
        }
    }
}