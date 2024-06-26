package daily.dayo.presentation.fragment.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.domain.model.Post
import daily.dayo.presentation.R
import daily.dayo.presentation.activity.MainActivity
import daily.dayo.presentation.adapter.FeedListAdapter
import daily.dayo.presentation.common.ReplaceUnicode.trimBlankText
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentFeedBinding
import daily.dayo.presentation.viewmodel.AccountViewModel
import daily.dayo.presentation.viewmodel.FeedViewModel
import daily.dayo.presentation.viewmodel.SearchViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {
    private var binding by autoCleared<FragmentFeedBinding> { onDestroyBindingView() }
    private val accountViewModel by activityViewModels<AccountViewModel>()
    private val feedViewModel by activityViewModels<FeedViewModel>()
    private val searchViewModel by activityViewModels<SearchViewModel>()
    private var feedListAdapter: FeedListAdapter? = null
    private var glideRequestManager: RequestManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            getFeedPostList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRvFeedListAdapter()
        setFeedPostList()
        setAdapterLoadStateListener()
        setFeedPostClickListener()
        setFeedEmptyButtonClickListener()
        setFeedRefreshListener()
        setBottomNavigationIconClickListener()
    }

    override fun onResume() {
        super.onResume()
        binding.swipeRefreshLayoutFeed.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        binding.swipeRefreshLayoutFeed.isEnabled = false
    }

    private fun onDestroyBindingView() {
        binding.rvFeedPost.adapter = null
        glideRequestManager = null
        feedListAdapter = null
    }

    private fun setFeedRefreshListener() {
        binding.swipeRefreshLayoutFeed.setOnRefreshListener {
            feedListAdapter?.refresh()
        }
    }

    private fun setRvFeedListAdapter() {
        feedListAdapter = glideRequestManager?.let { requestManager ->
            FeedListAdapter(requestManager = requestManager, currentUserInfo = accountViewModel.getCurrentUserInfo())
        }
        feedListAdapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rvFeedPost.adapter = feedListAdapter
    }

    private fun getFeedPostList() {
        feedViewModel.requestFeedList()
    }

    private fun setFeedPostList() {
        feedViewModel.feedList.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayoutFeed.isRefreshing = false
            feedListAdapter?.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun setAdapterLoadStateListener() {
        feedListAdapter?.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.NotLoading) {
                val isListEmpty = (feedListAdapter?.itemCount ?: 0 == 0)
                binding.isEmpty = isListEmpty
            }
        }
    }

    private fun setFeedEmptyButtonClickListener() {
        binding.btnFeedEmpty.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.FeedFragment,
                action = R.id.action_feedFragment_to_homeFragment
            )
        }
    }

    private fun setFeedPostClickListener() {
        feedListAdapter?.setOnItemClickListener(object : FeedListAdapter.OnItemClickListener {
            override fun likePostClick(button: ImageButton, post: Post, position: Int) {
                setFeedPostLikeClickListener(button, post, position)
            }

            override fun likeCountClick(postId: Int) {
                setLikeCountClickListener(postId)
            }

            override fun bookmarkPostClick(button: ImageButton, post: Post, position: Int) {
                setFeedPostBookmarkClickListener(button, post, position)
            }

            override fun tagPostClick(chip: Chip) {
                searchViewModel.searchKeyword = trimBlankText(chip.text.toString().substringAfter("#"))
                findNavController().navigateSafe(
                    currentDestinationId = R.id.FeedFragment,
                    action = R.id.action_feedFragment_to_searchResultFragment
                )
            }
        })
    }

    private fun setFeedPostLikeClickListener(button: ImageButton, post: Post, position: Int) {
        with(post) {
            try {
                if (!heart) {
                    feedViewModel.requestLikePost(postId!!)
                } else {
                    feedViewModel.requestUnlikePost(post.postId!!)
                }
            } catch (postIdNullException: NullPointerException) {
                Log.e(this@FeedFragment.tag, "PostId Null Exception Occurred")
                getFeedPostList()
            }
        }
    }

    fun setLikeCountClickListener(postId: Int) {
        findNavController().navigateSafe(
            currentDestinationId = R.id.FeedFragment,
            action = FeedFragmentDirections.actionFeedFragmentToPostLikeUsersFragment(postId = postId)
        )
    }

    private fun setFeedPostBookmarkClickListener(button: ImageButton, post: Post, position: Int) {
        with(post) {
            try {
                if (bookmark == false) {
                    feedViewModel.requestBookmarkPost(postId = post.postId!!)
                } else {
                    feedViewModel.requestDeleteBookmarkPost(postId = post.postId!!)
                }
            } catch (postIdNullException: NullPointerException) {
                Log.e(this@FeedFragment.tag, "PostId Null Exception Occurred")
                getFeedPostList()
            }
        }
    }

    private fun setBottomNavigationIconClickListener() {
        (requireActivity() as MainActivity).setBottomNavigationIconClickListener(R.id.FeedFragment) {
            binding.swipeRefreshLayoutFeed.isRefreshing = true
            getFeedPostList()
            setScrollToTop(isSmoothScroll = true)
        }
    }

    private fun setScrollToTop(isSmoothScroll: Boolean = false) {
        with(binding.rvFeedPost) {
            if (isSmoothScroll) this.smoothScrollToPosition(0)
            else this.scrollToPosition(0)
        }
    }
}