package daily.dayo.presentation.view

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import daily.dayo.presentation.R

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

@Composable
fun BadgeRoundImageView(
    context: Context,
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentModifier: Modifier,
    imageDescription: String = "default image view",
    placeholder: Drawable? = null,
    roundSize: Dp = 8.dp,
    badgeSize: Dp = 30.dp
) {
    Box(modifier = modifier) {
        RoundImageView(
            context = context,
            imageUrl = imageUrl,
            imageDescription = imageDescription,
            placeholder = placeholder,
            roundSize = roundSize,
            customModifier = contentModifier
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_camera_button),
            contentDescription = "set image",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(badgeSize),
            tint = Color.Unspecified
        )
    }
}