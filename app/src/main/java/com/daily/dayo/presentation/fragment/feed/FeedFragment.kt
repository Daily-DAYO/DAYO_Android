package com.daily.dayo.presentation.fragment.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.R
import com.daily.dayo.common.ReplaceUnicode.trimBlankText
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentFeedBinding
import com.daily.dayo.presentation.adapter.FeedListAdapter
import com.daily.dayo.presentation.viewmodel.FeedViewModel
import com.daily.dayo.presentation.viewmodel.SearchViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.domain.model.Post
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : Fragment() {
    private var binding by autoCleared<FragmentFeedBinding> { onDestroyBindingView() }
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
            FeedListAdapter(requestManager = requestManager)
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
            findNavController().navigate(R.id.action_feedFragment_to_homeFragment)
        }
    }

    private fun setFeedPostClickListener() {
        feedListAdapter?.setOnItemClickListener(object : FeedListAdapter.OnItemClickListener {
            override fun likePostClick(button: ImageButton, post: Post, position: Int) {
                setFeedPostLikeClickListener(button, post, position)
            }

            override fun bookmarkPostClick(button: ImageButton, post: Post, position: Int) {
                setFeedPostBookmarkClickListener(button, post, position)
            }

            override fun tagPostClick(chip: Chip) {
                searchViewModel.searchKeyword = trimBlankText(chip.text.toString().substringAfter("#"))
                findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToSearchResultFragment())
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
}