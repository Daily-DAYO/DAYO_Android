package com.daily.dayo.presentation.fragment.account.signup

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.R
import com.daily.dayo.common.*
import com.daily.dayo.common.dialog.LoadingAlertDialog
import com.daily.dayo.databinding.FragmentSignupEmailSetProfileBinding
import com.daily.dayo.presentation.viewmodel.AccountViewModel
import com.daily.dayo.presentation.viewmodel.ProfileSettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

@AndroidEntryPoint
class SignupEmailSetProfileFragment : Fragment() {
    private var binding by autoCleared<FragmentSignupEmailSetProfileBinding>()
    private val loginViewModel by activityViewModels<AccountViewModel>()
    private val profileSettingViewModel by viewModels<ProfileSettingViewModel>()
    private val args by navArgs<SignupEmailSetProfileFragmentArgs>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var userProfileImageString: String
    private var imagePath: String? = null
    private val imageFileTimeFormat = SimpleDateFormat("yyyy-MM-d-HH-mm-ss", Locale.KOREA)
    private lateinit var userProfileImageExtension: String
    private lateinit var loadingAlertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

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
        setProfilePhotoClickListener()
        observeNavigationMyProfileImageCallBack()
        observeKakaoSignupCallback()
        observeSignupCallback()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ ->
            HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetProfileNickname)
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }

    private fun setTextEditorActionListener() {
        binding.etSignupEmailSetProfileNickname.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(), binding.etSignupEmailSetProfileNickname)
                    true
                }
                else -> false
            }
        }
    }

    private fun verifyNickname() {
        binding.etSignupEmailSetProfileNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                with(binding) {
                    if (s.toString().trim().length < 2) { // 닉네임 길이 검사 1
                        setEditTextTheme(
                            getString(R.string.my_profile_edit_nickname_message_length_fail_min),
                            false
                        )
                        ButtonActivation.setSignupButtonInactive(
                            requireContext(),
                            binding.btnSignupEmailSetProfileNext
                        )
                    } else if (s.toString().trim().length > 10) { // 닉네임 길이 검사 2
                        setEditTextTheme(
                            getString(R.string.my_profile_edit_nickname_message_length_fail_max),
                            false
                        )
                        ButtonActivation.setSignupButtonInactive(
                            requireContext(),
                            binding.btnSignupEmailSetProfileNext
                        )
                    } else {
                        if (Pattern.matches(
                                "^[ㄱ-ㅎ|ㅏ-ㅣ|가-힣|a-z|A-Z|0-9|]+\$",
                                s.toString().trim()
                            )
                        ) { // 닉네임 양식 검사
                            if (true) { // TODO : 닉네임 중복검사 통과 코드 작성
                                setEditTextTheme(
                                    getString(R.string.my_profile_edit_nickname_message_success),
                                    true
                                )
                                ButtonActivation.setSignupButtonActive(
                                    requireContext(),
                                    binding.btnSignupEmailSetProfileNext
                                )
                            } else {
                                setEditTextTheme(
                                    getString(R.string.my_profile_edit_nickname_message_duplicate_fail),
                                    false
                                )
                                ButtonActivation.setSignupButtonInactive(
                                    requireContext(),
                                    binding.btnSignupEmailSetProfileNext
                                )
                            }
                        } else {
                            setEditTextTheme(
                                getString(R.string.my_profile_edit_nickname_message_format_fail),
                                false
                            )
                            ButtonActivation.setSignupButtonInactive(
                                requireContext(),
                                binding.btnSignupEmailSetProfileNext
                            )
                        }
                    }
                }
            }
        })
    }

    private fun setEditTextTheme(checkMessage: String?, pass: Boolean) {
        with(binding.tvSignupEmailSetProfileNicknameMessage) {
            visibility = View.VISIBLE
            if (pass) {
                text = checkMessage
                setTextColor(resources.getColor(R.color.primary_green_23C882, context?.theme))
                binding.etSignupEmailSetProfileNickname.backgroundTintList =
                    resources.getColorStateList(R.color.primary_green_23C882, context?.theme)
            } else {
                text = checkMessage
                setTextColor(resources.getColor(R.color.red_FF4545, context?.theme))
                binding.etSignupEmailSetProfileNickname.backgroundTintList =
                    resources.getColorStateList(R.color.red_FF4545, context?.theme)
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

    private fun setProfilePhotoClickListener() {
        binding.layoutSignupEmailSetProfileUserImg.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_signupEmailSetProfileFragment_to_signupEmailSetProfileImageOptionFragment)
        }
    }

    private fun observeNavigationMyProfileImageCallBack() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("userProfileImageString")
            ?.observe(viewLifecycleOwner) {
                userProfileImageString = it
                if (this::userProfileImageString.isInitialized) {
                    if (userProfileImageString == "resetMyProfileImage") {
                        glideRequestManager.load(R.drawable.ic_user_profile_image_empty)
                            .centerCrop().into(binding.imgSignupEmailSetProfileUserImage)
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            glideRequestManager.load(userProfileImageString.toUri()).centerCrop()
                                .into(binding.imgSignupEmailSetProfileUserImage)
                        }
                    }
                }
            }
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("fileExtension")
            ?.observe(viewLifecycleOwner) {
                userProfileImageExtension = it
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

    fun Uri.toBitmap(): Bitmap {
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
        try {
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        } catch (e: OutOfMemoryError) {
            // TODO: Handle the error
            return null
        }
    }

    private fun setBackClickListener() {
        binding.btnSignupEmailSetProfileBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnSignupEmailSetProfileNext.setOnDebounceClickListener {
            LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
            var profileImgFile: File? = null
            profileImgFile = if (this::userProfileImageString.isInitialized) {
                setUploadImagePath(userProfileImageExtension)
                val resizedBitmap = ImageResizeUtil.resizeBitmap(
                    originalBitmap = userProfileImageString.toUri().toBitmap(),
                    resizedWidth = 100,
                    resizedHeight = 100
                )
                bitmapToFile(resizedBitmap, imagePath)
            } else { // 기본 프로필 사진으로 설정
                null
            }
            if (args.password != null) {
                loginViewModel.requestSignupEmail(
                    args.email,
                    binding.etSignupEmailSetProfileNickname.text.toString().trim(),
                    args.password!!,
                    profileImgFile
                )
            } else {
                // 카카오 계정 회원가입 시 비밀번호가 null
                profileSettingViewModel.requestUpdateMyProfile(
                    binding.etSignupEmailSetProfileNickname.text.toString().trim(), profileImgFile, profileImgFile == null)
            }
            Toast.makeText(
                requireContext(),
                R.string.signup_email_alert_message_loading,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun observeSignupCallback() {
        loginViewModel.signupSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                Navigation.findNavController(requireView()).navigate(
                    SignupEmailSetProfileFragmentDirections.actionSignupEmailSetProfileFragmentToSignupEmailCompleteFragment(
                        binding.etSignupEmailSetProfileNickname.text.toString().trim()
                    )
                )
            } else if (isSuccess.getContentIfNotHandled() == false) {
                LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
                Toast.makeText(
                    requireContext(),
                    R.string.signup_email_alert_message_fail_network,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun observeKakaoSignupCallback() {
        profileSettingViewModel.updateSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                Navigation.findNavController(requireView()).navigate(
                    SignupEmailSetProfileFragmentDirections.actionSignupEmailSetProfileFragmentToSignupEmailCompleteFragment(
                        binding.etSignupEmailSetProfileNickname.text.toString().trim()
                    )
                )
            } else if (isSuccess.getContentIfNotHandled() == false) {
                LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
                Toast.makeText(
                    requireContext(),
                    R.string.signup_email_alert_message_fail_network,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}