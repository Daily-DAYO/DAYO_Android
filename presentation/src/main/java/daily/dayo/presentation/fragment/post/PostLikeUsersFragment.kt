package daily.dayo.presentation.fragment.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Event
import daily.dayo.presentation.common.extension.clickableSingle
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.toSp
import daily.dayo.presentation.viewmodel.FollowViewModel
import daily.dayo.presentation.viewmodel.PostViewModel
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.domain.model.LikeUser
import daily.dayo.presentation.viewmodel.AccountViewModel

@AndroidEntryPoint
class PostLikeUsersFragment : Fragment() {
    private val args by navArgs<PostLikeUsersFragmentArgs>()
    private val accountViewModel by activityViewModels<AccountViewModel>()
    private val postViewModel by activityViewModels<PostViewModel>()
    private val followViewModel by activityViewModels<FollowViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requestPostLikeUsers()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    Scaffold(topBar = { SetPostLikeUsersActionbar() }) { contentPadding ->
                        Box(modifier = Modifier.padding(contentPadding)) {
                            SetPostLikeUsers()
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun SetPostLikeUsersActionbar() {
        val interactionSource = remember { MutableInteractionSource() }
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = colorResource(id = R.color.white_FFFFFF)
            ),
            title = {
                Text(
                    text = stringResource(id = R.string.post_like_users_title),
                    style = TextStyle(
                        fontSize = 16.dp.toSp(),
                        lineHeight = 24.dp.toSp(),
// TODO                fontFamily = FontFamily(Font(R.font.pretendard)),
                        fontWeight = FontWeight(600),
                        color = colorResource(id = R.color.gray_1_313131),
                        textAlign = TextAlign.Center
                    )
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { findNavController().navigateUp() },
                    modifier = Modifier
                        .indication(interactionSource = interactionSource, indication = null)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back_sign),
                        contentDescription = "back sign",
                    )
                }
            }
        )
    }

    @Composable
    private fun SetPostLikeUsers() {
        val likeUsers = postViewModel.postLikeUsers.collectAsLazyPagingItems()
        when (likeUsers.loadState.refresh) {
            is LoadState.Loading -> {}
            is LoadState.Error -> {}
            is LoadState.NotLoading -> {
                SetPostLikeUsersLayout(likeUsers = likeUsers)
            }
        }
    }

    @Composable
    private fun SetPostLikeUsersLayout(likeUsers: LazyPagingItems<LikeUser>) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.white_FFFFFF))
        ) {
            items(likeUsers.itemCount) { index ->
                val item = likeUsers[index]
                LikeUserLayout(likeUser = item!!)
            }
        }
    }

    @Composable
    private fun LikeUserLayout(likeUser: LikeUser) {
        Surface(
            color = colorResource(id = R.color.white_FFFFFF),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableSingle { navigateUserProfile(likeUser.memberId) }
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                LikeUserImageLayout(likeUser = likeUser)
                LikeUserNicknameLayout(userNickname = likeUser.nickname)
                Spacer(modifier = Modifier.weight(1f))
                if (likeUser.memberId != accountViewModel.getCurrentUserInfo().memberId) {
                    LikeUserFollowLayout(likeUser = likeUser)
                }
            }
        }
    }

    @Composable
    private fun LikeUserImageLayout(likeUser: LikeUser) {
        val imageInteractionSource = remember { MutableInteractionSource() }

        GlideImage(
            imageModel = { "${BuildConfig.BASE_URL}/images/${likeUser.profileImg}" },
            imageOptions = ImageOptions(
                contentDescription = "image description",
                contentScale = ContentScale.Crop,
            ),
            modifier = Modifier
                .padding(1.dp)
                .height(USER_THUMBNAIL_SIZE.dp)
                .aspectRatio(1f)
                .clip(CircleShape)
                .clickableSingle(
                    interactionSource = imageInteractionSource,
                    indication = null,
                    onClick = { navigateUserProfile(likeUser.memberId) }
                )
        )
    }

    @Composable
    private fun LikeUserNicknameLayout(userNickname: String) {
        Text(
            text = userNickname,
            style = TextStyle(
                fontSize = 13.dp.toSp(),
// TODO                        fontFamily = FontFamily(Font(R.font.sandoll gothicneo1)),
                fontWeight = FontWeight(400),
                color = colorResource(id = R.color.gray_1_313131),
            ),
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 11.dp)
        )
    }

    @Composable
    private fun LikeUserFollowLayout(likeUser: LikeUser) {
        val followInteractionSource = remember { MutableInteractionSource() }
        val followIsPressed by followInteractionSource.collectIsPressedAsState()

        var followState by rememberSaveable { mutableStateOf(likeUser.follow) }
        val followSuccess by followViewModel.followingFollowSuccess.observeAsState(Event(true))
        val unFollowSuccess by followViewModel.followerUnfollowSuccess.observeAsState(Event(true))

        TextButton(
            onClick = {
                if (followState) {
                    followViewModel.requestUnfollow(
                        followerId = likeUser.memberId,
                        isFollower = true
                    )
                    if (unFollowSuccess.getContentIfNotHandled() == true) {
                        followState = false
                    }
                } else {
                    followViewModel.requestFollow(
                        followerId = likeUser.memberId,
                        isFollower = false
                    )
                    if (followSuccess.getContentIfNotHandled() == true) {
                        followState = true
                    }
                }
            },
            interactionSource = followInteractionSource,
            colors = ButtonDefaults.buttonColors(
                if (followState or (!followState and followIsPressed)) colorResource(id = R.color.white_FFFFFF)
                else colorResource(id = R.color.primary_green_23C882)
            ),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .defaultMinSize(1.dp)
                .border(
                    width = 1.dp,
                    color =
                    if (followState or (!followState and followIsPressed)) colorResource(id = R.color.gray_3_9FA5AE)
                    else colorResource(id = R.color.primary_green_23C882),
                    shape = RoundedCornerShape(size = FOLLOW_BUTTON_RADIUS_SIZE.dp),
                )
                .background(
                    color =
                    if (followState or (!followState and followIsPressed)) colorResource(id = R.color.white_FFFFFF)
                    else colorResource(id = R.color.primary_green_23C882),
                    shape = RoundedCornerShape(size = FOLLOW_BUTTON_RADIUS_SIZE.dp)
                )
                .width(FOLLOW_BUTTON_WIDTH.dp)
                .height(FOLLOW_BUTTON_HEIGHT.dp)
        ) {
            Text(
                text = if (followState) stringResource(id = R.string.follow_already)
                else stringResource(id = R.string.follow_yet),
                style = TextStyle(
                    fontSize = 14.dp.toSp(),
                    lineHeight = 21.dp.toSp(),
// TODO                           fontFamily = FontFamily(Font(R.font.pretendard)),
                    fontWeight = FontWeight(600),
                    color = if (followState or (!followState and followIsPressed)) colorResource(id = R.color.gray_3_9FA5AE)
                    else colorResource(id = R.color.white_FFFFFF),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier
            )
        }
    }

    @Composable
    @Preview
    private fun PreviewLikeUserLayout() {
        MaterialTheme {
            LikeUserLayout(
                likeUser = LikeUser(
                    false,
                    "sample-member-id",
                    "sample-Nickname",
                    "sampleImgUrl.jpg"
                ),
            )
        }
    }

    @Composable
    @Preview
    private fun PreviewActionbarLayout() {
        MaterialTheme {
            SetPostLikeUsersActionbar()
        }
    }

    private fun requestPostLikeUsers() {
        postViewModel.requestPostLikeUsers(postId = args.postId)
    }

    private fun navigateUserProfile(memberId: String) {
        findNavController().navigateSafe(
            currentDestinationId = R.id.PostLikeUsersFragment,
            action = PostLikeUsersFragmentDirections.actionPostLikeUsersFragmentToProfileFragment(
                memberId = memberId
            )
        )
    }

    companion object {
        const val USER_THUMBNAIL_SIZE = 45
        const val FOLLOW_BUTTON_HEIGHT = 30
        const val FOLLOW_BUTTON_WIDTH = 85
        const val FOLLOW_BUTTON_RADIUS_SIZE = 31
    }
}