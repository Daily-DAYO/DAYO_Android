package daily.dayo.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Gray1_313131
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.h3


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigation(
    title: String = "",
    leftIcon: @Composable () -> Unit = {},
    rightIcon: @Composable () -> Unit = {},
    titleAlignment: TopNavigationAlign = TopNavigationAlign.LEFT
) {
    when (titleAlignment) {
        TopNavigationAlign.LEFT -> {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White_FFFFFF,
                    titleContentColor = Gray1_313131,
                ),
                navigationIcon = leftIcon,
                actions = { rightIcon() },
                title = {
                    Text(text = title, maxLines = 1, style = MaterialTheme.typography.h3)
                }
            )
        }

        TopNavigationAlign.CENTER -> {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White_FFFFFF,
                    titleContentColor = Gray1_313131,
                ),
                navigationIcon = leftIcon,
                actions = { rightIcon() },
                title = {
                    Text(text = title, maxLines = 1, style = MaterialTheme.typography.h3)
                }
            )
        }
    }
}

enum class TopNavigationAlign {
    LEFT, CENTER
}

@Preview
@Composable
fun PreviewTopNavigation() {
    Column {
        // Default 정렬 사용 예시
        TopNavigation(
            title = "Title",
            leftIcon = {
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        // Center 정렬 사용 예시
        TopNavigation(
            title = "Title",
            rightIcon = {
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_option),
                        contentDescription = "Option"
                    )
                }
            },
            titleAlignment = TopNavigationAlign.CENTER
        )
    }
}