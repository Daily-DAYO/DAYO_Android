package daily.dayo.presentation.screen.bookmark

import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Gray1_50545B
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign

@Composable
fun BookmarkScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = { BookmarkTopNavigation(onBackClick) },
        content = { contentPadding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .background(color = White_FFFFFF)
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 20.dp),
            ) {

            }
        }
    )
}

@Composable
private fun BookmarkTopNavigation(
    onBackClick: () -> Unit
) {
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
        title = stringResource(id = R.string.bookmark),
        titleAlignment = TopNavigationAlign.CENTER
    )
}