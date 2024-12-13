package daily.dayo.presentation.screen.mypage

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import daily.dayo.domain.model.Profile
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Resource
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.dialog.LoadingAlertDialog.createLoadingDialog
import daily.dayo.presentation.common.dialog.LoadingAlertDialog.hideLoadingDialog
import daily.dayo.presentation.common.dialog.LoadingAlertDialog.resizeDialogFragment
import daily.dayo.presentation.common.dialog.LoadingAlertDialog.showLoadingDialog
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.BadgeRoundImageView
import daily.dayo.presentation.view.DayoTextField
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import daily.dayo.presentation.view.dialog.BottomSheetDialog
import daily.dayo.presentation.view.dialog.getBottomSheetDialogState
import daily.dayo.presentation.viewmodel.ProfileSettingViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.regex.Pattern

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun MyPageEditScreen(
    onBackClick: () -> Unit,
    profileSettingViewModel: ProfileSettingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val bottomSheetState = getBottomSheetDialogState()
    val coroutineScope = rememberCoroutineScope()
    val alertDialog = remember { mutableStateOf(createLoadingDialog(context)) }

    val profileUiState by profileSettingViewModel.profileInfo.observeAsState(Resource.loading(null))
    val isNicknameDuplicate by profileSettingViewModel.isNicknameDuplicate.collectAsStateWithLifecycle(false)
    val updateSuccess by profileSettingViewModel.updateSuccess.collectAsStateWithLifecycle(false)

    val profileInfo = remember { mutableStateOf<Profile?>(null) }
    val modifiedProfileImage = remember { mutableStateOf("") }
    val nickNameErrorMessage = remember { mutableStateOf("") }
    var showProfileGallery by remember { mutableStateOf(false) }
    var showProfileCapture by remember { mutableStateOf(false) }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            modifiedProfileImage.value = uri.toString()
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            modifiedProfileImage.value = bitmapToUri(context, bitmap).toString()
        }
    }

    LaunchedEffect(profileUiState) {
        profileInfo.value = when (profileUiState.status) {
            Status.SUCCESS -> profileUiState.data
            Status.LOADING, Status.ERROR -> null
        }
    }

    LaunchedEffect(profileInfo.value?.nickname, isNicknameDuplicate) {
        profileInfo.value?.nickname?.let { nickname ->
            profileSettingViewModel.requestCheckNicknameDuplicate(nickname)
        }

        nickNameErrorMessage.value = when {
            isNicknameDuplicate -> context.getString(R.string.my_profile_edit_nickname_message_duplicate_fail)
            else -> verifyNickname(profileInfo.value?.nickname ?: "", context)
        }
    }

    LaunchedEffect(profileInfo.value?.profileImg, modifiedProfileImage) {
        profileInfo.value?.profileImg?.let { profileImg ->
            modifiedProfileImage.value = "${BuildConfig.BASE_URL}/images/${profileImg}"
        }
    }

    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            hideLoadingDialog(alertDialog.value)
            onBackClick.invoke()
        }
    }

    if (showProfileGallery) {
        ProfileGallery(context, galleryLauncher)
        showProfileGallery = false
    }

    if (showProfileCapture) {
        ProfileCapture(context, cameraLauncher)
        showProfileCapture = false
    }

    MyPageEditScreen(
        profileInfo = profileInfo,
        bottomSheetState = bottomSheetState,
        modifiedProfileImage = modifiedProfileImage.value,
        nickNameErrorMessage = nickNameErrorMessage.value,
        onClickProfileSelect = {
            coroutineScope.launch {
                showProfileGallery = true
                bottomSheetState.hide()
            }
        },
        onClickProfileCapture = {
            coroutineScope.launch {
                showProfileCapture = true
                bottomSheetState.hide()
            }
        },
        onClickProfileReset = {
            modifiedProfileImage.value = ""
            coroutineScope.launch {
                bottomSheetState.hide()
            }
        },
        onBackClick = onBackClick,
        onConfirmClick = {
            if (profileInfo.value?.nickname != null) {
                showLoadingDialog(alertDialog.value)
                resizeDialogFragment(context, alertDialog.value, 0.8f)
                profileSettingViewModel.requestUpdateMyProfile(
                    nickname = profileInfo.value?.nickname!!,
                    profileImg = uriToFile(context, modifiedProfileImage.value),
                    isReset = modifiedProfileImage.value.isEmpty()
                )
            }
            focusManager.clearFocus()
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MyPageEditScreen(
    profileInfo: MutableState<Profile?>,
    modifiedProfileImage: String,
    nickNameErrorMessage: String,
    bottomSheetState: ModalBottomSheetState,
    onClickProfileSelect: () -> Unit,
    onClickProfileCapture: () -> Unit,
    onClickProfileReset: () -> Unit,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            MyPageEditTopNavigation(
                confirmEnabled = nickNameErrorMessage.isEmpty(),
                onBackClick = onBackClick,
                onConfirmClick = onConfirmClick
            )
        },
        bottomBar = {
            ProfileImageBottomSheetDialog(
                bottomSheetState,
                onClickProfileSelect,
                onClickProfileCapture,
                onClickProfileReset
            )
        },
        content = { contentPadding ->
            Column(
                modifier = Modifier
                    .background(DayoTheme.colorScheme.background)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(contentPadding)
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val placeholder = AppCompatResources.getDrawable(LocalContext.current, R.drawable.ic_profile_default_user_profile)
                BadgeRoundImageView(
                    context = LocalContext.current,
                    imageUrl = modifiedProfileImage,
                    imageDescription = "my page profile image",
                    placeholder = placeholder,
                    contentModifier = Modifier
                        .size(100.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(percent = 50))
                        .clickableSingle(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                coroutineScope.launch { bottomSheetState.show() }
                            }
                        )
                )

                Spacer(modifier = Modifier.height(36.dp))

                DayoTextField(
                    value = profileInfo.value?.nickname ?: "",
                    onValueChange = { textValue ->
                        profileInfo.value = profileInfo.value?.copy(
                            nickname = textValue
                        )
                    },
                    label = stringResource(id = R.string.nickname),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    isError = nickNameErrorMessage.isNotEmpty(),
                    errorMessage = nickNameErrorMessage
                )

                Spacer(modifier = Modifier.height(32.dp))

                Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                    Text(
                        text = stringResource(id = R.string.email),
                        style = DayoTheme.typography.caption3.copy(
                            color = Gray4_C5CAD2,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Text(
                        text = profileInfo.value?.email ?: "",
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = DayoTheme.typography.b4.copy(
                            color = Gray2_767B83,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                        color = Gray6_F0F1F3
                    )
                }
            }
        }
    )
}

@Composable
private fun MyPageEditTopNavigation(
    confirmEnabled: Boolean,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    TopNavigation(
        title = stringResource(id = R.string.my_profile_edit_title),
        leftIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_sign),
                    contentDescription = "back",
                    tint = Gray1_50545B
                )
            }
        },
        rightIcon = {
            Text(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .padding(start = 24.dp, end = 18.dp)
                    .clickableSingle(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = confirmEnabled,
                        onClick = onConfirmClick
                    ),
                text = stringResource(id = R.string.confirm),
                style = DayoTheme.typography.b3.copy(color = Dark),
            )
        },
        titleAlignment = TopNavigationAlign.CENTER
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ProfileImageBottomSheetDialog(
    bottomSheetState: ModalBottomSheetState,
    onClickProfileSelect: () -> Unit,
    onClickProfileCapture: () -> Unit,
    onClickProfileReset: () -> Unit,
) {
    BottomSheetDialog(
        sheetState = bottomSheetState,
        buttons = listOf(
            Pair(stringResource(id = R.string.my_profile_edit_image_select_gallery)) {
                onClickProfileSelect()
            }, Pair(stringResource(id = R.string.image_option_camera)) {
                onClickProfileCapture()
            }, Pair(stringResource(id = R.string.my_profile_edit_image_reset)) {
                onClickProfileReset()
            }),
        isFirstButtonColored = true
    )
}

@Composable
fun ProfileGallery(
    context: Context,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>
) {
    val imagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    }

    var hasImagePermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                imagePermission
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasImagePermission = isGranted
    }

    LaunchedEffect(hasImagePermission) {
        if (hasImagePermission) {
            galleryLauncher.launch("image/*")
        } else {
            permissionLauncher.launch(imagePermission)
        }
    }
}

@Composable
fun ProfileCapture(
    context: Context,
    cameraLauncher: ManagedActivityResultLauncher<Void?, Bitmap?>
) {
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(hasCameraPermission) {
        if (hasCameraPermission) {
            cameraLauncher.launch()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}

private fun verifyNickname(nickname: String, context: Context): String {
    return if (nickname.length < 2) context.getString(R.string.my_profile_edit_nickname_message_length_fail_min)
    else if (nickname.length > 10) context.getString(R.string.my_profile_edit_nickname_message_length_fail_max)
    else if (Pattern.matches("^[ㄱ-ㅎ|ㅏ-ㅣ가-힣a-zA-Z0-9]+$", nickname).not()) context.getString(R.string.my_profile_edit_nickname_message_format_fail)
    else ""
}

fun uriToFile(context: Context, uri: String): File? {
    if (uri.isEmpty()) return null
    if (uri.toUri().scheme == "http" || uri.toUri().scheme == "https") return null

    val inputStream = context.contentResolver.openInputStream(uri.toUri())
    return if (inputStream != null) {
        val tempFile = File(context.cacheDir, "profile_image_${System.currentTimeMillis()}.jpg")
        try {
            val outputStream = FileOutputStream(tempFile)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            inputStream.close()
        }
    } else {
        null
    }
}

fun bitmapToUri(context: Context, bitmap: Bitmap): Uri? {
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "profile_image_${System.currentTimeMillis()}.jpg")
    return try {
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
internal fun PreviewMyPageEditScreen() {
    DayoTheme {
        MyPageEditScreen(
            profileInfo = remember {
                mutableStateOf(
                    Profile(
                        null,
                        "princedj@gmail.com",
                        "동준왕자다요",
                        "",
                        0,
                        0,
                        0,
                        false
                    )
                )
            },
            modifiedProfileImage = "",
            nickNameErrorMessage = "",
            bottomSheetState = getBottomSheetDialogState(),
            onClickProfileSelect = {},
            onClickProfileCapture = {},
            onClickProfileReset = { },
            onBackClick = {},
            onConfirmClick = {}
        )
    }
}