package com.daily.dayo.presentation.fragment.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.R
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentFeedBinding
import com.daily.dayo.domain.model.Post
import com.daily.dayo.presentation.adapter.FeedListAdapter
import com.daily.dayo.presentation.viewmodel.FeedViewModel
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class FeedFragment : Fragment() {
    private var binding by autoCleared<FragmentFeedBinding>()
    private val feedViewModel by activityViewModels<FeedViewModel>()
    private lateinit var feedListAdapter: FeedListAdapter
    private lateinit var glideRequestManager: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        setRvFeedListAdapter()
        setFeedPostList()
        setFeedPostClickListener()
        setFeedEmptyButtonClickListener()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        feedViewModel.requestFeedList()
    }

    private fun setRvFeedListAdapter() {
        feedListAdapter = FeedListAdapter(requestManager = glideRequestManager)
        binding.rvFeedPost.adapter = feedListAdapter
    }

    private fun setFeedPostList() {
        viewLifecycleOwner.lifecycleScope.launch {
            feedViewModel.feedList.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { feedList ->
                            binding.feedCount = feedList.size
                            feedListAdapter.submitList(feedList.toMutableList())
                        }
                    }
                    Status.LOADING -> {
                    }
                    Status.ERROR -> {

                    }
                }
            })
        }
    }

    private fun setFeedEmptyButtonClickListener(){
        binding.btnFeedEmpty.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_homeFragment)
        }
    }

    private fun setFeedPostClickListener() {
        feedListAdapter.setOnItemClickListener(object : FeedListAdapter.OnItemClickListener {
            override fun likePostClick(button: ImageButton, post: Post, position: Int) {
                setFeedPostLikeClickListener(button, post, position)
            }

            override fun bookmarkPostClick(button: ImageButton, post: Post, position: Int) {
                setFeedPostBookmarkClickListener(button, post, position)
            }

            override fun tagPostClick(chip: Chip) {
                findNavController().navigate(
                    FeedFragmentDirections.actionFeedFragmentToSearchResultFragment(
                        chip.text.toString().substringAfter("#").trim()
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
                        feedViewModel.requestFeedList()
                    }
                }
            }
        }
    }

    private fun setFeedPostBookmarkClickListener(button: ImageButton, post: Post, position: Int) {
        if (!post.bookmark!!) {
            feedViewModel.requestBookmarkPost(postId = post.postId!!)
        } else {
            feedViewModel.requestDeleteBookmarkPost(postId = post.postId!!)
        }.let {
            it.invokeOnCompletion { throwable ->
                when (throwable) {
                    is CancellationException -> Log.e("Post Bookmark Click", "CANCELLED")
                    null -> {
                        feedViewModel.requestFeedList()
                    }
                }
            }
        }
    }
}