package daily.dayo.presentation.screen.account

import BottomSheetController
import LocalBottomSheetController
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import daily.dayo.domain.model.WithdrawalReason
import daily.dayo.presentation.R
import daily.dayo.presentation.activity.LoginActivity
import daily.dayo.presentation.activity.MainActivity
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.PrimaryL1_8FD9B9
import daily.dayo.presentation.theme.PrimaryL3_F2FBF7
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.FilledRoundedCornerButton
import daily.dayo.presentation.view.Loading
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.dialog.ConfirmDialog
import daily.dayo.presentation.viewmodel.AccountViewModel

const val OTHER_REASON_TEXT_MAX_LENGTH = 100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawScreen(
    onBackClick: () -> Unit,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    accountViewModel: AccountViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val withdrawStep = remember { mutableStateOf(WithdrawStep.REASON_SELECT) }
    var selectedReason by remember { mutableStateOf<SelectedWithdrawReason?>(null) }

    val bottomSheetController = LocalBottomSheetController.current
    val onClickWithdrawContinue: (WithdrawalReason) -> Unit = { reason ->
        bottomSheetController.show()
        selectedReason = SelectedWithdrawReason(reason)
    }
    val bottomSheetContent: @Composable () -> Unit = remember {
        {
            selectedReason?.let { selected ->
                WithdrawContinueBottomSheetDialog(
                    bottomSheetController = bottomSheetController,
                    keyboardController = keyboardController,
                    focusManager = focusManager,
                    setWithdrawStep = { withdrawStep.value = it },
                    selectedReason = selected,
                    setSelectedReason = { selectedReason = it },
                    snackBarHostState = snackBarHostState,
                    accountViewModel = accountViewModel,
                    onNavigateToHome = onNavigateToHome,
                    onNavigateToMyPage = onNavigateToMyPage
                )
            }
        }
    }

    DisposableEffect(Unit) {
        bottomSheetController.setContent(bottomSheetContent)
        onDispose {
            keyboardController?.hide()
            focusManager.clearFocus()
            bottomSheetController.hide()
        }
    }

    LaunchedEffect(bottomSheetController) {
        if (!bottomSheetController.isVisible) {
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
            if (withdrawStep.value == WithdrawStep.REASON_SELECT) {
                selectedReason = null
            }
        }
    }

    val onBackClickInWithdraw = {
        when (withdrawStep.value) {
            WithdrawStep.REASON_SELECT -> {
                onBackClick()
            }

            WithdrawStep.CONFIRM -> {
                withdrawStep.value = WithdrawStep.REASON_SELECT
                selectedReason = null
            }
        }
    }
    val showWithdrawDialog = remember { mutableStateOf(false) }
    val withdrawSuccess by accountViewModel.withdrawSuccess.collectAsStateWithLifecycle()

    BackHandler {
        if (withdrawStep.value == WithdrawStep.CONFIRM) {
            withdrawStep.value = WithdrawStep.REASON_SELECT
            selectedReason = null
        } else {
            onBackClick()
        }
    }

    LaunchedEffect(withdrawSuccess) {
        if (withdrawSuccess == Status.SUCCESS) {
            accountViewModel.clearCurrentUser()
            val intent = Intent(context, LoginActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            (context as? MainActivity)?.let {
                it.startActivity(intent)
                it.finish()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (withdrawStep.value) {
            WithdrawStep.REASON_SELECT -> {
                WithdrawReasonList(
                    onBackClick = onBackClickInWithdraw,
                    onShowBottomSheet = { reason ->
                        selectedReason = SelectedWithdrawReason(reason)
                        onClickWithdrawContinue(reason)
                    }
                )
            }

            WithdrawStep.CONFIRM -> {
                WithdrawConfirmScreen(
                    context = context,
                    onBackClick = onBackClickInWithdraw,
                    showWithdrawDialog = showWithdrawDialog,
                )

                if (showWithdrawDialog.value) {
                    val withDrawReason = if (selectedReason?.reason == WithdrawalReason.OTHER) {
                        selectedReason?.otherReasonText
                    } else {
                        selectedReason?.reason?.content?.reasonTextResId?.let {
                            stringResource(it)
                        }
                    }
                    WithdrawDialog(
                        onConfirmClick = {
                            accountViewModel.requestWithdraw(content = withDrawReason ?: "")
                            showWithdrawDialog.value = false
                        },
                        onDismissClick = { showWithdrawDialog.value = false },
                    )
                }

                Loading(
                    isVisible = (withdrawSuccess == Status.LOADING || withdrawSuccess == Status.SUCCESS),
                )
            }
        }
    }
}

@Preview
@Composable
fun WithdrawActionbarLayout(
    onBackClick: () -> Unit = {},
) {
    TopNavigation(
        leftIcon = {
            NoRippleIconButton(
                onClick = { onBackClick() },
                iconContentDescription = stringResource(R.string.back_sign),
                iconPainter = painterResource(id = R.drawable.ic_arrow_left),
            )
        },
    )
}

@Composable
fun WithdrawReasonList(
    onBackClick: () -> Unit = {},
    onShowBottomSheet: (WithdrawalReason) -> Unit
) {
    Scaffold(
        topBar = { WithdrawActionbarLayout(onBackClick = onBackClick) },
        modifier = Modifier
            .fillMaxSize()
            .background(DayoTheme.colorScheme.background)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(top = 4.dp, start = 20.dp, end = 20.dp, bottom = 8.dp)
                .background(DayoTheme.colorScheme.background)
        ) {
            Text(
                text = stringResource(id = R.string.withdraw_reason_title),
                style = DayoTheme.typography.h4.copy(
                    color = Dark,
                    fontWeight = FontWeight.SemiBold
                ),
            )
            Spacer(modifier = Modifier.height(20.dp))
            enumValues<WithdrawalReason>().forEach { reason ->
                WithdrawReasonItem(
                    reason = reason,
                    onClickWithdraw = { onShowBottomSheet(reason) }
                )
            }
        }
    }
}

@Composable
private fun WithdrawReasonItem(
    reason: WithdrawalReason,
    onClickWithdraw: (reason: WithdrawalReason) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(start = 8.dp)
            .fillMaxWidth()
            .height(44.dp)
            .clickable { onClickWithdraw(reason) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = reason.content.reasonTextResId),
            style = DayoTheme.typography.b4.copy(
                color = Gray1_50545B,
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_r_3),
            contentDescription = stringResource(id = reason.content.reasonTextResId),
            tint = Gray4_C5CAD2
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawContinueBottomSheetDialog(
    bottomSheetController: BottomSheetController,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager,
    setWithdrawStep: (WithdrawStep) -> Unit,
    selectedReason: SelectedWithdrawReason?,
    setSelectedReason: (SelectedWithdrawReason?) -> Unit,
    snackBarHostState: SnackbarHostState,
    accountViewModel: AccountViewModel,
    onNavigateToHome: () -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
) {
    val recordWords by accountViewModel.recordGuideWords.collectAsStateWithLifecycle()
    val followWords by accountViewModel.followGuideWords.collectAsStateWithLifecycle()
    val guideImages by accountViewModel.guideImages.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .imePadding()
            .padding(bottom = 0.dp),
        shape = RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp),
        color = White_FFFFFF
    ) {
        selectedReason?.let { selected ->
            Column(
                modifier = Modifier
                    .padding(bottom = 0.dp)
                    .imePadding()
            ) {
                WithdrawHoldBottomSheet(
                    reason = selected.reason,
                    otherReasonText = selected.otherReasonText,
                    setOtherReasonText = { newText ->
                        setSelectedReason(
                            selected.copy(otherReasonText = newText)
                        )
                    },
                    recordWords = recordWords,
                    followWords = followWords,
                    guideImages = guideImages,
                    onRequestWords = { withdrawalReason ->
                        accountViewModel.requestWithdrawGuideWords(
                            withdrawalReason
                        )
                    },
                    onRequestImage = { fileName, withdrawalReason ->
                        accountViewModel.requestWithdrawGuideImage(
                            fileName,
                            withdrawalReason
                        )
                    },
                    onClearImages = { accountViewModel.clearGuideImages() },
                    onConfirm = {
                        when (selected.reason) {
                            WithdrawalReason.OTHER -> {
                                if (selected.otherReasonText.isBlank()) {
                                    return@WithdrawHoldBottomSheet
                                } else {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }
                            }

                            else -> {

                            }
                        }
                        setWithdrawStep(WithdrawStep.CONFIRM)
                        bottomSheetController.hide()
                    },
                    onCancel = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        bottomSheetController.hide()
                        when (selected.reason) {
                            WithdrawalReason.WANT_TO_DELETE_HISTORY -> {
                                onNavigateToMyPage()
                            }

                            WithdrawalReason.CONTENT_NOT_SATISFYING -> {
                                onNavigateToHome()
                            }

                            else -> { /* DO NOTHING */
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun WithdrawHoldBottomSheet(
    reason: WithdrawalReason,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    setOtherReasonText: (String) -> Unit = {},
    otherReasonText: String = "",
    recordWords: List<String> = emptyList(),
    followWords: List<String> = emptyList(),
    guideImages: Map<String, ByteArray> = emptyMap(),
    onRequestWords: (WithdrawalReason) -> Unit = {},
    onRequestImage: (String, WithdrawalReason) -> Unit = { _, _ -> },
    onClearImages: () -> Unit = {},
) {
    val content = reason.content
    val hasWithdrawReasonGuide = reason in listOf(
        WithdrawalReason.WANT_TO_DELETE_HISTORY,
        WithdrawalReason.CONTENT_NOT_SATISFYING
    )
    val isOtherReason = (content.reasonTextResId == R.string.withdraw_reason_other)
    var otherReasonContentValue by remember { mutableStateOf(TextFieldValue(otherReasonText)) }

    // otherReasonText가 변경될 때 로컬 상태도 동기화
    LaunchedEffect(otherReasonText) {
        if (otherReasonContentValue.text != otherReasonText) {
            otherReasonContentValue = TextFieldValue(otherReasonText)
        }
    }

    Column(
        modifier = modifier
            .padding(top = 20.dp, bottom = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 8.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = 8.dp)
            ) {

                Text(
                    text = stringResource(id = content.titleResId),
                    style = DayoTheme.typography.b1,
                    color = Dark,
                )

                val descriptionText = stringResource(id = content.descriptionResId)
                if (descriptionText.isNotBlank()) {
                    Spacer(
                        modifier = Modifier.height(
                            if (isOtherReason) 2.dp else 4.dp
                        )
                    )
                    Text(
                        text = descriptionText,
                        style = DayoTheme.typography.caption2.copy(
                            color = Gray2_767B83,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            Spacer(
                modifier = Modifier.height(
                    if (isOtherReason || hasWithdrawReasonGuide) 8.dp
                    else 12.dp
                )
            )

            if (hasWithdrawReasonGuide) {
                WithdrawGuideContentUI(
                    reason = reason,
                    recordWords = recordWords,
                    followWords = followWords,
                    guideImages = guideImages,
                    onRequestWords = onRequestWords,
                    onRequestImage = onRequestImage,
                    onClearImages = onClearImages
                )
            }

            if (isOtherReason) {
                val focusRequester = remember { FocusRequester() }

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }

                BasicTextField(
                    value = otherReasonContentValue,
                    onValueChange = {
                        if (it.text.length > OTHER_REASON_TEXT_MAX_LENGTH) return@BasicTextField
                        otherReasonContentValue = it
                        setOtherReasonText(it.text)
                    },
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .focusRequester(focusRequester)
                        .background(
                            color = Color(0xFFF6F6F7),
                            shape = RoundedCornerShape(size = 8.dp)
                        )
                        .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp),
                    textStyle = DayoTheme.typography.b6.copy(
                        color = Dark,
                    ),
                    decorationBox = { innerTextField ->
                        if (otherReasonContentValue.text.isEmpty()) {
                            Text(
                                text = stringResource(R.string.withdraw_reason_other_hint),
                                style = DayoTheme.typography.b6.copy(
                                    color = Gray4_C5CAD2,
                                )
                            )
                        }
                        innerTextField()
                    },
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            Row(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 4.dp)
                    .fillMaxWidth()
            ) {
                FilledRoundedCornerButton(
                    onClick = onCancel,
                    label = stringResource(id = content.cancelButtonTextResId),
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 52.dp),
                    color = ButtonDefaults.buttonColors(
                        containerColor = PrimaryL3_F2FBF7,
                        contentColor = Primary_23C882
                    ),
                    textStyle = DayoTheme.typography.b3,
                    radius = 16,
                )

                Spacer(modifier = Modifier.width(8.dp))

                FilledRoundedCornerButton(
                    onClick = onConfirm,
                    label = stringResource(id = content.confirmButtonTextResId),
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 52.dp),
                    enabled = !(reason == WithdrawalReason.OTHER && otherReasonText.isBlank()),
                    color = ButtonDefaults.buttonColors(
                        containerColor = Primary_23C882,
                        contentColor = White_FFFFFF,
                        disabledContainerColor = PrimaryL1_8FD9B9,
                        disabledContentColor = White_FFFFFF,
                    ),
                    textStyle = DayoTheme.typography.b3,
                    radius = 16,
                )
            }
        }
    }
}

@Composable
fun WithdrawConfirmScreen(
    context: Context = LocalContext.current,
    onBackClick: () -> Unit = {},
    showWithdrawDialog: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    val withdrawCheckLists =
        remember { context.resources.getStringArray(R.array.withdraw_confirm_check_list) }

    Scaffold(
        topBar = {
            WithdrawActionbarLayout(onBackClick = onBackClick)
        },
        modifier = Modifier
            .fillMaxSize()
            .background(DayoTheme.colorScheme.background)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(DayoTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 4.dp, start = 20.dp, end = 20.dp, bottom = 8.dp)
                    .background(DayoTheme.colorScheme.background)
            ) {
                Text(
                    text = stringResource(id = R.string.withdraw_confirm_title),
                    style = DayoTheme.typography.h4.copy(
                        color = Dark,
                        fontWeight = FontWeight.SemiBold
                    ),
                )
                Spacer(modifier = Modifier.height(20.dp))
                withdrawCheckLists.forEachIndexed { index, text ->
                    WithdrawConfirmCheckItems(checkText = text)
                    if (index != withdrawCheckLists.lastIndex) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            WithdrawButton(
                onWithDrawClick = {
                    showWithdrawDialog.value = true
                }
            )
        }
    }
}

@Composable
@Preview
fun WithdrawDialog(
    onConfirmClick: () -> Unit = {},
    onDismissClick: () -> Unit = {}
) {
    ConfirmDialog(
        title = stringResource(R.string.withdraw_final_message),
        description = "",
        onClickConfirmText = stringResource(R.string.withdraw_final_confirm),
        onClickConfirm = onConfirmClick,
        onClickCancelText = stringResource(R.string.cancel),
        onClickCancel = onDismissClick,
    )
}

@Composable
@Preview
fun WithdrawButton(
    onWithDrawClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 20.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledRoundedCornerButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            label = stringResource(R.string.withdraw_confirm),
            color = ButtonDefaults.buttonColors(
                containerColor = Primary_23C882,
                contentColor = White_FFFFFF,
                disabledContainerColor = PrimaryL1_8FD9B9,
                disabledContentColor = White_FFFFFF,
            ),
            textStyle = DayoTheme.typography.b3,
            onClick = { onWithDrawClick() },
        )
    }
}

@Composable
fun WithdrawConfirmCheckItems(
    checkText: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_check),
            contentDescription = null,
            tint = Primary_23C882,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = checkText,
            style = DayoTheme.typography.b6.copy(
                color = Gray1_50545B,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun WithdrawGuideContentUI(
    reason: WithdrawalReason,
    recordWords: List<String>,
    followWords: List<String>,
    guideImages: Map<String, ByteArray>,
    onRequestWords: (WithdrawalReason) -> Unit,
    onRequestImage: (String, WithdrawalReason) -> Unit,
    onClearImages: () -> Unit
) {
    val words = when (reason) {
        WithdrawalReason.WANT_TO_DELETE_HISTORY -> recordWords
        WithdrawalReason.CONTENT_NOT_SATISFYING -> followWords
        else -> emptyList()
    }

    LaunchedEffect(reason) {
        when (reason) {
            WithdrawalReason.WANT_TO_DELETE_HISTORY -> {
                onRequestWords(reason)
                onClearImages()
                repeat(4) { index ->
                    onRequestImage("record${index + 1}.png", reason)
                }
            }

            WithdrawalReason.CONTENT_NOT_SATISFYING -> {
                onRequestWords(reason)
                onClearImages()
                repeat(4) { index ->
                    onRequestImage("follow${index + 1}.png", reason)
                }
            }

            else -> {
                onClearImages()
            }
        }
    }
    Column(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            val availableImages = (1..4).mapNotNull { index ->
                val fileName = when (reason) {
                    WithdrawalReason.WANT_TO_DELETE_HISTORY -> "record${index}.png"
                    WithdrawalReason.CONTENT_NOT_SATISFYING -> "follow${index}.png"
                    else -> return@mapNotNull null
                }
                if (guideImages.containsKey(fileName)) index else null
            }

            if (availableImages.isNotEmpty()) {
                val pagerState = rememberPagerState(pageCount = { availableImages.size })

                HorizontalPager(state = pagerState) { pageIndex ->
                    val imageIndex = availableImages[pageIndex]
                    val fileName = when (reason) {
                        WithdrawalReason.WANT_TO_DELETE_HISTORY -> "record${imageIndex}.png"
                        WithdrawalReason.CONTENT_NOT_SATISFYING -> "follow${imageIndex}.png"
                        else -> ""
                    }
                    WithDrawGuideImage(
                        reasonTextResId = reason.content.reasonTextResId,
                        index = imageIndex,
                        imageData = guideImages[fileName]
                    )
                }

                if (availableImages.size > 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 9.dp,
                            alignment = Alignment.CenterHorizontally
                        ),
                    ) {
                        repeat(availableImages.size) { iteration ->
                            val color =
                                if (pagerState.currentPage == iteration) Dark else Gray4_C5CAD2
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(color)
                                    .width(6.dp)
                                    .height(6.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        val guideStrings = words.ifEmpty { emptyList() }

        Row(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .wrapContentHeight()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            guideStrings.forEachIndexed { index, guide ->
                Text(
                    text = guide,
                    color = Gray1_50545B,
                    textAlign = TextAlign.Center,
                    style = DayoTheme.typography.caption4
                )
                if (index != guideStrings.lastIndex) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right),
                        contentDescription = null,
                        tint = Gray3_9FA5AE,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }
    }
}

@Composable
fun WithDrawGuideImage(
    @StringRes reasonTextResId: Int,
    index: Int,
    imageData: ByteArray?
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageData)
            .crossfade(true)
            .memoryCacheKey("guide_${reasonTextResId}_$index")
            .diskCacheKey("guide_${reasonTextResId}_$index")
            .build(),
        contentDescription = "withdraw guide image",
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(320f / 200f)
            .clip(RoundedCornerShape(size = 12.dp)),
        contentScale = ContentScale.Crop,
    )
}

data class SelectedWithdrawReason(
    val reason: WithdrawalReason,
    val otherReasonText: String = ""
)

val WithdrawalReason.content: WithdrawRetentionSheetContent
    get() = when (this) {
        WithdrawalReason.INCONVENIENT_USE -> WithdrawRetentionSheetContent(
            reasonTextResId = R.string.withdraw_reason_inconvenient_use,
            titleResId = R.string.withdraw_reason_inconvenient_use_hold_title,
            descriptionResId = R.string.withdraw_reason_inconvenient_use_hold_description,
            confirmButtonTextResId = R.string.withdraw_reason_inconvenient_use_hold_confirm,
            cancelButtonTextResId = R.string.withdraw_reason_inconvenient_use_hold_cancel
        )

        WithdrawalReason.WANT_TO_DELETE_HISTORY -> WithdrawRetentionSheetContent(
            reasonTextResId = R.string.withdraw_reason_want_to_delete_history,
            titleResId = R.string.withdraw_reason_want_to_delete_history_hold_title,
            descriptionResId = R.string.withdraw_reason_want_to_delete_history_hold_description,
            confirmButtonTextResId = R.string.withdraw_reason_want_to_delete_history_hold_confirm,
            cancelButtonTextResId = R.string.withdraw_reason_want_to_delete_history_hold_cancel
        )

        WithdrawalReason.RARELY_USED -> WithdrawRetentionSheetContent(
            reasonTextResId = R.string.withdraw_reason_rarely_used,
            titleResId = R.string.withdraw_reason_rarely_used_hold_title,
            descriptionResId = R.string.withdraw_reason_rarely_used_hold_description,
            confirmButtonTextResId = R.string.withdraw_reason_rarely_used_hold_confirm,
            cancelButtonTextResId = R.string.withdraw_reason_rarely_used_hold_cancel
        )

        WithdrawalReason.CONTENT_NOT_SATISFYING -> WithdrawRetentionSheetContent(
            reasonTextResId = R.string.withdraw_reason_content_not_satisfying,
            titleResId = R.string.withdraw_reason_content_not_satisfying_hold_title,
            descriptionResId = R.string.withdraw_reason_content_not_satisfying_hold_description,
            confirmButtonTextResId = R.string.withdraw_reason_content_not_satisfying_hold_confirm,
            cancelButtonTextResId = R.string.withdraw_reason_content_not_satisfying_hold_cancel
        )

        WithdrawalReason.OTHER -> WithdrawRetentionSheetContent(
            reasonTextResId = R.string.withdraw_reason_other,
            titleResId = R.string.withdraw_reason_other_hold_title,
            descriptionResId = R.string.withdraw_reason_other_hold_description,
            confirmButtonTextResId = R.string.withdraw_reason_other_hold_confirm,
            cancelButtonTextResId = R.string.withdraw_reason_other_hold_cancel
        )
    }

data class WithdrawRetentionSheetContent(
    @StringRes val reasonTextResId: Int,       // 탈퇴 사유 텍스트
    @StringRes val titleResId: Int,            // 바텀시트 제목
    @StringRes val descriptionResId: Int,      // 바텀시트 설명
    @StringRes val confirmButtonTextResId: Int,
    @StringRes val cancelButtonTextResId: Int,
)

enum class WithdrawStep(val stepNum: Int) {
    REASON_SELECT(0),
    CONFIRM(1),
}