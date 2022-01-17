package com.daily.dayo.signup.email

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentSignupEmailSetProfileBinding
import com.daily.dayo.util.ButtonActivation
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class SignupEmailSetProfileFragment : Fragment() {
    private var binding by autoCleared<FragmentSignupEmailSetProfileBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupEmailSetProfileBinding.inflate(inflater, container, false)
        setBackClickListener()
        setNextClickListener()
        textWatcherEditText()
        return binding.root
    }

    private fun textWatcherEditText() {
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

                    if(s.toString().trim().length < 2) { // 닉네임 길이 검사
                        tvSignupEmailSetProfileNicknameMessage.visibility = View.VISIBLE
                        tvSignupEmailSetProfileNicknameMessage.text = getString(R.string.my_profile_edit_nickname_message_length_fail_min)
                        tvSignupEmailSetProfileNicknameMessage.setTextColor(resources.getColor(R.color.red_FF4545, context?.theme))
                        etSignupEmailSetProfileNickname.backgroundTintList = resources.getColorStateList(R.color.red_FF4545, context?.theme)
                        ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetProfileNext)
                    } else if(s.toString().trim().length > 10) {
                        tvSignupEmailSetProfileNicknameMessage.visibility = View.VISIBLE
                        tvSignupEmailSetProfileNicknameMessage.text = getString(R.string.my_profile_edit_nickname_message_length_fail_max)
                        tvSignupEmailSetProfileNicknameMessage.setTextColor(resources.getColor(R.color.red_FF4545, context?.theme))
                        etSignupEmailSetProfileNickname.backgroundTintList = resources.getColorStateList(R.color.red_FF4545, context?.theme)
                        ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetProfileNext)
                    } else {
                        if(Pattern.matches("^[ㄱ-ㅎ|ㅏ-ㅣ|가-힣|a-z|A-Z|0-9|]+\$", s.toString().trim())) { // 닉네임 양식 검사
                            if(true) { // TODO : 닉네임 중복검사 통과
                                tvSignupEmailSetProfileNicknameMessage.visibility = View.INVISIBLE
                                tvSignupEmailSetProfileNicknameMessage.text = getString(R.string.my_profile_edit_nickname_message_success)
                                tvSignupEmailSetProfileNicknameMessage.setTextColor(resources.getColor(R.color.primary_green_23C882, context?.theme))
                                etSignupEmailSetProfileNickname.backgroundTintList = resources.getColorStateList(R.color.primary_green_23C882, context?.theme)
                                ButtonActivation.setSignupButtonActive(requireContext(), binding.btnSignupEmailSetProfileNext)
                            } else {
                                tvSignupEmailSetProfileNicknameMessage.visibility = View.VISIBLE
                                tvSignupEmailSetProfileNicknameMessage.text = getString(R.string.my_profile_edit_nickname_message_duplicate_fail)
                                tvSignupEmailSetProfileNicknameMessage.setTextColor(resources.getColor(R.color.red_FF4545, context?.theme))
                                etSignupEmailSetProfileNickname.backgroundTintList = resources.getColorStateList(R.color.red_FF4545, context?.theme)
                                ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetProfileNext)
                            }
                        } else {
                            tvSignupEmailSetProfileNicknameMessage.visibility = View.VISIBLE
                            tvSignupEmailSetProfileNicknameMessage.text = getString(R.string.my_profile_edit_nickname_message_format_fail)
                            tvSignupEmailSetProfileNicknameMessage.setTextColor(resources.getColor(R.color.red_FF4545, context?.theme))
                            etSignupEmailSetProfileNickname.backgroundTintList = resources.getColorStateList(R.color.red_FF4545, context?.theme)
                            ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSignupEmailSetProfileNext)
                        }
                    }
                }
            }
        })
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