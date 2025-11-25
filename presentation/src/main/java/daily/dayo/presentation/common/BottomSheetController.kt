import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class BottomSheetController {
    var sheetContent: @Composable (() -> Unit) by mutableStateOf({})
        private set

    var isVisible by mutableStateOf(false)
        private set

    fun setContent(content: @Composable () -> Unit) {
        sheetContent = content
    }

    fun show() {
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }

    fun clear() {
        isVisible = false
        sheetContent = {}
    }
}

val LocalBottomSheetController = compositionLocalOf<BottomSheetController> {
    error("No BottomSheetController provided")
}
