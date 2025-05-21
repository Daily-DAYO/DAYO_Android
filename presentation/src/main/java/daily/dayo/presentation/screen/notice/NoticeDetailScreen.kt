package daily.dayo.presentation.screen.notice

import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.viewmodel.NoticeViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NoticeDetailScreen(
    noticeId: Long,
    onBackClick: () -> Unit = {},
    noticeViewModel: NoticeViewModel = hiltViewModel(),
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedElementScope: SharedTransitionScope,
) {
    val scrollState = rememberScrollState()
    val noticeDetail by noticeViewModel.detailNotice.collectAsStateWithLifecycle()
    val notice = noticeViewModel.selectedNotice.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        noticeViewModel.requestDetailNotice(noticeId)
    }

    Scaffold(
        topBar = {
            NoticeDetailActionbarLayout(onBackClick = onBackClick)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(DayoTheme.colorScheme.background)
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            NoticeDetail(
                noticeId = noticeId,
                title = notice?.title ?: "공지사항",
                contents = noticeDetail?.data ?: "",
                uploadDate = notice?.uploadDate ?: "0000.00.00",
                sharedTransitionScope = sharedElementScope,
                animatedVisibilityScope = animatedVisibilityScope,
            )
        }
    }
}

@Preview
@Composable
fun NoticeDetailActionbarLayout(
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NoticeDetail(
    noticeId: Long,
    title: String = "공지사항",
    contents: String = "공지사항 내용",
    uploadDate: String = "0000.00.00",
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 20.dp, end = 20.dp, top = 4.dp)
                .background(DayoTheme.colorScheme.background)
        ) {
            NoticeDetailHeader(
                titleModifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "title_$noticeId"),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
                uploadDateModifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "uploadDate_$noticeId"),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
                title = title,
                uploadDate = uploadDate,
            )
            Divider(
                modifier = Modifier.padding(top = 20.dp, bottom = 18.dp),
                color = Gray6_F0F1F3,
                thickness = 1.dp,
            )

            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        loadDataWithBaseURL(null, contents, "text/html", "UTF-8", null)
                    }
                },
                update = { webView ->
                    webView.loadDataWithBaseURL(null, contents, "text/html", "UTF-8", null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            )
        }
    }
}

@Composable
fun NoticeDetailHeader(
    titleModifier: Modifier,
    uploadDateModifier: Modifier,
    title: String = "공지사항",
    uploadDate: String = "0000.00.00",
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Text(
            modifier = uploadDateModifier,
            text = uploadDate,
            style = DayoTheme.typography.caption4,
            color = Gray3_9FA5AE,
            softWrap = false,
            overflow = TextOverflow.Visible
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = titleModifier,
            text = title,
            style = DayoTheme.typography.b1,
            color = Dark,
            softWrap = false,
            overflow = TextOverflow.Visible
        )
    }
}