package daily.dayo.presentation.screen.mypage

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.myPageNavGraph() {
    composable(MyPageRoute.route) {
        MyPageScreen()
    }
}

object MyPageRoute {
    const val route = "myPage"
}