package daily.dayo.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import daily.dayo.domain.model.Post
import daily.dayo.presentation.BuildConfig

@Composable
fun HomePostView(post: Post, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(bottom = 10.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("${BuildConfig.BASE_URL}/images/${post.thumbnailImage}")
                .build(),
            contentDescription = "dayo pick image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(size = 8.dp))
        )
    }
}