package daily.dayo.presentation.screen.notification

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.size.Size
import daily.dayo.domain.model.Notification
import daily.dayo.domain.model.Topic
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.TimeChangerUtil
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray7_F6F6F7
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.viewmodel.NotificationViewModel
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun NotificationScreen(
    notificationViewModel: NotificationViewModel = hiltViewModel()
) {
    val notifications = notificationViewModel.alarmList.collectAsLazyPagingItems()
    val refreshing by notificationViewModel.isRefreshing.collectAsStateWithLifecycle()
    val pullRefreshState =
        rememberPullRefreshState(refreshing, { notificationViewModel.loadNotifications() })

    LaunchedEffect(Unit) {
        notificationViewModel.loadNotifications()
    }

    Scaffold(
        topBar = {
            NotificationTopNavigation()
        }) { innerPadding ->
        NotificationContent(
            innerPadding = innerPadding,
            context = LocalContext.current,
            notifications = notifications,
            markAlarmAsChecked = { alarmId ->
                notificationViewModel.markAlarmAsChecked(alarmId)
            },
            pullRefreshState = pullRefreshState
        )
    }
}

@Composable
@Preview
fun NotificationTopNavigation() {
    TopNavigation(
        title = stringResource(R.string.notification),
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationContent(
    innerPadding: PaddingValues,
    isRefreshing: Boolean = false,
    pullRefreshState: PullRefreshState,
    context: Context = LocalContext.current,
    notifications: LazyPagingItems<Notification> = flowOf(
        PagingData.from(
            listOf(
                Notification(
                    alarmId = 0,
                    topic = Topic.HEART,
                    check = false,
                    content = "",
                    createdTime = "",
                    image = "",
                    nickname = "",
                    memberId = "",
                    postId = 0,
                )
            )
        )
    ).collectAsLazyPagingItems(),
    markAlarmAsChecked: (Int) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        if (notifications.itemCount < 1 &&
            notifications.loadState.refresh is LoadState.NotLoading &&
            notifications.loadState.append.endOfPaginationReached
        ) {
            EmptyNotifications()
        } else {
            // checkedItems와 unCheckedItems를 보여줄때, alaramId만으로 구분하면, check 상태가 변경 되었을 때 오류가 날 수 있음
            val (checkedItems, uncheckedItems) = notifications.itemSnapshotList.items.partition { it.check == true }

            LazyColumn(
                modifier = Modifier.wrapContentSize()
            ) {
                if (uncheckedItems.isNotEmpty()) {
                    itemsIndexed(
                        items = uncheckedItems,
                        key = { idx, notification -> "unchecked-${notification.alarmId ?: idx}" },
                    ) { idx, notification ->
                        Box(modifier = Modifier.animateItem()) {
                            NotificationView(
                                notification = notification,
                                context = context,
                                onClick = {
                                    notification.alarmId?.let { alarmId ->
                                        markAlarmAsChecked(alarmId)
                                    }
                                    // TODO Navigate Alarm Detail
                                }
                            )
                        }
                    }

                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                        )
                    }
                }

                // 구분선이 필요할 때
                if (uncheckedItems.isNotEmpty() && checkedItems.isNotEmpty()) {
                    item {
                        Divider(
                            color = Gray7_F6F6F7,
                            thickness = 1.dp,
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .height(1.dp)
                                .fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                    }
                }

                if (checkedItems.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.notification_seen_title),
                            style = DayoTheme.typography.b3.copy(color = Dark),
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                    }

                    itemsIndexed(
                        items = checkedItems,
                        key = { idx, notification -> "checked-${notification.alarmId ?: idx}" },
                    ) { idx, notification ->
                        Box(modifier = Modifier.animateItem()) {
                            NotificationView(
                                notification = notification,
                                context = context,
                                onClick = {
                                    // TODO Navigate Alarm Detail
                                }
                            )
                        }
                    }
                }
            }

            // refresh indicator
            PullRefreshIndicator(
                isRefreshing,
                pullRefreshState,
                Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
@Preview
fun EmptyNotifications() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_notification_empty),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = stringResource(R.string.notification_empty_title),
            style = DayoTheme.typography.b3.copy(color = Gray3_9FA5AE)
        )
        Spacer(modifier = Modifier.size(2.dp))
        Text(
            text = stringResource(R.string.notification_empty_description),
            style = DayoTheme.typography.caption2.copy(color = Gray4_C5CAD2)
        )
    }
}

@Composable
@Preview
fun NotificationView(
    notification: Notification = Notification(
        alarmId = 0,
        topic = Topic.HEART,
        check = false,
        content = "",
        createdTime = "",
        image = "",
        nickname = "",
        memberId = "",
        postId = 0,
    ),
    context: Context = LocalContext.current,
    onClick: () -> Unit = {}
) {
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val notificationMessage = buildAnnotatedString {
        // 1. 닉네임 포함된 메시지 인지 체크
        if (!notification.nickname.isNullOrBlank()) {
            pushStringAnnotation(
                tag = "nickname",
                annotation = notification.nickname ?: ""
            )
            withStyle(
                style = DayoTheme.typography.caption1.copy(color = Dark)
                    .toSpanStyle()
            ) {
                append(notification.nickname ?: "")
            }
            pop()
        }

        // 2. 본문
        withStyle(
            style = DayoTheme.typography.caption2.copy(color = Dark).toSpanStyle()
        ) {
            append(notification.content ?: "")
        }
    }

    Row(
        modifier = Modifier
            .clickable { onClick() }
            .background(DayoTheme.colorScheme.background)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 12.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
        ) {
            RoundImageView(
                imageUrl = "", // TODO USER IMAGE Format: ${BuildConfig.BASE_URL}/images/
                context = context,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        // TODO: Navigate To User Profile
                    },
                imageDescription = "notification thumbnail",
                imageSize = Size.ORIGINAL,
                roundSize = 14.dp,
                placeholderResId = R.drawable.ic_profile_default_user_profile,
            )
            Spacer(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Text(
                    text = notificationMessage,
                    style = DayoTheme.typography.caption2.copy(color = Dark), // 기본 텍스트 스타일
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures { offset ->
                            textLayoutResult?.let { layoutResult ->
                                val position = layoutResult.getOffsetForPosition(offset)
                                notificationMessage.getStringAnnotations(
                                    tag = "nickname",
                                    start = position,
                                    end = position
                                )
                                    .firstOrNull()?.let {
                                        // TODO: Navigate To User Profile
                                    }
                            }
                        }
                    },
                    onTextLayout = { layoutResult ->
                        textLayoutResult = layoutResult
                    },
                )
                notification.createdTime?.let {
                    Text(
                        text = TimeChangerUtil.timeChange(context, notification.createdTime ?: ""),
                        style = DayoTheme.typography.caption4.copy(color = Gray3_9FA5AE)
                    )
                }
            }
        }

        if (!notification.image.isNullOrBlank()) {
            Row(modifier = Modifier.requiredSize(width = (16 + 56).dp, height = 56.dp)) {
                Spacer(
                    modifier = Modifier
                        .width(16.dp)
                        .fillMaxHeight()
                )
                RoundImageView(
                    imageUrl = "${BuildConfig.BASE_URL}/images/${notification.image!!}",
                    context = context,
                    modifier = Modifier
                        .size(56.dp),
                    roundSize = 10.dp,
                    imageDescription = "notification thumbnail",
                )
            }
        }
    }
}