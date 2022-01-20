package com.daily.dayo.profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.daily.dayo.profile.viewmodel.MyProfileViewModel
import com.daily.dayo.util.Status
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class MyProfileEditFragment : Fragment() {
    private var binding by autoCleared<FragmentMyProfileEditBinding>()
    private val myProfileViewModel by activityViewModels<MyProfileViewModel>()
    private lateinit var userProfileImageString : String
    private var imagePath: String? = null
    private val imageFileTimeFormat = SimpleDateFormat("yyyy-MM-d-HH-mm-ss", Locale.KOREA)
    private lateinit var userProfileImageExtension : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyProfileEditBinding.inflate(inflater, container, false)
        initMyProfile()
        setBackButtonClickListener()
        textWatcherEditText()
        setProfileImageOptionClickListener()
        observeNavigationMyProfileImageCallBack()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setProfileUpdateClickListener()
        }
        return binding.root
    }

    private fun setBackButtonClickListener(){
        binding.btnMyProfileEditBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initMyProfile() {
        Glide.with(requireContext())
            .load("http://117.17.198.45:8080/images/" + myProfileViewModel.myProfile.value?.data?.profileImg)
            .centerCrop()
            .into(binding.imgMyProfileEditUserImage)
        binding.etMyProfileEditNickname.setText(myProfileViewModel.myProfile.value?.data?.nickname)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                myProfileViewModel.myProfile.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    when(it.status) {
                        Status.SUCCESS -> {
                            it.data?.let { myProfile -> {
                                Glide.with(requireContext())
                                    .load("http://117.17.198.45:8080/images/" + myProfile.profileImg)
                                    .centerCrop()
                                    .into(binding.imgMyProfileEditUserImage)
                                binding.etMyProfileEditNickname.setText(myProfile.nickname)
                                }
                            }
                        }
                    }
                })
            }
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
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("fileExtension")?.observe(viewLifecycleOwner) {
            userProfileImageExtension = it
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setProfileUpdateClickListener() {
        binding.btnMyProfileEditComplete.setOnClickListener {
            val nickname: String? = binding.etMyProfileEditNickname.text.toString().trim()

            runBlocking {
                if(this@MyProfileEditFragment::userProfileImageString.isInitialized) {
                    setUploadImagePath(userProfileImageExtension)
                    val profileImgFile = bitmapToFile(userProfileImageString.toUri().toBitmap(), imagePath)

                    myProfileViewModel.requestUpdateMyProfile(nickname, profileImgFile).invokeOnCompletion {throwable ->
                        when(throwable) {
                            is CancellationException -> Log.e("My Profile Update", "CANCELLED")
                            null -> {
                                myProfileViewModel.requestMyProfile()
                                // TODO : 변경하는 중임을 알리는 Dialog 필요
                                findNavController().navigateUp()
                            }
                        }
                    }
                } else {
                    myProfileViewModel.requestUpdateMyProfile(nickname, null).invokeOnCompletion {throwable ->
                        when(throwable) {
                            is CancellationException -> Log.e("My Profile Update", "CANCELLED")
                            null -> {
                                myProfileViewModel.requestMyProfile()
                                // TODO : 변경하는 중임을 알리는 Dialog 필요
                                findNavController().navigateUp()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setUploadImagePath(fileExtension: String) {
        // uri를 통하여 불러온 이미지를 임시로 파일로 저장할 경로로 앱 내부 캐시 디렉토리로 설정,
        // 파일 이름은 불러온 시간 사용
        val fileName = imageFileTimeFormat.format(Date(System.currentTimeMillis()))
            .toString() + "." + fileExtension
        val cacheDir = requireContext().cacheDir.toString()
        imagePath = "$cacheDir/$fileName"
    }

    fun bitmapToFile(bitmap: Bitmap?, path: String?): File? {
        if (bitmap == null || path == null) { return null }
        var file = File(path)
        var out: OutputStream? = null
        try { file.createNewFile()
            out = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
        } finally { out?.close() }
        return file
    }

    fun Uri.toBitmap(): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, this) )
        } else {
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, this)
        }
    }
}