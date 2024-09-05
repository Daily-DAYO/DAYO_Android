package daily.dayo.presentation.view

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun RoundImageView(
    context: Context,
    imageUrl: String,
    imageDescription: String = "default image view",
    placeholder: Drawable? = null,
    roundSize: Dp = 8.dp,
    customModifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .placeholder(placeholder)
            .build(),
        contentDescription = imageDescription,
        contentScale = ContentScale.Crop,
        modifier = customModifier
            .clip(RoundedCornerShape(size = roundSize))
    )
}