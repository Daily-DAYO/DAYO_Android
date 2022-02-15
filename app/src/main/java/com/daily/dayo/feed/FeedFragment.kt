package com.daily.dayo.feed

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
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentFeedBinding
import com.daily.dayo.feed.adapter.FeedListAdapter
import com.daily.dayo.feed.model.FeedContent
import com.daily.dayo.feed.viewmodel.FeedViewModel
import com.daily.dayo.post.model.RequestLikePost
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class FeedFragment : Fragment() {
    private var binding by autoCleared<FragmentFeedBinding>()
    private val feedViewModel by activityViewModels<FeedViewModel>()
    private lateinit var feedListAdapter: FeedListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        setRvFeedListAdapter()
        setFeedPostList()
        setFeedPostLikeClickListener()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        feedViewModel.requestFeedList()
    }

    private fun setRvFeedListAdapter() {
        feedListAdapter = FeedListAdapter()
        binding.rvFeedPost.adapter = feedListAdapter
    }

    private fun setFeedPostList() {
        viewLifecycleOwner.lifecycleScope.launch {
            feedViewModel.feedList.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { feedList ->
                            feedListAdapter.submitList(feedList.data?.toMutableList())
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

    private fun setFeedPostLikeClickListener() {
        feedListAdapter.setOnItemClickListener(object : FeedListAdapter.OnItemClickListener {
            override fun likePostClick(btn: ImageButton, data: FeedContent, pos: Int) {
                if (!data.heart) {
                    btn.setImageDrawable(
                        resources.getDrawable(
                            R.drawable.ic_post_like_checked,
                            context?.theme
                        )
                    )
                    feedViewModel.requestLikePost(RequestLikePost(data.id))
                } else {
                    btn.setImageDrawable(
                        resources.getDrawable(
                            R.drawable.ic_post_like_default,
                            context?.theme
                        )
                    )
                    feedViewModel.requestUnlikePost(data.id)
                }.let { it.invokeOnCompletion { throwable ->
                    when (throwable) {
                        is CancellationException -> Log.e("Post Like Click", "CANCELLED")
                        null -> {
                            feedViewModel.requestFeedList()
                            }
                        }
                    }
                }
            }
        })
    }

}