package daily.dayo.presentation.view

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun EmojiView(
    emoji: String,
    emojiSize: TextUnit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AppCompatTextView(context).apply {
                setTextColor(Black.toArgb())
                text = emoji
                textSize = emojiSize.value
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
        },
        update = {
            it.apply {
                text = emoji
            }
        },
    )
}