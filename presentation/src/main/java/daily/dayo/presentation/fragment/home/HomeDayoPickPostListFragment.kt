package daily.dayo.presentation.fragment.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.domain.model.Category
import daily.dayo.domain.model.Post
import daily.dayo.presentation.R
import daily.dayo.presentation.activity.MainActivity
import daily.dayo.presentation.adapter.HomeDayoPickAdapter
import daily.dayo.presentation.common.GlideLoadUtil.loadImageBackground
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.common.toByteArray
import daily.dayo.presentation.databinding.FragmentHomeDayoPickPostListBinding
import daily.dayo.presentation.view.EmojiView
import daily.dayo.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeDayoPickPostListFragment : Fragment() {
    private var binding by autoCleared<FragmentHomeDayoPickPostListBinding>() { onDestroyBindingView() }
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private var homeDayoPickAdapter: HomeDayoPickAdapter? = null
    private var glideRequestManager: RequestManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            loadPosts(homeViewModel.currentDayoPickCategory)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeDayoPickPostListBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    HomeDayoPickScreen()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startLoadingView()
        setInitialCategory()
        setRvDayoPickPostAdapter()
        setDayoPickPostListCollect()
        setPostLikeClickListener()
        setEmptyViewActionClickListener()
        setDayoPickPostListRefreshListener()
    }

    override fun onResume() {
        binding.swipeRefreshLayoutDayoPickPost.isEnabled = true
        setBottomNavigationIconClickListener()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.swipeRefreshLayoutDayoPickPost.isEnabled = false
    }

    private fun onDestroyBindingView() {
        glideRequestManager = null
        homeDayoPickAdapter = null
        binding.rvDayopickPost.adapter = null
    }

    @Composable
    private fun HomeDayoPickScreen() {
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
            }
        }
    }

    private fun setDayoPickPostListRefreshListener() {
        binding.swipeRefreshLayoutDayoPickPost.setOnRefreshListener {
            loadPosts(homeViewModel.currentDayoPickCategory)
        }
    }

    private fun setInitialCategory() {
        with(binding) {
            radiogroupDayopickPostCategory.check(
                when (homeViewModel.currentDayoPickCategory) {
                    Category.ALL -> radiobuttonDayopickPostCategoryAll.id
                    Category.SCHEDULER -> radiobuttonDayopickPostCategoryScheduler.id
                    Category.STUDY_PLANNER -> radiobuttonDayopickPostCategoryStudyplanner.id
                    Category.GOOD_NOTE -> radiobuttonDayopickPostCategoryDigital.id
                    Category.POCKET_BOOK -> radiobuttonDayopickPostCategoryPocketbook.id
                    Category.SIX_DIARY -> radiobuttonDayopickPostCategory6holediary.id
                    Category.ETC -> radiobuttonDayopickPostCategoryEtc.id
                }
            )
        }
    }

    private fun setRvDayoPickPostAdapter() {
        homeDayoPickAdapter = glideRequestManager?.let { requestManager ->
            HomeDayoPickAdapter(
                rankingShowing = true,
                requestManager = requestManager
            )
        }
        homeDayoPickAdapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rvDayopickPost.adapter = homeDayoPickAdapter
    }

    private fun setDayoPickPostListCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.dayoPickPostList.observe(viewLifecycleOwner) {
                // TODO : 하단 BottomNavigation을 통해 Fragment를 이동하고 나서 돌아오고나서 카테고리를 선택하면 observe가 중복해서 생겨난다
                //  해당 LiveData 객체가 1개 더 생성되는듯 하다
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { postList ->
                            binding.swipeRefreshLayoutDayoPickPost.isRefreshing = false
                            loadInitialPostThumbnail(postList)
                            binding.layoutDayopickPostEmpty.isVisible = postList.isEmpty()
                        }
                    }

                    Status.LOADING -> {
                    }

                    Status.ERROR -> {

                    }
                }
            }
        }
        with(binding) {
            radiobuttonDayopickPostCategoryAll.setOnDebounceClickListener {
                loadPosts(Category.ALL)
            }
            radiobuttonDayopickPostCategoryScheduler.setOnDebounceClickListener {
                loadPosts(Category.SCHEDULER)
            }
            radiobuttonDayopickPostCategoryStudyplanner.setOnDebounceClickListener {
                loadPosts(Category.STUDY_PLANNER)
            }
            radiobuttonDayopickPostCategoryPocketbook.setOnDebounceClickListener {
                loadPosts(Category.POCKET_BOOK)
            }
            radiobuttonDayopickPostCategory6holediary.setOnDebounceClickListener {
                loadPosts(Category.SIX_DIARY)
            }
            radiobuttonDayopickPostCategoryDigital.setOnDebounceClickListener {
                loadPosts(Category.GOOD_NOTE)
            }
            radiobuttonDayopickPostCategoryEtc.setOnDebounceClickListener {
                loadPosts(Category.ETC)
            }
        }
    }

    private fun loadPosts(selectCategory: Category, isSmoothScroll: Boolean = false) {
        with(homeViewModel) {
            currentDayoPickCategory = selectCategory
            requestDayoPickPostList()
        }

        if (this.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            if (isSmoothScroll) binding.rvDayopickPost.smoothScrollToPosition(0)
            else binding.rvDayopickPost.scrollToPosition(0)
        }
    }

    private fun setPostLikeClickListener() {
        homeDayoPickAdapter?.setOnItemClickListener(object :
            HomeDayoPickAdapter.OnItemClickListener {
            override fun likePostClick(post: Post) {
                with(post) {
                    try {
                        if (!heart) {
                            homeViewModel.requestLikePost(postId!!, isDayoPickLike = true)
                        } else {
                            homeViewModel.requestUnlikePost(postId!!, isDayoPickLike = true)
                        }
                    } catch (postIdNullException: NullPointerException) {
                        Log.e(
                            this@HomeDayoPickPostListFragment.tag,
                            "PostId Null Exception Occurred"
                        )
                        loadPosts(homeViewModel.currentDayoPickCategory)
                    }
                }
            }
        })
    }

    private fun setEmptyViewActionClickListener() {
        binding.btnDayopickPostEmptyAction.setOnDebounceClickListener {
            val homeViewPager =
                requireParentFragment().requireView().findViewById<ViewPager2>(R.id.pager_home_post)
            val current = homeViewPager.currentItem
            if (current == 0) {
                homeViewPager.setCurrentItem(1, true)
            } else {
                homeViewPager.setCurrentItem(current - 1, true)
            }
        }
    }

    private fun loadInitialPostThumbnail(postList: List<Post>) {
        val thumbnailImgList = emptyList<ByteArray>().toMutableList()
        val userImgList = emptyList<ByteArray>().toMutableList()

        viewLifecycleOwner.lifecycleScope.launch {
            for (i in 0 until (if (postList.size >= 6) 6 else postList.size)) {
                thumbnailImgList.add(withContext(Dispatchers.IO) {
                    loadImageBackground(
                        context = requireContext(),
                        height = 158,
                        width = 158,
                        imgName = postList[i].thumbnailImage ?: ""
                    )
                }.toByteArray)
                userImgList.add(withContext(Dispatchers.IO) {
                    loadImageBackground(
                        context = requireContext(),
                        height = 17,
                        width = 17,
                        imgName = postList[i].userProfileImage ?: ""
                    )
                }.toByteArray)
            }
        }.invokeOnCompletion { throwable ->
            when (throwable) {
                is CancellationException -> Log.e("Image Loading", "CANCELLED")
                null -> {
                    for (i in 0 until (if (postList.size >= 6) 6 else postList.size)) {
                        with(postList[i]) {
                            preLoadThumbnail = thumbnailImgList[i]
                            preLoadUserImg = userImgList[i]
                        }
                    }
                    homeDayoPickAdapter?.submitList(postList.toMutableList())
                    stopLoadingView()
                    thumbnailImgList.clear()
                    userImgList.clear()
                }
            }
        }
    }

    private fun setBottomNavigationIconClickListener() {
        val currentViewPagerPosition =
            requireParentFragment().requireView()
                .findViewById<ViewPager2>(R.id.pager_home_post)
                .currentItem

        (requireActivity() as MainActivity).setBottomNavigationIconClickListener(reselectedIconId = R.id.HomeFragment) {
            if (currentViewPagerPosition == HOME_DAYOPICK_PAGE_TAB_ID) {
                binding.swipeRefreshLayoutDayoPickPost.isRefreshing = true
                loadPosts(homeViewModel.currentDayoPickCategory, isSmoothScroll = true)
            }
        }
    }

    private fun startLoadingView() {
        with(binding) {
            with(layoutDayopickPostShimmer) {
                startShimmer()
                visibility = View.VISIBLE
            }
            rvDayopickPost.visibility = View.INVISIBLE
        }
    }

    private fun stopLoadingView() {
        with(binding) {
            with(layoutDayopickPostShimmer) {
                stopShimmer()
                visibility = View.GONE
            }
            rvDayopickPost.visibility = View.VISIBLE
        }
    }

    @Composable
    @Preview(showBackground = true)
    private fun PreviewHomeDayoPickScreen() {
        MaterialTheme {
            HomeDayoPickScreen()
        }
    }
}