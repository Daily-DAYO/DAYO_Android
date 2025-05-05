package daily.dayo.presentation.screen.notice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daily.dayo.domain.model.Notice
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.TopNavigation

@Preview
@Composable
fun NoticesScreen(
    onBackClick: () -> Unit = {},
    onNoticeDetailClick: (Long) -> Unit = {},
) {
    Scaffold(
        topBar = {
            NoticesActionbarLayout(onBackClick = onBackClick)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(DayoTheme.colorScheme.background)
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(30) { index ->
                    Notice(
                        notice = Notice(
                            noticeId = index.toLong(),
                            title = "공지사항 제목 $index",
                            uploadDate = "2023.10.01"
                        ),
                        onNoticeDetailClick = onNoticeDetailClick,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun NoticesActionbarLayout(
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
        title = stringResource(R.string.notice_actionbar_title),
    )
}

@Preview
@Composable
fun Notice(
    notice: Notice = Notice(
        noticeId = Long.MAX_VALUE,
        title = "공지사항",
        uploadDate = "0000.00.00"
    ),
    onNoticeDetailClick: (Long) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .clickable { onNoticeDetailClick(notice.noticeId) }
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(
            text = notice.uploadDate,
            modifier = Modifier.wrapContentSize(),
            style = DayoTheme.typography.caption4,
            color = Gray3_9FA5AE,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = notice.title,
            modifier = Modifier.wrapContentSize(),
            style = DayoTheme.typography.b6,
            color = Dark,
        )
    }
}