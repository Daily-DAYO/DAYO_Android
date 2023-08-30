package daily.dayo.presentation.fragment.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import daily.dayo.presentation.R
import daily.dayo.presentation.common.GlideLoadUtil
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.common.toByteArray
import daily.dayo.presentation.databinding.FragmentHomeNewPostListBinding
import daily.dayo.domain.model.Category
import daily.dayo.domain.model.Post
import daily.dayo.presentation.adapter.HomeNewAdapter
import daily.dayo.presentation.viewmodel.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.presentation.activity.MainActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeNewPostListFragment : Fragment() {
    private var binding by autoCleared<FragmentHomeNewPostListBinding> { onDestroyBindingView() }
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private var homeNewAdapter: HomeNewAdapter? = null
    private var glideRequestManager: RequestManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            loadPosts(homeViewModel.currentNewCategory)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeNewPostListBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startLoadingView()
        setInitialCategory()
        setRvNewPostAdapter()
        setNewPostListCollect()
        setPostLikeClickListener()
        setEmptyViewActionClickListener()
        setNewPostListRefreshListener()
    }

    override fun onResume() {
        binding.swipeRefreshLayoutNewPost.isEnabled = true
        setBottomNavigationIconClickListener()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.swipeRefreshLayoutNewPost.isEnabled = false
    }

    private fun onDestroyBindingView() {
        glideRequestManager = null
        homeNewAdapter = null
        binding.rvNewPost.adapter = null
    }

    private fun setNewPostListRefreshListener() {
        binding.swipeRefreshLayoutNewPost.setOnRefreshListener {
            loadPosts(homeViewModel.currentNewCategory)
        }
    }

    private fun setInitialCategory() {
        with(binding) {
            radiogroupNewPostCategory.check(
                when (homeViewModel.currentNewCategory) {
                    Category.ALL -> radiobuttonNewPostCategoryAll.id
                    Category.SCHEDULER -> radiobuttonNewPostCategoryScheduler.id
                    Category.STUDY_PLANNER -> radiobuttonNewPostCategoryStudyplanner.id
                    Category.GOOD_NOTE -> radiobuttonNewPostCategoryDigital.id
                    Category.POCKET_BOOK -> radiobuttonNewPostCategoryPocketbook.id
                    Category.SIX_DIARY -> radiobuttonNewPostCategory6holediary.id
                    Category.ETC -> radiobuttonNewPostCategoryEtc.id
                }
            )
        }
    }

    private fun setRvNewPostAdapter() {
        homeNewAdapter = glideRequestManager?.let { requestManager ->
            HomeNewAdapter(
                rankingShowing = false,
                requestManager = requestManager
            )
        }
        homeNewAdapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rvNewPost.adapter = homeNewAdapter
    }

    private fun setNewPostListCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.newPostList.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { postList ->
                            binding.swipeRefreshLayoutNewPost.isRefreshing = false
                            loadPostThumbnail(postList)
                            binding.layoutNewPostEmpty.isVisible = postList.isEmpty()
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
            radiobuttonNewPostCategoryAll.setOnDebounceClickListener {
                loadPosts(Category.ALL)
            }
            radiobuttonNewPostCategoryScheduler.setOnDebounceClickListener {
                loadPosts(Category.SCHEDULER)
            }
            radiobuttonNewPostCategoryStudyplanner.setOnDebounceClickListener {
                loadPosts(Category.STUDY_PLANNER)
            }
            radiobuttonNewPostCategoryPocketbook.setOnDebounceClickListener {
                loadPosts(Category.POCKET_BOOK)
            }
            radiobuttonNewPostCategory6holediary.setOnDebounceClickListener {
                loadPosts(Category.SIX_DIARY)
            }
            radiobuttonNewPostCategoryDigital.setOnDebounceClickListener {
                loadPosts(Category.GOOD_NOTE)
            }
            radiobuttonNewPostCategoryEtc.setOnDebounceClickListener {
                loadPosts(Category.ETC)
            }
        }
    }

    private fun loadPosts(selectCategory: Category, isSmoothScroll: Boolean = false) {
        with(homeViewModel) {
            currentNewCategory = selectCategory
            requestNewPostList()
        }

        if (this.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            if (isSmoothScroll) binding.rvNewPost.smoothScrollToPosition(0)
            else binding.rvNewPost.scrollToPosition(0)
        }
    }

    private fun setPostLikeClickListener() {
        homeNewAdapter?.setOnItemClickListener(object :
            HomeNewAdapter.OnItemClickListener {
            override fun likePostClick(post: Post) {
                with(post) {
                    try {
                        if (!heart) {
                            homeViewModel.requestLikePost(postId!!, false)
                        } else {
                            homeViewModel.requestUnlikePost(post.postId!!, false)
                        }
                    } catch (postIdNullException: NullPointerException) {
                        Log.e(this@HomeNewPostListFragment.tag, "PostId Null Exception Occurred")
                        loadPosts(homeViewModel.currentNewCategory)
                    }
                }
            }
        })
    }

    private fun setEmptyViewActionClickListener() {
        binding.btnNewPostEmptyAction.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.HomeFragment,
                action = R.id.action_homeFragment_to_writeFragment
            )
        }
    }

    private fun loadPostThumbnail(postList: List<Post>) {
        val thumbnailImgList = emptyList<ByteArray>().toMutableList()
        val userImgList = emptyList<ByteArray>().toMutableList()

        viewLifecycleOwner.lifecycleScope.launch {
            for (i in 0 until (if (postList.size >= 6) 6 else postList.size)) {
                thumbnailImgList.add(withContext(Dispatchers.IO) {
                    GlideLoadUtil.loadImageBackground(
                        context = requireContext(),
                        height = 158,
                        width = 158,
                        imgName = postList[i].thumbnailImage ?: ""
                    )
                }.toByteArray)
                userImgList.add(withContext(Dispatchers.IO) {
                    GlideLoadUtil.loadImageBackground(
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
                    var loadedPostList = postList.toMutableList()
                    for (i in 0 until (if (postList.size >= 6) 6 else postList.size)) {
                        loadedPostList[i].preLoadThumbnail = thumbnailImgList[i]
                        loadedPostList[i].preLoadUserImg = userImgList[i]
                    }
                    homeNewAdapter?.submitList(postList.toMutableList())
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
            if (currentViewPagerPosition == HOME_NEW_PAGE_TAB_ID) {
                binding.swipeRefreshLayoutNewPost.isRefreshing = true
                loadPosts(homeViewModel.currentNewCategory, isSmoothScroll = true)
            }
        }
    }

    private fun startLoadingView() {
        with(binding) {
            with(layoutNewPostShimmer) {
                startShimmer()
                visibility = View.VISIBLE
            }
            rvNewPost.visibility = View.INVISIBLE
        }
    }

    private fun stopLoadingView() {
        with(binding) {
            with(layoutNewPostShimmer) {
                stopShimmer()
                visibility = View.GONE
            }
            rvNewPost.visibility = View.VISIBLE
        }
    }
}