package daily.dayo.presentation.view

import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import daily.dayo.presentation.screen.home.CategoryMenu
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray7_F6F6F7
import daily.dayo.presentation.theme.PrimaryL3_F2FBF7
import daily.dayo.presentation.theme.Primary_23C882

@Composable
fun CategoryHorizontalGroup(
    categoryMenus: List<CategoryMenu>,
    selectedCategory: CategoryMenu,
    onCategorySelect: (CategoryMenu) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 18.dp)
    ) {
        categoryMenus.forEach { category ->
            item {
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val buttonColors = if (category == selectedCategory)
                    ButtonDefaults.buttonColors(
                        containerColor = PrimaryL3_F2FBF7,
                        contentColor = Primary_23C882
                    )
                else
                    ButtonDefaults.buttonColors(
                        containerColor = if (isPressed) Color(0xFFE4F7ED) else Gray7_F6F6F7,
                        contentColor = if (isPressed) Primary_23C882 else Gray4_C5CAD2
                    )

                Button(
                    onClick = { onCategorySelect(category) },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    colors = buttonColors,
                    interactionSource = interactionSource,
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(36.dp)
                        .selectable(
                            selected = (category == categoryMenus),
                            onClick = { onCategorySelect(category) },
                            role = Role.RadioButton
                        )
                        .indication(interactionSource = interactionSource, indication = null),
                    content = {
                        Text(text = category.name, style = DayoTheme.typography.b5)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewCategoryHorizontalGroup() {
    val categoryMenus = listOf(
        CategoryMenu.All,
        CategoryMenu.Scheduler,
        CategoryMenu.StudyPlanner,
        CategoryMenu.PocketBook,
        CategoryMenu.SixHoleDiary,
        CategoryMenu.Digital,
        CategoryMenu.ETC
    )
    val selectedCategory = remember { mutableStateOf(categoryMenus[0]) }
    DayoTheme {
        CategoryHorizontalGroup(categoryMenus, selectedCategory.value, {})
    }
}