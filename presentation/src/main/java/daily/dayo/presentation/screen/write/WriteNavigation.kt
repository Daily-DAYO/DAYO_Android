package daily.dayo.presentation.screen.write

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.writeNavGraph(
    onBackClick: () -> Unit
) {
    composable(route = WriteRoute.route) {
        WriteRoute(
            onBackClick = onBackClick,
        )
    }
}

object WriteRoute {
    const val route = "write"
}