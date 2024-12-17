package daily.dayo.presentation.screen.write

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.size.Size
import daily.dayo.domain.model.Folder
import daily.dayo.domain.model.Privacy
import daily.dayo.presentation.R
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.common.extension.limitTo
import daily.dayo.presentation.theme.Dark
import daily.dayo.presentation.theme.DayoTheme
import daily.dayo.presentation.theme.Gray2_767B83
import daily.dayo.presentation.theme.Gray3_9FA5AE
import daily.dayo.presentation.theme.Primary_23C882
import daily.dayo.presentation.theme.White_FFFFFF
import daily.dayo.presentation.view.NoRippleIconButton
import daily.dayo.presentation.view.RoundImageView
import daily.dayo.presentation.view.TopNavigation
import daily.dayo.presentation.view.TopNavigationAlign
import daily.dayo.presentation.viewmodel.AccountViewModel
import daily.dayo.presentation.viewmodel.ProfileViewModel
import daily.dayo.presentation.viewmodel.WriteViewModel

const val MAX_FOLDER_COUNT = 5
const val FOLDER_THUMBNAIL_SIZE = 72
const val FOLDER_THUMBNAIL_RADIUS_SIZE = 12

@Composable
fun WriteFolderRoute(
    onBackClick: () -> Unit,
    onWriteFolderNewClick: () -> Unit,
    writeViewModel: WriteViewModel = hiltViewModel(),
    accountViewModel: AccountViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
) {
    val folders by profileViewModel.folders.collectAsStateWithLifecycle()
    val folderId by writeViewModel.writeFolderId.collectAsState()
    val writePostId by writeViewModel.writePostId.observeAsState()

    LaunchedEffect(Unit) {
        profileViewModel.requestFolderList(
            memberId = accountViewModel.getCurrentUserInfo().memberId!!,
            true
        )
    }

    WriteFolderScreen(
        onBackClick = onBackClick,
        onFolderClick = { folderId, folderName ->
            writeViewModel.setFolderId(folderId)
            writeViewModel.setFolderName(folderName)
            onBackClick()
        },
        navigateToCreateNewFolder = { onWriteFolderNewClick() },
        folders = folders.data.orEmpty(),
        currentFolderId = folderId
    )

}

@Composable
fun WriteFolderScreen(
    onBackClick: () -> Unit,
    onFolderClick: (String, String) -> Unit,
    navigateToCreateNewFolder: () -> Unit,
    folders: List<Folder>,
    currentFolderId: String?,
) {
    Surface(
        color = White_FFFFFF,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            WriteFolderActionbarLayout(
                onBackClick = onBackClick
            )
            WriteFolderContent(
                onFolderClick = onFolderClick,
                navigateToCreateNewFolder = navigateToCreateNewFolder,
                folders = folders,
                currentFolderId = currentFolderId
            )
        }
    }
}

@Composable
fun WriteFolderActionbarLayout(onBackClick: () -> Unit) {
    Column {
        TopNavigation(
            leftIcon = {
                NoRippleIconButton(
                    onClick = { onBackClick() },
                    iconContentDescription = stringResource(R.string.previous),
                    iconPainter = painterResource(id = R.drawable.ic_arrow_left),
                )
            },
            title = stringResource(R.string.write_post_folder_title),
            titleAlignment = TopNavigationAlign.CENTER
        )
    }
}

@Preview
@Composable
fun WriteFolderContent(
    onFolderClick: (String, String) -> Unit = { _, _ -> },
    navigateToCreateNewFolder: () -> Unit = {},
    folders: List<Folder> = emptyList(),
    currentFolderId: String? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
    ) {
        if (folders.size < MAX_FOLDER_COUNT) {
            WriteFolderNewLayout(
                navigateToCreateNewFolder = navigateToCreateNewFolder
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )
        }
        WriteFoldersLayout(
            onFolderClick = onFolderClick,
            folders = folders.limitTo(MAX_FOLDER_COUNT), // 5개이상 오더라도 5개까지만 보여주도록 클라이언트 측도 처리
            currentFolderId = currentFolderId
        )
    }
}

@Preview
@Composable
fun WriteFolderNewLayout(
    navigateToCreateNewFolder: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(FOLDER_THUMBNAIL_SIZE.dp)
            .background(White_FFFFFF)
            .clickableSingle { navigateToCreateNewFolder() },
    ) {
        Image(
            contentDescription = "new folderThumbnail",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(FOLDER_THUMBNAIL_SIZE.dp)
                .clip(RoundedCornerShape(FOLDER_THUMBNAIL_RADIUS_SIZE.dp)),
            painter = painterResource(id = R.drawable.ic_folder_new),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .wrapContentWidth(Alignment.Start)
                .wrapContentHeight(Alignment.CenterVertically),
            text = stringResource(R.string.write_post_folder_navigate_create_folder),
            style = DayoTheme.typography.b4.copy(
                color = Gray2_767B83,
                textAlign = TextAlign.Center
            )
        )
    }

}

@Composable
fun WriteFoldersLayout(
    onFolderClick: (String, String) -> Unit,
    folders: List<Folder>,
    currentFolderId: String?,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(folders) { _, folder ->
            WriteFolderItemLayout(
                folder = folder,
                isSelected = folder.folderId?.toString() == currentFolderId,
                onFolderClick = onFolderClick
            )
        }
    }

}

@Preview
@Composable
fun WriteFolderItemLayout(
    folder: Folder = Folder(
        folderId = 1,
        title = "",
        memberId = "",
        privacy = Privacy.ONLY_ME,
        subheading = "",
        thumbnailImage = "",
        postCount = 1
    ),
    isSelected: Boolean = true,
    onFolderClick: (String, String) -> Unit = { _, _ -> },
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(FOLDER_THUMBNAIL_SIZE.dp)
            .clip(RoundedCornerShape(FOLDER_THUMBNAIL_RADIUS_SIZE.dp))
            .background(White_FFFFFF)
            .clickableSingle { onFolderClick(folder.folderId.toString(), folder.title) }
    ) {
        Box(
            modifier = Modifier
                .size(FOLDER_THUMBNAIL_SIZE.dp)
        ) {
            RoundImageView(
                context = LocalContext.current,
                imageUrl = folder.thumbnailImage,
                customModifier = Modifier.size(FOLDER_THUMBNAIL_SIZE.dp),
                imageSize = Size(FOLDER_THUMBNAIL_SIZE, FOLDER_THUMBNAIL_SIZE),
                roundSize = FOLDER_THUMBNAIL_RADIUS_SIZE.dp,
            )
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(FOLDER_THUMBNAIL_SIZE.dp)
                        .clip(RoundedCornerShape(size = FOLDER_THUMBNAIL_RADIUS_SIZE.dp))
                        .background(Primary_23C882.copy(alpha = 0.6f))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_check_mark),
                        contentDescription = "Selected",
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(White_FFFFFF),
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentHeight(Alignment.CenterVertically)
        ) {
            Row {
                if (folder.privacy == Privacy.ONLY_ME) {
                    Image(
                        modifier = Modifier
                            .height(24.dp)
                            .wrapContentHeight(Alignment.CenterVertically),
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = "Only Me",
                        colorFilter = ColorFilter.tint(Dark)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .wrapContentWidth(Alignment.Start)
                        .wrapContentHeight(Alignment.CenterVertically),
                    text = folder.title,
                    style = DayoTheme.typography.b4.copy(
                        color = Dark,
                    )
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .wrapContentWidth(Alignment.Start)
                    .wrapContentHeight(Alignment.CenterVertically),
                text = stringResource(R.string.write_post_folder_detail_current_count).format(folder.postCount),
                style = DayoTheme.typography.b6.copy(
                    color = Gray3_9FA5AE,
                )
            )
        }
    }
}