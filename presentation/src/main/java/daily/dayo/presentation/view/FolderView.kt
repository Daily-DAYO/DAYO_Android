package daily.dayo.presentation.view

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import daily.dayo.domain.model.Folder
import daily.dayo.domain.model.Privacy
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray3_9FA5AE
import java.text.DecimalFormat

@Composable
fun FolderView(
    folder: Folder,
    onClickFolder: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
    val imageInteractionSource = remember { MutableInteractionSource() }
    Column(modifier = modifier
        .clickableSingle(
            interactionSource = imageInteractionSource,
            indication = null,
            onClick = { onClickFolder() }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            // thumbnail image
            RoundImageView(
                context = LocalContext.current,
                imageUrl = "${BuildConfig.BASE_URL}/images/${folder.thumbnailImage}",
                imageDescription = folder.title,
                customModifier = Modifier
                    .matchParentSize()
            )

            // private icon
            if (folder.privacy == Privacy.ONLY_ME) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_folder_private_my_page),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    contentDescription = "비공개 폴더"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // folder info
        Column {
            Text(text = folder.title, style = DayoTheme.typography.b6.copy(Dark))

            val dec = DecimalFormat("#,###")
            Text(text = "${dec.format(folder.postCount)}개", style = DayoTheme.typography.b6.copy(Gray3_9FA5AE))
        }
    }
}