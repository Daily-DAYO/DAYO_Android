package com.daily.dayo.profile

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
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
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.daily.dayo.profile.viewmodel.MyProfileViewModel
import com.daily.dayo.util.ButtonActivation
import com.daily.dayo.util.HideKeyBoardUtil
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
        setBackButtonClickListener()
        setLimitEditTextInputType()
        setTextEditorActionListener()
        verifyNickname()
        initMyProfile()
        setProfileImageOptionClickListener()
        observeNavigationMyProfileImageCallBack()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setProfileUpdateClickListener()
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ ->
            HideKeyBoardUtil.hide(requireContext(), binding.etMyProfileEditNickname)
            true
        }
    }
    private fun setTextEditorActionListener() {
        binding.etMyProfileEditNickname.setOnEditorActionListener {  _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(), binding.etMyProfileEditNickname)
                    true
                } else -> false
            }
        }
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
        binding.etMyProfileEditNickname.filters = arrayOf(filterInputCheck, lengthFilter)
    }

    private fun verifyNickname() {
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

                    if(s.toString().trim().length < 2) { // 닉네임 길이 검사 1
                        setEditTextTheme(getString(R.string.my_profile_edit_nickname_message_length_fail_min), false)
                        ButtonActivation.setTextViewButtonInactive(requireContext(), btnMyProfileEditComplete)
                    } else if(s.toString().trim().length > 10) { // 닉네임 길이 검사 2
                        setEditTextTheme(getString(R.string.my_profile_edit_nickname_message_length_fail_max), false)
                        ButtonActivation.setTextViewButtonInactive(requireContext(), btnMyProfileEditComplete)
                    } else {
                        if(Pattern.matches("^[ㄱ-ㅎ|ㅏ-ㅣ|가-힣|a-z|A-Z|0-9|]+\$", s.toString().trim())) { // 닉네임 양식 검사
                            if(true) { // TODO : 닉네임 중복검사 통과 코드 작성
                                setEditTextTheme(getString(R.string.my_profile_edit_nickname_message_success), true)
                                ButtonActivation.setTextViewButtonActive(requireContext(), btnMyProfileEditComplete)
                            } else {
                                setEditTextTheme(getString(R.string.my_profile_edit_nickname_message_duplicate_fail), false)
                                ButtonActivation.setTextViewButtonInactive(requireContext(), btnMyProfileEditComplete)
                            }
                        } else {
                            setEditTextTheme(getString(R.string.my_profile_edit_nickname_message_format_fail), false)
                            ButtonActivation.setTextViewButtonInactive(requireContext(), btnMyProfileEditComplete)
                        }
                    }
                }
            }
        })
    }

    private fun setEditTextTheme(checkMessage: String?, pass: Boolean) {
        with(binding.tvProfileEditNicknameMessage) {
            visibility = View.VISIBLE
            if(pass) {
                text = checkMessage
                setTextColor(resources.getColor(R.color.primary_green_23C882, context?.theme))
                binding.tvProfileEditNicknameMessage.backgroundTintList = resources.getColorStateList(R.color.primary_green_23C882, context?.theme)
            } else {
                text = checkMessage
                setTextColor(resources.getColor(R.color.red_FF4545, context?.theme))
                binding.tvProfileEditNicknameMessage.backgroundTintList = resources.getColorStateList(R.color.red_FF4545, context?.theme)
            }
        }
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
            var profileImgFile: File?

            runBlocking {
                if(this@MyProfileEditFragment::userProfileImageString.isInitialized) {
                    // 프로필 사진을 변경하는 경우
                    profileImgFile = changeMyProfileImage()
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
                    // 프로필 사진을 변경하지 않는 경우
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

    private fun changeMyProfileImage() : File? {
        // 1. 프로필 사진 초기화를 한 경우
        if(userProfileImageString == "resetMyProfileImage"){
            val profileEmptyDrawable = resources.getDrawable(R.drawable.ic_user_profile_image_empty, context?.theme)
            val profileEmptyBitmap = vectorDrawableToBitmapDrawable(profileEmptyDrawable)

            setUploadImagePath("png")
            return bitmapToFile(profileEmptyBitmap, imagePath)
        } else { // 2. 프로필 사진을 다른 사진으로 변경 한 경우
            setUploadImagePath(userProfileImageExtension)
            return bitmapToFile(userProfileImageString.toUri().toBitmap(), imagePath)
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

    private fun vectorDrawableToBitmapDrawable(drawable: Drawable): Bitmap? {
        try {
            val bitmap: Bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        } catch (e: OutOfMemoryError) {
            // TODO: Handle the error
            return null
        }
    }
}