package daily.dayo.presentation.screen.mypage

import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.b5
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import kotlinx.coroutines.launch

@Composable
fun FollowScreen(
    tabNum: Int,
    onBackClick: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = tabNum, pageCount = { 2 })
    Scaffold(
        topBar = {
            TopNavigation(
                leftIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.indication(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_sign),
                            contentDescription = "back sign",
                            tint = Gray1_50545B
                        )
                    }
                },
                title = "username",
                titleAlignment = TopNavigationAlign.CENTER
            )
        },
        containerColor = White
    ) { innerPadding ->
        FollowTab(
            modifier = Modifier.padding(innerPadding),
            pagerState = pagerState
        )

        FollowContent(pagerState)
    }
}

@Composable
private fun FollowContent(pagerState: PagerState) {
    HorizontalPager(
        state = pagerState,
        flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
        pageContent = { page -> }
    )
}

@Composable
private fun FollowTab(modifier: Modifier, pagerState: PagerState) {
    val coroutineScope = rememberCoroutineScope()
    val pages = listOf(
        stringResource(id = R.string.follower),
        stringResource(id = R.string.following)
    )

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        containerColor = White,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                color = Primary_23C882,
            )
        },
        divider = { Divider(color = Color.Transparent, thickness = 0.dp) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(top = 4.dp),
    ) {
        pages.forEachIndexed { index, title ->
            Tab(
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.b5
                    )
                },
                selected = pagerState.currentPage == index,
                selectedContentColor = Primary_23C882,
                unselectedContentColor = Gray2_767B83,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            )
        }
    }
}

@Preview
@Composable
private fun PreviewFollowScreen() {
    FollowScreen(0, {})
}