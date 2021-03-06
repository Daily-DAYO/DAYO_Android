package com.daily.dayo.presentation.fragment.mypage.profile

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.DayoApplication
import com.daily.dayo.R
import com.daily.dayo.common.ButtonActivation
import com.daily.dayo.common.GlideLoadUtil
import com.daily.dayo.common.HideKeyBoardUtil
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentProfileEditBinding
import com.daily.dayo.presentation.viewmodel.ProfileSettingViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class ProfileEditFragment : Fragment() {
    private var binding by autoCleared<FragmentProfileEditBinding>()
    private val profileSettingViewModel by activityViewModels<ProfileSettingViewModel>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var userProfileImageString: String
    private var imagePath: String? = null
    private val imageFileTimeFormat = SimpleDateFormat("yyyy-MM-d-HH-mm-ss", Locale.KOREA)
    private lateinit var userProfileImageExtension: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)
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
            HideKeyBoardUtil.hide(requireContext(), binding.etProfileEditNickname)
            true
        }
    }

    private fun setTextEditorActionListener() {
        binding.etProfileEditNickname.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(), binding.etProfileEditNickname)
                    true
                }
                else -> false
            }
        }
    }

    private fun setBackButtonClickListener() {
        binding.btnProfileEditBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initMyProfile() {
        profileSettingViewModel.requestProfile(memberId = DayoApplication.preferences.getCurrentUser().memberId!!)
        profileSettingViewModel.profileInfo.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { profile ->
                        CoroutineScope(Dispatchers.Main).launch {
                            val userProfileThumbnailImage = withContext(Dispatchers.IO) {
                                GlideLoadUtil.loadImageBackgroundProfile(
                                    requestManager = glideRequestManager,
                                    width = 100,
                                    height = 100,
                                    imgName = profile.profileImg
                                )
                            }
                            GlideLoadUtil.loadImageViewProfile(
                                requestManager = glideRequestManager,
                                width = 100,
                                height = 100,
                                img = userProfileThumbnailImage,
                                imgView = binding.imgProfileEditUserImage
                            )
                        }
                        binding.etProfileEditNickname.setText(profile.nickname)
                    }
                }
            }
        }
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
        val lengthFilter = InputFilter.LengthFilter(10)
        binding.etProfileEditNickname.filters = arrayOf(filterInputCheck, lengthFilter)
    }

    private fun verifyNickname() {
        binding.etProfileEditNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                with(binding) {
                    if (s.toString().length == 0) {
                        tvProfileEditNicknameCount.text =
                            "0${getString(R.string.my_profile_edit_nickname_edittext_count)}"
                    } else {
                        tvProfileEditNicknameCount.text = "${
                            s.toString().trim().length
                        }${getString(R.string.my_profile_edit_nickname_edittext_count)}"
                    }

                    if (s.toString().trim().length < 2) { // ????????? ?????? ?????? 1
                        setEditTextTheme(
                            getString(R.string.my_profile_edit_nickname_message_length_fail_min),
                            false
                        )
                        ButtonActivation.setTextViewButtonInactive(
                            requireContext(),
                            btnProfileEditComplete
                        )
                    } else if (s.toString().trim().length > 10) { // ????????? ?????? ?????? 2
                        setEditTextTheme(
                            getString(R.string.my_profile_edit_nickname_message_length_fail_max),
                            false
                        )
                        ButtonActivation.setTextViewButtonInactive(
                            requireContext(),
                            btnProfileEditComplete
                        )
                    } else {
                        if (Pattern.matches(
                                "^[???-???|???-???|???-???|a-z|A-Z|0-9|]+\$",
                                s.toString().trim()
                            )
                        ) { // ????????? ?????? ??????
                            if (true) { // TODO : ????????? ???????????? ?????? ?????? ??????
                                setEditTextTheme(
                                    getString(R.string.my_profile_edit_nickname_message_success),
                                    true
                                )
                                ButtonActivation.setTextViewButtonActive(
                                    requireContext(),
                                    btnProfileEditComplete
                                )
                            } else {
                                setEditTextTheme(
                                    getString(R.string.my_profile_edit_nickname_message_duplicate_fail),
                                    false
                                )
                                ButtonActivation.setTextViewButtonInactive(
                                    requireContext(),
                                    btnProfileEditComplete
                                )
                            }
                        } else {
                            setEditTextTheme(
                                getString(R.string.my_profile_edit_nickname_message_format_fail),
                                false
                            )
                            ButtonActivation.setTextViewButtonInactive(
                                requireContext(),
                                btnProfileEditComplete
                            )
                        }
                    }
                }
            }
        })
    }

    private fun setEditTextTheme(checkMessage: String?, pass: Boolean) {
        with(binding.tvProfileEditNicknameMessage) {
            visibility = View.VISIBLE
            if (pass) {
                text = checkMessage
                setTextColor(resources.getColor(R.color.primary_green_23C882, context?.theme))
                binding.tvProfileEditNicknameMessage.backgroundTintList =
                    resources.getColorStateList(R.color.primary_green_23C882, context?.theme)
            } else {
                text = checkMessage
                setTextColor(resources.getColor(R.color.red_FF4545, context?.theme))
                binding.tvProfileEditNicknameMessage.backgroundTintList =
                    resources.getColorStateList(R.color.red_FF4545, context?.theme)
            }
        }
    }

    private fun setProfileImageOptionClickListener() {
        binding.layoutProfileEditUserImg.setOnClickListener {
            findNavController().navigate(R.id.action_profileEditFragment_to_profileEditImageOptionFragment)
        }
    }

    private fun observeNavigationMyProfileImageCallBack() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("userProfileImageString")
            ?.observe(viewLifecycleOwner) {
                userProfileImageString = it
                if (this::userProfileImageString.isInitialized) {
                    if (userProfileImageString == "resetMyProfileImage") {
                        glideRequestManager.load(R.drawable.ic_user_profile_image_empty)
                            .centerCrop().into(binding.imgProfileEditUserImage)
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            glideRequestManager.load(userProfileImageString.toUri()).centerCrop()
                                .into(binding.imgProfileEditUserImage)
                        }
                    }
                }
            }
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("fileExtension")
            ?.observe(viewLifecycleOwner) {
                userProfileImageExtension = it
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setProfileUpdateClickListener() {
        binding.btnProfileEditComplete.setOnClickListener {
            val nickname: String? = binding.etProfileEditNickname.text.toString().trim()
            var profileImgFile: File?

            runBlocking {
                if (this@ProfileEditFragment::userProfileImageString.isInitialized) {
                    // ????????? ????????? ???????????? ??????
                    profileImgFile = changeMyProfileImage()
                    profileSettingViewModel.requestUpdateMyProfile(nickname, profileImgFile)
                        .invokeOnCompletion { throwable ->
                            when (throwable) {
                                is CancellationException -> Log.e("My Profile Update", "CANCELLED")
                                null -> {
                                    profileSettingViewModel.requestProfile(memberId = DayoApplication.preferences.getCurrentUser().memberId!!)
                                    // TODO : ???????????? ????????? ????????? Dialog ??????
                                    findNavController().navigateUp()
                                }
                            }
                        }
                } else {
                    // ????????? ????????? ???????????? ?????? ??????
                    profileSettingViewModel.requestUpdateMyProfile(nickname, null)
                        .invokeOnCompletion { throwable ->
                            when (throwable) {
                                is CancellationException -> Log.e("My Profile Update", "CANCELLED")
                                null -> {
                                    profileSettingViewModel.requestProfile(memberId = DayoApplication.preferences.getCurrentUser().memberId!!)
                                    // TODO : ???????????? ????????? ????????? Dialog ??????
                                    findNavController().navigateUp()
                                }
                            }
                        }
                }
            }
        }
    }

    private fun changeMyProfileImage(): File? {
        // 1. ????????? ?????? ???????????? ??? ??????
        if (userProfileImageString == "resetMyProfileImage") {
            val profileEmptyDrawable =
                resources.getDrawable(R.drawable.ic_user_profile_image_empty, context?.theme)
            val profileEmptyBitmap = vectorDrawableToBitmapDrawable(profileEmptyDrawable)

            setUploadImagePath("png")
            return bitmapToFile(profileEmptyBitmap, imagePath)
        } else { // 2. ????????? ????????? ?????? ???????????? ?????? ??? ??????
            setUploadImagePath(userProfileImageExtension)
            return bitmapToFile(userProfileImageString.toUri().toBitmap(), imagePath)
        }
    }

    private fun setUploadImagePath(fileExtension: String) {
        // uri??? ????????? ????????? ???????????? ????????? ????????? ????????? ????????? ??? ?????? ?????? ??????????????? ??????,
        // ?????? ????????? ????????? ?????? ??????
        val fileName = imageFileTimeFormat.format(Date(System.currentTimeMillis()))
            .toString() + "." + fileExtension
        val cacheDir = requireContext().cacheDir.toString()
        imagePath = "$cacheDir/$fileName"
    }

    private fun bitmapToFile(bitmap: Bitmap?, path: String?): File? {
        if (bitmap == null || path == null) {
            return null
        }
        var file = File(path)
        var out: OutputStream? = null
        try {
            file.createNewFile()
            out = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
        } finally {
            out?.close()
        }
        return file
    }

    private fun Uri.toBitmap(): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    requireContext().contentResolver,
                    this
                )
            )
        } else {
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, this)
        }
    }

    private fun vectorDrawableToBitmapDrawable(drawable: Drawable): Bitmap? {
        return try {
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: OutOfMemoryError) {
            // TODO: Handle the error
            null
        }
    }
}