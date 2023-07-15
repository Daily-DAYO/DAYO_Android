package com.daily.dayo.presentation.fragment.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.R
import com.daily.dayo.common.ReplaceUnicode.trimBlankText
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentFeedBinding
import com.daily.dayo.domain.model.Post
import com.daily.dayo.presentation.adapter.FeedListAdapter
import com.daily.dayo.presentation.viewmodel.FeedViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : Fragment() {
    private var binding by autoCleared<FragmentFeedBinding> { onDestroyBindingView() }
    private val feedViewModel by activityViewModels<FeedViewModel>()
    private var feedListAdapter: FeedListAdapter? = null
    private var glideRequestManager: RequestManager? = null

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
        getFeedPostList()
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
            FeedListAdapter(
                requestManager = requestManager,
                mainDispatcher = Dispatchers.Main,
                ioDispatcher = Dispatchers.IO
            )
        }
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
                findNavController().navigate(
                    FeedFragmentDirections.actionFeedFragmentToSearchResultFragment(
                        trimBlankText(chip.text.toString().substringAfter("#"))
                    )
                )
            }
        })
    }

    private fun setFeedPostLikeClickListener(button: ImageButton, post: Post, position: Int) {
        if (!post.heart) {
            feedViewModel.requestLikePost(postId = post.postId!!)
        } else {
            feedViewModel.requestUnlikePost(postId = post.postId!!)
        }.let {
            it.invokeOnCompletion { throwable ->
                when (throwable) {
                    is CancellationException -> Log.e("Post Like Click", "CANCELLED")
                    null -> {
                        val heartUpdate = post.heart.not()
                        val heartCountUpdate = if (heartUpdate) post.heartCount.plus(1) else post.heartCount.minus(1)
                        feedListAdapter?.updateItemAtPosition(position, post.apply {
                            heart = heartUpdate
                            heartCount = heartCountUpdate
                        })
                    }
                }
            }
        }
    }

    private fun setFeedPostBookmarkClickListener(button: ImageButton, post: Post, position: Int) {
        if (post.bookmark == null) return
        if (!post.bookmark!!) {
            feedViewModel.requestBookmarkPost(postId = post.postId!!)
        } else {
            feedViewModel.requestDeleteBookmarkPost(postId = post.postId!!)
        }.let {
            it.invokeOnCompletion { throwable ->
                when (throwable) {
                    is CancellationException -> Log.e("Post Bookmark Click", "CANCELLED")
                    null -> {
                        val bookmarkUpdate = post.bookmark?.not()
                        feedListAdapter?.updateItemAtPosition(position, post.apply {
                            bookmark = bookmarkUpdate
                        })
                    }
                }
            }
        }
    }
}