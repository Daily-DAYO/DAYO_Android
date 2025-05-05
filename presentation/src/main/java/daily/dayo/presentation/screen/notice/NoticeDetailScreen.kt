package daily.dayo.presentation.screen.notice

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.TopNavigation

@Preview
@Composable
fun NoticeDetailScreen(
    onBackClick: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
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
                title = "공지사항",
                contents = "공지사항 내용",
                uploadDate = "0000.00.00",
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

@Preview
@Composable
fun NoticeDetail(
    title: String = "공지사항",
    contents: String = "공지사항 내용",
    uploadDate: String = "0000.00.00",
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 20.dp, end = 20.dp, top = 4.dp)
            .background(DayoTheme.colorScheme.background)
    ) {
        NoticeDetailHeader(
            title = title,
            uploadDate = uploadDate,
        )
        Divider(
            modifier = Modifier.padding(top = 20.dp, bottom = 18.dp),
            color = Gray6_F0F1F3,
            thickness = 1.dp,
        )
        Text(
            text = contents,
            style = DayoTheme.typography.b6,
            color = Dark,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        )
    }
}

@Preview
@Composable
fun NoticeDetailHeader(
    title: String = "공지사항",
    uploadDate: String = "0000.00.00",
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Text(
            text = uploadDate,
            style = DayoTheme.typography.caption4,
            color = Gray3_9FA5AE,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = DayoTheme.typography.b1,
            color = Dark,
        )
    }
}