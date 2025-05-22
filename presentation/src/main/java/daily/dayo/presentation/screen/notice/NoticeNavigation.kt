package daily.dayo.presentation.screen.notice

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import daily.dayo.presentation.viewmodel.NoticeViewModel
import kotlinx.coroutines.CoroutineScope

fun NavController.navigateNotices() {
    navigate(NoticeRoute.route)
}

fun NavController.navigateNoticeDetail(noticeId: Long) {
    navigate(NoticeRoute.noticeDetail(noticeId))
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.noticeNavGraph(
    noticeViewModel: NoticeViewModel,
    onBackClick: () -> Unit,
    onNoticeDetailClick: (Long) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
) {
    composable(NoticeRoute.route) {
        NoticesScreen(
            onBackClick = onBackClick,
            onNoticeDetailClick = onNoticeDetailClick,
            noticeViewModel = noticeViewModel,
            sharedElementScope = sharedTransitionScope,
            animatedVisibilityScope = this,
        )
    }

    composable(
        route = NoticeRoute.noticeDetailRoute,
        arguments = listOf(
            navArgument("noticeId") {
                type = NavType.LongType
            }
        )
    ) { navBackStackEntry ->
        navBackStackEntry.arguments?.getLong("noticeId")?.let { noticeId ->
            NoticeDetailScreen(
                noticeId = noticeId,
                onBackClick = onBackClick,
                noticeViewModel = noticeViewModel,
                sharedElementScope = sharedTransitionScope,
                animatedVisibilityScope = this,
            )
        }
    }
}

object NoticeRoute {
    const val route = "notice"

    const val noticeDetailRoute = "$route/{noticeId}"

    fun noticeDetail(noticeId: Long) = "$route/$noticeId"
}