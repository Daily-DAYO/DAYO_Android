package daily.dayo.presentation.screen.account

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import daily.dayo.presentation.R
import daily.dayo.presentation.activity.LoginActivity
import daily.dayo.presentation.activity.MainActivity
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray2_767B83
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val OTHER_REASON_TEXT_MAX_LENGTH = 100

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WithdrawScreen(
    onBackClick: () -> Unit = {},
    bottomSheetState: ModalBottomSheetState,
    bottomSheetContent: (@Composable () -> Unit) -> Unit,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    accountViewModel: AccountViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val withdrawStep = remember { mutableStateOf(WithdrawStep.REASON_SELECT) }
    var selectedReason by remember { mutableStateOf<SelectedWithdrawReason?>(null) }
    val onClickWithdrawContinue: (WithdrawalReason) -> Unit = { reason ->
        coroutineScope.launch { bottomSheetState.show() }
        bottomSheetContent {
            selectedReason?.let { selected ->
                WithdrawContinueBottomSheetDialog(
                    coroutineScope = coroutineScope,
                    bottomSheetState = bottomSheetState,
                    keyboardController = keyboardController,
                    focusManager = focusManager,
                    setWithdrawStep = { withdrawStep.value = it },
                    selectedReason = selected,
                    setSelectedReason = { selectedReason = it },
                    snackBarHostState = snackBarHostState
                )
            }
        }
    }

    LaunchedEffect(bottomSheetState) {
        snapshotFlow { bottomSheetState.currentValue }
            .collect { value ->
                if (value == ModalBottomSheetValue.Hidden) {
                    focusManager.clearFocus(force = true)
                    keyboardController?.hide()
                    if (withdrawStep.value == WithdrawStep.REASON_SELECT) {
                        selectedReason = null
                    }
                }
            }
    }

    val onBackClickInWithdraw = {
        when (withdrawStep.value) {
            WithdrawStep.REASON_SELECT -> onBackClick()
            WithdrawStep.CONFIRM -> {
                withdrawStep.value = WithdrawStep.REASON_SELECT
                selectedReason = null
            }
        }
    }
    val showWithdrawDialog = remember { mutableStateOf(false) }
    val withdrawSuccess by accountViewModel.withdrawSuccess.collectAsStateWithLifecycle()

    BackHandler {
        onBackClickInWithdraw()
    }

    BackHandler(enabled = bottomSheetState.isVisible) {
        coroutineScope.launch {
            bottomSheetState.hide()
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

    when (withdrawStep.value) {
        WithdrawStep.REASON_SELECT -> {
            WithdrawReasonList(
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
            painter = painterResource(id = R.drawable.ic_next),
            contentDescription = stringResource(id = reason.content.reasonTextResId),
            tint = Gray4_C5CAD2
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WithdrawContinueBottomSheetDialog(
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager,
    setWithdrawStep: (WithdrawStep) -> Unit,
    selectedReason: SelectedWithdrawReason?,
    setSelectedReason: (SelectedWithdrawReason?) -> Unit,
    snackBarHostState: SnackbarHostState,
) {
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp),
        modifier = Modifier,
        sheetContent = {
            selectedReason?.let { selected ->
                WithdrawHoldBottomSheet(
                    reason = selected.reason,
                    otherReasonText = selected.otherReasonText,
                    setOtherReasonText = { newText ->
                        setSelectedReason(
                            selected.copy(otherReasonText = newText)
                        )
                    },
                    onConfirm = {
                        when (selected.reason) {
                            WithdrawalReason.OTHER -> {
                                if (selected.otherReasonText.isBlank()) {
                                    return@WithdrawHoldBottomSheet
                                } else {
                                    coroutineScope.launch {
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                    }
                                }
                            }

                            else -> {

                            }
                        }

                        coroutineScope.launch {
                            setWithdrawStep(WithdrawStep.CONFIRM)
                            bottomSheetState.hide()
                        }
                    },
                    onCancel = {
                        when (selected.reason) {
                            WithdrawalReason.WANT_TO_DELETE_HISTORY -> {
                                coroutineScope.launch {
                                    snackBarHostState.showSnackbar("기록 삭제가 요청되었습니다.")
                                    bottomSheetState.hide()
                                    // TODO 기록 지우기 디자인에 따라 구현 추가
                                }
                            }

                            else -> {
                                coroutineScope.launch { bottomSheetState.hide() }
                            }
                        }
                    }
                )
            }
        }
    ) { }
}

@Composable
fun WithdrawHoldBottomSheet(
    reason: WithdrawalReason,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    setOtherReasonText: (String) -> Unit = {},
    otherReasonText: String = "",
) {
    val content = reason.content
    val isOtherReason = (content.reasonTextResId == R.string.withdraw_reason_other)
    var otherReasonContentValue by remember { mutableStateOf(TextFieldValue(otherReasonText)) }

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
                    style = DayoTheme.typography.b1.copy(
                        color = Dark,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(
                    modifier = Modifier.height(
                        if (isOtherReason) 2.dp else 4.dp
                    )
                )
                Text(
                    text = stringResource(id = content.descriptionResId),
                    style = DayoTheme.typography.caption2.copy(
                        color = Gray2_767B83,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            Spacer(
                modifier = Modifier.height(
                    if (isOtherReason) 8.dp else 12.dp
                )
            )

            if (isOtherReason) {
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
                        .height(52.dp),
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
                        .height(52.dp),
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
            painter = painterResource(id = R.drawable.ic_check_mark),
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

data class SelectedWithdrawReason(
    val reason: WithdrawalReason,
    val otherReasonText: String = ""
)

enum class WithdrawalReason(val content: WithdrawRetentionSheetContent) {
    INCONVENIENT_USE(
        WithdrawRetentionSheetContent(
            reasonTextResId = R.string.withdraw_reason_inconvenient_use,
            titleResId = R.string.withdraw_reason_inconvenient_use_hold_title,
            descriptionResId = R.string.withdraw_reason_inconvenient_use_hold_description,
            confirmButtonTextResId = R.string.withdraw_reason_inconvenient_use_hold_confirm,
            cancelButtonTextResId = R.string.withdraw_reason_inconvenient_use_hold_cancel
        )
    ),
    WANT_TO_DELETE_HISTORY(
        WithdrawRetentionSheetContent(
            reasonTextResId = R.string.withdraw_reason_want_to_delete_history,
            titleResId = R.string.withdraw_reason_want_to_delete_history_hold_title,
            descriptionResId = R.string.withdraw_reason_want_to_delete_history_hold_description,
            confirmButtonTextResId = R.string.withdraw_reason_want_to_delete_history_hold_confirm,
            cancelButtonTextResId = R.string.withdraw_reason_want_to_delete_history_hold_cancel
        )
    ),
    RARELY_USED(
        WithdrawRetentionSheetContent(
            reasonTextResId = R.string.withdraw_reason_rarely_used,
            titleResId = R.string.withdraw_reason_rarely_used_hold_title,
            descriptionResId = R.string.withdraw_reason_rarely_used_hold_description,
            confirmButtonTextResId = R.string.withdraw_reason_rarely_used_hold_confirm,
            cancelButtonTextResId = R.string.withdraw_reason_rarely_used_hold_cancel
        )
    ),
    CONTENT_NOT_SATISFYING(
        WithdrawRetentionSheetContent(
            reasonTextResId = R.string.withdraw_reason_content_not_satisfying,
            titleResId = R.string.withdraw_reason_content_not_satisfying_hold_title,
            descriptionResId = R.string.withdraw_reason_content_not_satisfying_hold_description,
            confirmButtonTextResId = R.string.withdraw_reason_content_not_satisfying_hold_confirm,
            cancelButtonTextResId = R.string.withdraw_reason_content_not_satisfying_hold_cancel
        )
    ),
    OTHER(
        WithdrawRetentionSheetContent(
            reasonTextResId = R.string.withdraw_reason_other,
            titleResId = R.string.withdraw_reason_other_hold_title,
            descriptionResId = R.string.withdraw_reason_other_hold_description,
            confirmButtonTextResId = R.string.withdraw_reason_other_hold_confirm,
            cancelButtonTextResId = R.string.withdraw_reason_other_hold_cancel
        )
    );
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