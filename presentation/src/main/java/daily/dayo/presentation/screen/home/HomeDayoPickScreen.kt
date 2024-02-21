package daily.dayo.presentation.screen.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray6_F0F1F3
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.caption3
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
    ) {
        // description
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
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
            }
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
    Button(
        onClick = { coroutineScope.launch { bottomSheetState.show() } },
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(top = 6.dp, bottom = 6.dp, start = 12.dp, end = 4.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = White_FFFFFF, contentColor = Gray2_767B83),
        modifier = Modifier
            .border(1.dp, Gray6_F0F1F3, shape = RoundedCornerShape(8.dp))
    ) {
        Text(text = selectedCategory, style = MaterialTheme.typography.caption3)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.Filled.ArrowDropDown, "category menu")
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview(showBackground = true)
private fun PreviewHomeDayoPickScreen() {
    MaterialTheme {
        HomeDayoPickScreen(CategoryMenu.All.name, rememberCoroutineScope(), getBottomSheetDialogState())
    }
}

