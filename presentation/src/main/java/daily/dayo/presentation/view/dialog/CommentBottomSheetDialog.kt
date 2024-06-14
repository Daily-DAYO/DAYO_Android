package daily.dayo.presentation.view.dialog

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.R
import daily.dayo.presentation.theme.Gray1_313131
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.b1
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.viewmodel.PostViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CommentBottomSheetDialog(
    sheetState: ModalBottomSheetState,
    onClickClose: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    postViewModel: PostViewModel
) {
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp),
        modifier = modifier,
        sheetContent = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(White_FFFFFF)
                    ) {
                        Text(
                            text = stringResource(id = R.string.comment),
                            modifier = Modifier.padding(top = 12.dp, bottom = 6.dp),
                            textAlign = TextAlign.Center,
                            color = Gray1_313131,
                            style = MaterialTheme.typography.b1.copy(Gray1_313131)
                        )

                        NoRippleIconButton(
                            onClick = onClickClose,
                            iconContentDescription = "close",
                            iconPainter = painterResource(id = R.drawable.ic_x_sign),
                            iconButtonModifier = modifier.align(Alignment.CenterEnd)
                        )
                    }
                }
            }
        }
    ) {}
}