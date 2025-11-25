package daily.dayo.presentation.screen.write

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Surface
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import daily.dayo.domain.model.Category
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.screen.home.CategoryMenu
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Gray4_C5CAD2
import daily.dayo.presentation.theme.Gray5_E8EAEE
import daily.dayo.presentation.theme.Gray7_F6F6F7
import daily.dayo.presentation.theme.PrimaryL3_F2FBF7
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.DayoTextButton
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import daily.dayo.presentation.view.dialog.BottomSheetDialog
import daily.dayo.presentation.viewmodel.WriteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val WRITE_POST_IMAGE_MIN_SIZE = 1
const val WRITE_POST_DETAIL_MAX_LENGTH = 200
const val WRITE_POST_IMAGE_SIZE = 220
const val WRITE_POST_TOP_Z_INDEX = 1f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WriteRoute(
    postId: Long?,
    snackBarHostState: SnackbarHostState,
    navigateToWritePost: (Long) -> Unit,
    onBackClick: () -> Unit,
    onTagClick: () -> Unit,
    onWriteFolderClick: () -> Unit,
    onCropImageClick: (Int) -> Unit = {},
    writeViewModel: WriteViewModel = hiltViewModel(),
    bottomSheetState: BottomSheetScaffoldState,
    bottomSheetContent: (@Composable () -> Unit) -> Unit,
) {
    val isPostEditMode = postId != null
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val option = BitmapFactory.Options().apply {
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }

    val postEditId by writeViewModel.postEditId.collectAsStateWithLifecycle()
    val writeText by writeViewModel.writeText.collectAsStateWithLifecycle()
    val imageAssets by writeViewModel.writeImagesUri.collectAsStateWithLifecycle()
    val tags by writeViewModel.writeTags.collectAsStateWithLifecycle()
    val folderId by writeViewModel.writeFolderId.collectAsStateWithLifecycle()
    val folderName by writeViewModel.writeFolderName.collectAsStateWithLifecycle()
    val writePostId by writeViewModel.writePostId.collectAsState(null)
    val selectedCategory by writeViewModel.writeCategory.collectAsStateWithLifecycle() // name, index
    val onClickCategory: (CategoryMenu, Int) -> Unit = { categoryMenu, index ->
        writeViewModel.setPostCategory(Pair(categoryMenu.category, index))
        coroutineScope.launch { bottomSheetState.bottomSheetState.hide() }
    }
    val categoryMenus = listOf(
        CategoryMenu.Scheduler,
        CategoryMenu.StudyPlanner,
        CategoryMenu.PocketBook,
        CategoryMenu.SixHoleDiary,
        CategoryMenu.Digital,
        CategoryMenu.ETC
    )
    val uploadSuccess by writeViewModel.uploadSuccess.collectAsStateWithLifecycle()
    val postInfoSuccess by writeViewModel.postInfoSuccess.collectAsStateWithLifecycle(null)

    BackHandler {
        if (bottomSheetState.bottomSheetState.currentValue == SheetValue.Expanded) {
            coroutineScope.launch { bottomSheetState.bottomSheetState.hide() }
        } else {
            onBackClick()
        }
    }

    LaunchedEffect(postId) {
        if (isPostEditMode && postEditId == null) {
            writeViewModel.requestPostDetail(postId!!, categoryMenus)
        }
    }

    LaunchedEffect(uploadSuccess) {
        when (uploadSuccess) {
            Status.LOADING -> {
                snackBarHostState.showSnackbar(
                    ContextCompat.getString(context, R.string.write_post_upload_state_loading)
                )
            }

            Status.SUCCESS -> {
                onBackClick()
                writePostId?.let { newPostId ->
                    navigateToWritePost(newPostId)
                }
            }

            Status.ERROR -> {
                snackBarHostState.showSnackbar(
                    ContextCompat.getString(context, R.string.write_post_upload_state_fail)
                )
            }

            null -> {}
        }
    }

    LaunchedEffect(postInfoSuccess) {
        if (postInfoSuccess == false) {
            launch {
                snackBarHostState.showSnackbar(ContextCompat.getString(context, R.string.write_post_edit_state_fail))
            }
            onBackClick()
        }
    }

    WriteScreen(
        isPostEditMode = isPostEditMode,
        context = context,
        onBackClick = onBackClick,
        onUploadClick = {
            coroutineScope.launch {
                writeViewModel.requestUploadPost()
            }
        },
        setWriteText = { text ->
            writeViewModel.setWriteText(text)
        },
        writeText = writeText,
        addImages = { uris ->
            writeViewModel.addOriginalImages(uris)
        },
        editImage = { index ->
            onCropImageClick(index)
        },
        deleteImageUri = { position ->
            writeViewModel.removeUploadImage(position)
        },
        clearImages = {
            writeViewModel.clearUploadImage()
        },
        processedImages = imageAssets,
        navigateToCategory = {
            coroutineScope.launch { bottomSheetState.bottomSheetState.expand() }
        },
        categoryMenus = categoryMenus,
        category = selectedCategory,
        navigateToTag = { onTagClick() },
        tags = tags,
        navigateToFolder = { onWriteFolderClick() },
        folderId = folderId,
        folderName = folderName,
        uploadSuccess = uploadSuccess,
    )

    bottomSheetContent {
        CategoryBottomSheetDialog(
            categoryMenus,
            onClickCategory,
            selectedCategory,
            coroutineScope,
            bottomSheetState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryBottomSheetDialog(
    categoryMenus: List<CategoryMenu>,
    onCategorySelected: (CategoryMenu, Int) -> Unit,
    selectedCategory: Pair<Category?, Int>,
    coroutineScope: CoroutineScope,
    bottomSheetState: BottomSheetScaffoldState
) {

    BottomSheetDialog(
        sheetState = bottomSheetState,
        buttons = categoryMenus.mapIndexed { index, category ->
            Pair(category.name) {
                onCategorySelected(category, index)
            }
        },
        title = stringResource(id = R.string.category),
        leftIconButtons = categoryMenus.map {
            ImageVector.vectorResource(it.defaultIcon)
        },
        leftIconCheckedButtons = categoryMenus.map {
            ImageVector.vectorResource(it.checkedIcon)
        },
        normalColor = Gray2_767B83,
        checkedColor = Primary_23C882,
        checkedButtonIndex = selectedCategory.second,
        closeButtonAction = { coroutineScope.launch { bottomSheetState.bottomSheetState.hide() } }
    )
}

@Composable
fun WriteScreen(
    isPostEditMode: Boolean,
    context: Context = LocalContext.current,
    onBackClick: () -> Unit,
    onUploadClick: () -> Unit,
    setWriteText: (String) -> Unit,
    writeText: String,
    addImages: (List<Uri>) -> Unit,
    editImage: (Int) -> Unit = {},
    deleteImageUri: (Int) -> Unit,
    clearImages: () -> Unit = {},
    processedImages: List<ImageAsset>,
    navigateToCategory: () -> Unit,
    categoryMenus: List<CategoryMenu>,
    category: Pair<Category?, Int> = Pair(null, -1),
    navigateToTag: () -> Unit,
    tags: List<String> = emptyList(),
    navigateToFolder: () -> Unit,
    folderId: Long? = null,
    folderName: String? = null,
    uploadSuccess: Status? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val selectedUris = remember { mutableStateListOf<Uri>() }

    // 이미지 선택 런처
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                selectedUris.addAll(uris)
                clearImages()
                addImages(uris)
            } else {
                onBackClick.invoke()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!isPostEditMode && processedImages.isEmpty()) {
            imagePickerLauncher.launch(arrayOf("image/*"))
        }
    }

    Surface(
        color = White_FFFFFF,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            WriteActionbarLayout(
                title = if (isPostEditMode) stringResource(R.string.write_post_edit_title) else stringResource(R.string.write_post_title),
                rightText = if (isPostEditMode) stringResource(R.string.save) else stringResource(R.string.write_post_upload),
                onBackClick = onBackClick,
                onUploadClick = onUploadClick,
                isUploadEnable = processedImages.isNotEmpty() && category.first != null && folderId != null && folderName != null
            )
            WriteUploadImages(
                isPostEditMode = isPostEditMode,
                images = processedImages,
                deleteImage = { index -> deleteImageUri(index) },
                onEditImage = { index -> editImage(index) },
            )
            WritePostDetail(
                setWriteText = setWriteText,
                writeText = writeText
            )
            Spacer(modifier = Modifier.height(36.dp))
            WriteCategoryLayout(
                categoryMenus = categoryMenus,
                category = category.first,
                navigateToCategory = navigateToCategory
            )
            Spacer(modifier = Modifier.height(16.dp))
            Divider(
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .fillMaxWidth()
                    .height(1.dp),
                color = Gray7_F6F6F7
            )
            WriteTagLayout(
                context = context,
                tags = tags,
                navigateToTag = navigateToTag
            )
            Divider(
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .fillMaxWidth()
                    .height(1.dp),
                color = Gray7_F6F6F7
            )
            WriteFolderLayout(
                folderId = folderId,
                folderName = folderName,
                navigateToFolder = navigateToFolder
            )
        }

        // 클릭 차단 레이어
        if (uploadSuccess == Status.LOADING || uploadSuccess == Status.SUCCESS) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .zIndex(WRITE_POST_TOP_Z_INDEX) // 다른 컴포넌트 위에 표시
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource
                    ) {
                        // DO NOTHING FOR BLOCKING CLICK
                    }
            )
        }
    }
}

@Composable
fun WriteUploadImages(
    isPostEditMode: Boolean,
    images: List<ImageAsset>,
    deleteImage: (Int) -> Unit,
    onEditImage: (Int) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(WRITE_POST_IMAGE_SIZE.dp)
    ) {
        itemsIndexed(
            items = images,
            key = { _, imageAsset -> imageAsset.uriString }
        ) { index, imageAsset ->
            val context = LocalContext.current

            // 캐시 키를 uri, 수정시간, EXIF 정보로 지정해 이미지 편집하는 경우 갱신될수 있도록 수정
            val cacheKey = "${imageAsset.uriString}-${imageAsset.lastModified}-${imageAsset.exifInfo?.orientation ?: "none"}"
            val imageRequest = ImageRequest.Builder(context)
                .data(
                    if (isPostEditMode) {
                        "${BuildConfig.BASE_URL}/images/${imageAsset.uriString}"
                    } else {
                        imageAsset.uriString
                    }
                )
                .memoryCacheKey(cacheKey)
                .diskCacheKey(cacheKey)
                .crossfade(true)
                .build()

            Box(modifier = Modifier.size(WRITE_POST_IMAGE_SIZE.dp)) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = "업로드 이미지 $index",
                    modifier = Modifier
                        .size(WRITE_POST_IMAGE_SIZE.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                if (!isPostEditMode) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 16.dp, bottom = 16.dp),
                        shape = RoundedCornerShape(99.dp),
                        color = Dark,
                    ) {
                        Row(
                            modifier = Modifier
                                .width(112.dp)
                                .height(36.dp)
                                .clickable {
                                    onEditImage(index)
                                }
                                .padding(horizontal = 12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_crop),
                                contentDescription = "edit image",
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.write_post_image_edit),
                                style = DayoTheme.typography.b5,
                                color = White_FFFFFF,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                    if (images.size == WRITE_POST_IMAGE_MIN_SIZE) return@itemsIndexed

                    Image(
                        painter = painterResource(id = R.drawable.ic_x_with_circle),
                        contentDescription = "delete image",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding((11.19).dp)
                            .size((22.4).dp)
                            .clickable {
                                deleteImage(index)
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun WriteActionbarLayout(
    title: String,
    rightText: String,
    onBackClick: () -> Unit,
    onUploadClick: () -> Unit,
    isUploadEnable: Boolean = false
) {
    TopNavigation(
        title = title,
        leftIcon = {
            NoRippleIconButton(
                onClick = {
                    onBackClick()
                },
                iconContentDescription = stringResource(R.string.previous),
                iconPainter = painterResource(id = R.drawable.ic_arrow_left),
            )
        },
        rightIcon = {
            DayoTextButton(
                onClick = { if (isUploadEnable) onUploadClick() },
                text = rightText,
                textStyle = DayoTheme.typography.b3.copy(
                    textAlign = TextAlign.Center,
                    color = if (isUploadEnable) Primary_23C882 else Gray5_E8EAEE
                ),
                modifier = Modifier.padding(10.dp),
            )
        },
        titleAlignment = TopNavigationAlign.CENTER
    )
}

@Preview
@Composable
fun WritePostDetail(
    setWriteText: (String) -> Unit = {},
    writeText: String = ""
) {
    var writeContentValue by remember { mutableStateOf(TextFieldValue(writeText)) }
    LaunchedEffect(writeText) {
        if (writeText != writeContentValue.text) {
            writeContentValue = TextFieldValue(
                text = writeText,
                selection = TextRange(writeText.length)
            )
        }
    }

    Column(
        modifier = Modifier
            .padding(top = 20.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth()
            .background(color = White_FFFFFF)
    ) {
        BasicTextField(
            value = writeContentValue,
            onValueChange = {
                if (it.text.length > WRITE_POST_DETAIL_MAX_LENGTH) return@BasicTextField

                writeContentValue = it
                setWriteText(it.text)
            },
            singleLine = false,
            modifier = Modifier
                .height(height = 140.dp)
                .fillMaxWidth()
                .background(color = White_FFFFFF),
            textStyle = DayoTheme.typography.b6.copy(
                color = Dark,
            ),
            decorationBox = { innerTextField ->
                if (writeContentValue.text.isEmpty()) {
                    Text(
                        text = stringResource(R.string.write_post_detail_placeholder),
                        style = DayoTheme.typography.b6.copy(
                            color = Gray4_C5CAD2,
                        )
                    )
                }
                innerTextField()
            },
        )
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = DayoTheme.typography.caption3.toSpanStyle()
                        .copy(color = Gray2_767B83)
                ) {
                    append(writeContentValue.text.length.toString())
                }
                withStyle(
                    style = DayoTheme.typography.caption3.toSpanStyle()
                        .copy(color = Gray4_C5CAD2)
                ) {
                    append(
                        stringResource(R.string.write_post_detail_count_limit)
                            .format(WRITE_POST_DETAIL_MAX_LENGTH)
                    )
                }
            },
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.End),
            textAlign = TextAlign.End
        )
    }
}

@Preview
@Composable
fun WriteCategoryLayout(
    categoryMenus: List<CategoryMenu> = emptyList(),
    category: Category? = null,
    navigateToCategory: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(horizontal = 18.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickableSingle { navigateToCategory() },
        shape = RoundedCornerShape(20.dp),
        color = PrimaryL3_F2FBF7
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (category == null) {
                Text(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically),
                    text = stringResource(R.string.write_post_select_category_title),
                    style = DayoTheme.typography.b6,
                    color = Gray3_9FA5AE
                )
            } else {
                Image(
                    modifier = Modifier.fillMaxHeight(),
                    painter = painterResource(id = categoryMenus.first { it.category == category }.checkedIcon),
                    contentDescription = stringResource(R.string.write_post_select_category_title)
                )
                Spacer(modifier = Modifier.size(2.dp))
                Text(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically),
                    text = categoryMenus.first { it.category == category }.name,
                    style = DayoTheme.typography.b6,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Image(
                modifier = Modifier.fillMaxHeight(),
                painter = painterResource(id = R.drawable.ic_chevron_r_primary_green),
                contentDescription = stringResource(R.string.write_post_select_category_title)
            )
        }
    }
}

@Preview
@Composable
fun WriteTagLayout(
    context: Context = LocalContext.current,
    tags: List<String> = emptyList(),
    navigateToTag: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clickableSingle { navigateToTag() }
            .padding(horizontal = 18.dp)
            .fillMaxWidth()
            .height(48.dp)
            .background(White_FFFFFF)
            .padding(horizontal = 2.dp, vertical = 12.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(Alignment.CenterVertically),
            text = stringResource(R.string.write_post_select_tag_title),
            style = DayoTheme.typography.b3,
            color = Dark
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
                .widthIn(min = 54.dp)
        )
        if (tags.isNotEmpty()) {
            val tag = tags.joinToString(separator = ", ", postfix = " ") {
                ContextCompat.getString(context, R.string.write_post_select_tag_contents).format(it)
            }
            Text(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight(Alignment.CenterVertically)
                    .weight(1f),
                text = tag,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = DayoTheme.typography.b6,
                color = Dark,
                textAlign = TextAlign.End
            )
        } else {
            Text(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight(Alignment.CenterVertically)
                    .weight(1f),
                text = stringResource(R.string.write_post_select_tag_subheading),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = DayoTheme.typography.b6,
                color = Gray3_9FA5AE,
                textAlign = TextAlign.End
            )
        }
        Image(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 2.dp),
            painter = painterResource(id = R.drawable.ic_chevron_r_gray),
            contentDescription = stringResource(R.string.write_post_select_tag_title)
        )
    }
}

@Preview
@Composable
fun WriteFolderLayout(
    folderId: Long? = null,
    folderName: String? = null,
    navigateToFolder: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clickableSingle { navigateToFolder() }
            .padding(horizontal = 18.dp)
            .fillMaxWidth()
            .height(48.dp)
            .background(White_FFFFFF)
            .padding(horizontal = 2.dp, vertical = 12.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(Alignment.CenterVertically),
            text = stringResource(R.string.write_post_select_folder_title),
            style = DayoTheme.typography.b3,
            color = Dark
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
                .widthIn(min = 54.dp)
        )
        if (!folderName.isNullOrEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight(Alignment.CenterVertically)
                    .weight(1f),
                text = folderName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = DayoTheme.typography.b6,
                color = Dark,
                textAlign = TextAlign.End
            )
        } else {
            Text(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight(Alignment.CenterVertically)
                    .weight(1f),
                text = stringResource(R.string.write_post_select_folder_subheading),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = DayoTheme.typography.b6,
                color = Gray3_9FA5AE,
                textAlign = TextAlign.End
            )
        }
        Image(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 2.dp),
            painter = painterResource(id = R.drawable.ic_chevron_r_gray),
            contentDescription = stringResource(R.string.write_post_select_folder_title)
        )
    }
}