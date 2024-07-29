package daily.dayo.presentation.screen.write

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Surface
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.size.Size
import daily.dayo.domain.model.Category
import daily.dayo.domain.model.Folder
import daily.dayo.domain.model.categoryKR
import daily.dayo.presentation.R
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.common.toSp
import daily.dayo.presentation.theme.Gray1_313131
import daily.dayo.presentation.theme.Gray7_F6F6F7
import daily.dayo.presentation.theme.PrimaryL3_F2FBF7
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.theme.b3
import daily.dayo.presentation.theme.b6
import daily.dayo.presentation.theme.caption3
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TextButton
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import daily.dayo.presentation.viewmodel.AccountViewModel
import daily.dayo.presentation.viewmodel.ProfileViewModel
import daily.dayo.presentation.viewmodel.WriteViewModel
import kotlinx.coroutines.launch

@Composable
internal fun WriteRoute(
    onBackClick: () -> Unit,
    writeViewModel: WriteViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    accountViewModel: AccountViewModel = hiltViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    val folders by profileViewModel.folders.collectAsStateWithLifecycle()
    val imageUris by writeViewModel.writeImagesUri.collectAsState()
    val category by writeViewModel.writeCategory.collectAsState()
    val tags by writeViewModel.writeTags.collectAsState()
    val folderId by writeViewModel.writeFolderId.collectAsState()
    val folderName by writeViewModel.writeFolderName.collectAsState()
    val writePostId by writeViewModel.writePostId.observeAsState()

    writePostId?.let {
        LaunchedEffect(it) {
            onBackClick()
        }
    }

    WriteScreen(
        onBackClick = onBackClick,
        onUploadClick = {
            coroutineScope.launch {
                writeViewModel.requestUploadPost()
            }
        },
        setWriteContents = { writeContents ->
            writeViewModel.setPostContents(writeContents)
        },
        setImageUris = { uri ->
            writeViewModel.addUploadImage(uri.toString(), true)
        },
        deleteImageUri = { position ->
            writeViewModel.deleteUploadImage(position, true)
        },
        imageUris = imageUris,
        navigateToCategory = {
            // TODO - 임시 글작성을 위한 하드코딩
            writeViewModel.setPostCategory(Category.SIX_DIARY)
        },
        category = category,
        navigateToTag = {
            // TODO - 임시 글작성을 위한 하드코딩
            writeViewModel.addPostTag("test", true)
        },
        tags = tags,
        navigateToFolder = {
            // TODO - 임시 글작성을 위한 하드코딩
            profileViewModel.requestFolderList(
                memberId = accountViewModel.getCurrentUserInfo().memberId!!,
                true
            )
            folders.data?.let {
                writeViewModel.setFolderId(it.first().folderId!!.toString())
                writeViewModel.setFolderName(it.first().title)
            }
        },
        folderId = folderId,
        folderName = folderName,
    )
}

@Composable
fun WriteScreen(
    onBackClick: () -> Unit,
    onUploadClick: () -> Unit,
    setWriteContents: (String) -> Unit,
    setImageUris: (Uri) -> Unit,
    deleteImageUri: (Int) -> Unit,
    imageUris: List<String>,
    navigateToCategory: () -> Unit,
    category: Category? = null,
    navigateToTag: () -> Unit,
    tags: List<String> = emptyList(),
    navigateToFolder: () -> Unit,
    folderId: String? = null,
    folderName: String? = null,
) {

    val option = BitmapFactory.Options()
    option.inPreferredConfig = Bitmap.Config.ARGB_8888
    val bitmap = BitmapFactory.decodeResource(
        LocalContext.current.resources,
        R.drawable.ic_dayo_logo_splash,
        option
    )
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isEmpty()) onBackClick.invoke()
        uris.forEach {
            setImageUris(it)
        }
    }

    LaunchedEffect(Unit) {
        galleryLauncher.launch(arrayOf("image/*"))
    }

    Surface(
        color = colorResource(id = R.color.white_FFFFFF),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            WriteActionbarLayout(onBackClick, onUploadClick)
            WriteUploadImages(images = imageUris.map { uriString ->
                resizeBitmap(
                    BitmapFactory.decodeStream(
                        LocalContext.current.contentResolver.openInputStream(Uri.parse(uriString)),
                        null,
                        option
                    ) ?: bitmap,
                    220.dp.value.toInt(),
                    500
                )
            }, deleteImage = { index ->
                deleteImageUri(index)
            })
            WriteTextField(
                setPostContents = setWriteContents
            )
            Spacer(modifier = Modifier.height(36.dp))
            WriteCategoryLayout(
                category = category,
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
    }
}

@Composable
fun WriteUploadImages(images: List<Bitmap>, deleteImage: (Int) -> Unit = {}) {
    LazyRow(
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(220.dp)
    ) {
        itemsIndexed(images) { index, image ->
            Box(modifier = Modifier.size(220.dp)) {
                RoundImageView(
                    context = LocalContext.current,
                    imageUrl = image,
                    customModifier = Modifier.size(220.dp),
                    imageSize = Size(220, 220)
                )
                if (images.size == 1) return@itemsIndexed

                Image(
                    painter = painterResource(id = R.drawable.ic_img_delete),
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

@Composable
fun WriteActionbarLayout(onBackClick: () -> Unit, onUploadClick: () -> Unit) {
    TopNavigation(
        title = "게시글 작성",
        leftIcon = {
            NoRippleIconButton(
                onClick = { onBackClick() },
                iconContentDescription = "Previous Page",
                iconPainter = painterResource(id = R.drawable.ic_arrow_left),
            )
        },
        rightIcon = {
            TextButton(
                onClick = { onUploadClick() },
                text = "올리기",
                textStyle = MaterialTheme.typography.b3.copy(
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(10.dp),
            )
        },
        titleAlignment = TopNavigationAlign.CENTER
    )
}

@Preview
@Composable
fun WriteTextField(setPostContents: (String) -> Unit = {}) {
    var writeContentValue by remember { mutableStateOf(TextFieldValue()) }
    Column(
        modifier = Modifier
            .padding(top = 20.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.white_FFFFFF))
    ) {
        BasicTextField(
            value = writeContentValue,
            onValueChange = {
                writeContentValue = it
                setPostContents(it.text)
            },
            singleLine = false,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.white_FFFFFF))
                .defaultMinSize(minHeight = 140.dp),
            textStyle = MaterialTheme.typography.b6.copy(
                color = colorResource(id = R.color.gray_1_313131),
            ),
            decorationBox = { innerTextField ->
                if (writeContentValue.text.isEmpty()) {
                    Text(
                        text = "꾸민 다이어리에 대해 설명해주세요.",
                        style = TextStyle(
                            fontSize = 14.dp.toSp(),
                            lineHeight = 20.dp.toSp(),
                            fontFamily = FontFamily(Font(R.font.pretendard_medium)),
                            fontWeight = FontWeight(500),
                            color = colorResource(id = R.color.gray_3_9FA5AE),
                        )
                    )
                }
                innerTextField()
            },
        )
        Text(
            text = "${writeContentValue.text.length}/200",
            style = MaterialTheme.typography.caption3,
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.End),
            textAlign = TextAlign.End
        )
    }
}

@Preview
@Composable
fun WriteCategoryLayout(category: Category? = null, navigateToCategory: () -> Unit = {}) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(horizontal = 18.dp)
            .clickableSingle { navigateToCategory() },
        shape = RoundedCornerShape(20.dp),
        color = PrimaryL3_F2FBF7
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Image(
                modifier = Modifier.fillMaxHeight(),
                painter = painterResource(id = R.drawable.ic_category),
                contentDescription = "category"
            )
            Spacer(modifier = Modifier.size(2.dp))
            if (category != null) {
                Text(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically),
                    text = categoryKR(category),
                    style = MaterialTheme.typography.b6,
                    color = colorResource(id = R.color.black_000000)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Image(
                modifier = Modifier.fillMaxHeight(),
                painter = painterResource(id = R.drawable.ic_arrow_category_green),
                contentDescription = "category"
            )
        }
    }
}

@Preview
@Composable
fun WriteTagLayout(tags: List<String> = emptyList(), navigateToTag: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .padding(horizontal = 18.dp)
            .fillMaxWidth()
            .height(48.dp)
            .background(White_FFFFFF)
            .padding(horizontal = 2.dp, vertical = 12.dp)
            .clickableSingle { navigateToTag() }
    ) {
        Text(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(Alignment.CenterVertically),
            text = "태그",
            style = MaterialTheme.typography.b3,
            color = Gray1_313131
        )
        Spacer(modifier = Modifier.weight(1f))
        if (tags.isNotEmpty()) {
            val tag = tags.joinToString(prefix = "#", postfix = " ")
            Text(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight(Alignment.CenterVertically),
                text = tag,
                style = MaterialTheme.typography.b6,
                color = Gray1_313131
            )
        } else {
            Text(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight(Alignment.CenterVertically),
                text = "태그 입력",
                style = MaterialTheme.typography.b6,
                color = Gray1_313131,
                textAlign = TextAlign.Center
            )
        }
        Image(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 2.dp),
            painter = painterResource(id = R.drawable.ic_arrow_tag_gray),
            contentDescription = "tag"
        )
    }
}

@Preview
@Composable
fun WriteFolderLayout(
    folderId: String? = null,
    folderName: String? = null,
    navigateToFolder: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 18.dp)
            .fillMaxWidth()
            .height(48.dp)
            .background(White_FFFFFF)
            .padding(horizontal = 2.dp, vertical = 12.dp)
            .clickableSingle { navigateToFolder() }
    ) {
        Text(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(Alignment.CenterVertically),
            text = "폴더",
            style = MaterialTheme.typography.b3,
            color = Gray1_313131
        )
        Spacer(modifier = Modifier.weight(1f))
        if (folderName != null) {
            Text(
                text = folderName,
                style = MaterialTheme.typography.b6,
                color = Gray1_313131
            )
        } else {
            Text(
                modifier = Modifier.fillMaxHeight(),
                text = "폴더 선택",
                style = MaterialTheme.typography.b6,
                color = Gray1_313131,
                textAlign = TextAlign.Center
            )
        }
        Image(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 2.dp),
            painter = painterResource(id = R.drawable.ic_arrow_tag_gray),
            contentDescription = "tag"
        )
    }
}

fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    val ratioBitmap = width.toFloat() / height.toFloat()
    val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

    var finalWidth = maxWidth
    var finalHeight = maxHeight
    if (ratioMax > 1) {
        finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
    } else {
        finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
    }

    return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
}