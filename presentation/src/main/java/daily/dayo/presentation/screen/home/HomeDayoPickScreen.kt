package daily.dayo.presentation.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.R
import daily.dayo.presentation.view.EmojiView
import daily.dayo.presentation.view.getBottomSheetDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeDayoPickScreen(
    selectedCategoryName: String,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            EmojiView(
                emoji = "\uD83D\uDCA1",
                emojiSize = MaterialTheme.typography.bodyMedium.fontSize,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Text(
                text = stringResource(id = R.string.home_dayopick_description),
                style = MaterialTheme.typography.bodyMedium.copy(Color(0xFF73777C)),
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .align(Alignment.CenterVertically)
            )

            CategoryButton(selectedCategoryName, coroutineScope, bottomSheetState)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CategoryButton(
    selectedCategory: String,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState
) {
    OutlinedTextField(
        value = selectedCategory,
        onValueChange = { },
        trailingIcon = { Icon(Icons.Filled.ArrowDropDown, "category menu") },
        readOnly = true,
        enabled = false,
        modifier = Modifier.clickable(
            onClick = { coroutineScope.launch { bottomSheetState.show() } },
            indication = null,
            interactionSource = remember { MutableInteractionSource() }),
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview(showBackground = true)
private fun PreviewHomeDayoPickScreen() {
    MaterialTheme {
        HomeDayoPickScreen(CategoryMenu.All.name, rememberCoroutineScope(), getBottomSheetDialogState())
    }
}

