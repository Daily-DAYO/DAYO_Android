package com.daily.dayo.profile

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentMyProfileEditBinding
import com.daily.dayo.util.autoCleared
import android.text.InputFilter
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import java.util.*
import java.util.regex.Pattern

class MyProfileEditFragment : Fragment() {
    private var binding by autoCleared<FragmentMyProfileEditBinding>()
    private lateinit var userProfileImageString : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyProfileEditBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        textWatcherEditText()
        setProfileImageOptionClickListener()
        observeNavigationMyProfileImageCallBack()
        return binding.root
    }

    private fun setBackButtonClickListener(){
        binding.btnMyProfileEditBack.setOnClickListener {
            findNavController().navigateUp()
        }
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
        binding.etMyProfileEditNickname.filters = arrayOf(filterInputCheck, lengthFilter)

        binding.etMyProfileEditNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                with(binding) {
                    if(s.toString().length == 0){
                        tvMyProfileEditNicknameCount.text = "0${getString(R.string.my_profile_edit_nickname_edittext_count)}"
                    } else {
                        tvMyProfileEditNicknameCount.text = "${s.toString().trim().length}${getString(R.string.my_profile_edit_nickname_edittext_count)}"
                    }

                    if(s.toString().trim().length >= 2) {
                        if(Pattern.matches("^[ㄱ-ㅎ|ㅏ-ㅣ|가-힣|a-z|A-Z|0-9|]+\$", s.toString().trim())) {
                            tvProfileEditNicknameMessage.visibility = View.INVISIBLE
                            btnMyProfileEditComplete.setTextColor(resources.getColorStateList(R.color.primary_green_23C882, context?.theme))
                            btnMyProfileEditComplete.isEnabled = true
                        } else {
                            tvProfileEditNicknameMessage.visibility = View.VISIBLE
                            tvProfileEditNicknameMessage.text = getString(R.string.my_profile_edit_nickname_message_format_fail)
                            btnMyProfileEditComplete.setTextColor(resources.getColorStateList(R.color.gray_4_D3D2D2, context?.theme))
                            btnMyProfileEditComplete.isEnabled = false
                        }
                    } else {
                        tvProfileEditNicknameMessage.visibility = View.VISIBLE
                        tvProfileEditNicknameMessage.text = getString(R.string.my_profile_edit_nickname_message_length_fail_min)
                        btnMyProfileEditComplete.setTextColor(resources.getColorStateList(R.color.gray_4_D3D2D2, context?.theme))
                        btnMyProfileEditComplete.isEnabled = false
                    }
                }
            }
        })
    }

    private fun setProfileImageOptionClickListener() {
        binding.layoutMyProfileEditUserImg.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileEditFragment_to_myProfileEditImageOptionFragment)
        }
    }

    private fun observeNavigationMyProfileImageCallBack() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("userProfileImageString")?.observe(viewLifecycleOwner) {
            userProfileImageString = it
            if(this::userProfileImageString.isInitialized){
                if(userProfileImageString == "resetMyProfileImage") {
                    Glide.with(requireContext())
                        .load(R.drawable.ic_user_profile_image_empty)
                        .centerCrop()
                        .into(binding.imgMyProfileEditUserImage)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Glide.with(requireContext())
                            .load(userProfileImageString.toUri())
                            .centerCrop()
                            .into(binding.imgMyProfileEditUserImage)
                    }
                }
            }
        }
    }
}