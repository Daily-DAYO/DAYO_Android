package daily.dayo.presentation.fragment.mypage.profile

import android.app.AlertDialog
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
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import daily.dayo.presentation.R
import daily.dayo.presentation.common.ButtonActivation
import daily.dayo.presentation.common.GlideLoadUtil
import daily.dayo.presentation.common.HideKeyBoardUtil
import daily.dayo.presentation.common.ReplaceUnicode.trimBlankText
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.dialog.LoadingAlertDialog
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.image.ImageResizeUtil
import daily.dayo.presentation.common.image.ImageResizeUtil.USER_PROFILE_THUMBNAIL_RESIZE_SIZE
import daily.dayo.presentation.common.image.ImageResizeUtil.cropCenterBitmap
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentProfileEditBinding
import daily.dayo.presentation.viewmodel.AccountViewModel
import daily.dayo.presentation.viewmodel.ProfileSettingViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

class ProfileEditFragment : Fragment() {
    private var binding by autoCleared<FragmentProfileEditBinding> {
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
        onDestroyBindingView()
    }
    private val accountViewModel by activityViewModels<AccountViewModel>()
    private val profileSettingViewModel by activityViewModels<ProfileSettingViewModel>()
    private var glideRequestManager: RequestManager? = null
    private lateinit var userProfileImageString: String
    private var imagePath: String? = null
    private val imageFileTimeFormat = SimpleDateFormat("yyyy-MM-d-HH-mm-ss", Locale.KOREA)
    private lateinit var userProfileImageExtension: String
    private lateinit var loadingAlertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
        setKeyboardMode()
        initializeLoadingDialog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        setHideKeyboard()
    }

    private fun onDestroyBindingView() {
        glideRequestManager = null
    }

    private fun setKeyboardMode() {
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun setHideKeyboard() {
        requireView().setOnTouchListener { _, _ ->
            HideKeyBoardUtil.hide(requireContext(), binding.etProfileEditNickname)
            true
        }
    }

    private fun initializeLoadingDialog() {
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())
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
        binding.btnProfileEditBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initMyProfile() {
        profileSettingViewModel.requestProfile(memberId = accountViewModel.getCurrentUserInfo().memberId!!)
        profileSettingViewModel.profileInfo.observe(viewLifecycleOwner) {
            it?.let { profile ->
                glideRequestManager?.let { requestManager ->
                    GlideLoadUtil.loadImageViewProfile(
                        requestManager = requestManager,
                        width = binding.imgProfileEditUserImage.width,
                        height = binding.imgProfileEditUserImage.height,
                        imgName = profile.profileImg,
                        imgView = binding.imgProfileEditUserImage
                    )
                }
                binding.etProfileEditNickname.setText(profile.nickname)
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
                            trimBlankText(s).length
                        }${getString(R.string.my_profile_edit_nickname_edittext_count)}"
                    }

                    if (trimBlankText(s) == profileSettingViewModel.profileInfo.value?.nickname) { // 기존 닉네임과 동일한 경우
                        setEditTextTheme(
                            "",
                            true
                        )
                        ButtonActivation.setTextViewButtonActive(
                            requireContext(),
                            btnProfileEditComplete
                        )
                    } else {
                        if (trimBlankText(s).length < 2) { // 닉네임 길이 검사 1
                            setEditTextTheme(
                                getString(R.string.my_profile_edit_nickname_message_length_fail_min),
                                false
                            )
                            ButtonActivation.setTextViewButtonInactive(
                                requireContext(),
                                btnProfileEditComplete
                            )
                        } else if (trimBlankText(s).length > 10) { // 닉네임 길이 검사 2
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
                                    "^[ㄱ-ㅎ|ㅏ-ㅣ|가-힣|a-z|A-Z|0-9|]+\$",
                                    trimBlankText(s)
                                )
                            ) {
                                profileSettingViewModel.requestCheckNicknameDuplicate(
                                    trimBlankText(
                                        binding.etProfileEditNickname.text
                                    )
                                )
                                profileSettingViewModel.isNicknameDuplicate.observe(
                                    viewLifecycleOwner
                                ) { isDuplicate ->
                                    if (isDuplicate) {
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
        binding.layoutProfileEditUserImg.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.ProfileEditFragment,
                action = R.id.action_profileEditFragment_to_profileEditImageOptionFragment
            )
        }
    }

    private fun observeNavigationMyProfileImageCallBack() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("userProfileImageString")
            ?.observe(viewLifecycleOwner) {
                userProfileImageString = it
                if (this::userProfileImageString.isInitialized) {
                    if (userProfileImageString == "resetMyProfileImage") {
                        glideRequestManager?.load(R.drawable.ic_user_profile_image_empty)
                            ?.centerCrop()?.into(binding.imgProfileEditUserImage)
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            glideRequestManager?.load(userProfileImageString.toUri())?.centerCrop()
                                ?.into(binding.imgProfileEditUserImage)
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
        binding.btnProfileEditComplete.setOnDebounceClickListener {
            LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
            val nickname: String? = trimBlankText(binding.etProfileEditNickname.text)
            var profileImgFile: File?

            runBlocking {
                if (this@ProfileEditFragment::userProfileImageString.isInitialized) {
                    // 프로필 사진을 변경하는 경우
                    profileImgFile = changeMyProfileImage()
                    profileSettingViewModel.requestUpdateMyProfile(
                        nickname = nickname,
                        profileImg = profileImgFile,
                        isReset = profileImgFile == null
                    )
                        .invokeOnCompletion { throwable ->
                            when (throwable) {
                                is CancellationException -> Log.e(
                                    "My Profile Update",
                                    "CANCELLED"
                                )

                                null -> {
                                    profileSettingViewModel.requestProfile(memberId = accountViewModel.getCurrentUserInfo().memberId!!)
                                    findNavController().navigateUp()
                                }
                            }
                        }
                } else {
                    // 프로필 사진을 변경하지 않는 경우
                    profileSettingViewModel.requestUpdateMyProfile(
                        nickname = nickname,
                        profileImg = null,
                        isReset = false
                    )
                        .invokeOnCompletion { throwable ->
                            when (throwable) {
                                is CancellationException -> Log.e("My Profile Update", "CANCELLED")
                                null -> {
                                    profileSettingViewModel.requestProfile(memberId = accountViewModel.getCurrentUserInfo().memberId!!)
                                    findNavController().navigateUp()
                                }
                            }
                        }
                }
            }
        }
    }

    private fun changeMyProfileImage(): File? {
        return if (userProfileImageString == "resetMyProfileImage") { // 1. 프로필 사진 초기화를 한 경우
            null
        } else { // 2. 프로필 사진을 다른 사진으로 변경 한 경우
            setUploadImagePath(userProfileImageExtension)
            val originalBitmap = userProfileImageString.toUri().toBitmap().cropCenterBitmap()
            val resizedBitmap = ImageResizeUtil.resizeBitmap(
                originalBitmap = originalBitmap,
                resizedWidth = USER_PROFILE_THUMBNAIL_RESIZE_SIZE,
                resizedHeight = USER_PROFILE_THUMBNAIL_RESIZE_SIZE
            )
            bitmapToFile(resizedBitmap, imagePath)
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