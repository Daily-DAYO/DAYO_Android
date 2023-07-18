package com.daily.dayo.presentation.fragment.mypage.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.R
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.dialog.DefaultDialogConfigure
import com.daily.dayo.common.dialog.DefaultDialogConfirm
import com.daily.dayo.databinding.FragmentFollowingListBinding
import com.daily.dayo.domain.model.Follow
import com.daily.dayo.presentation.adapter.FollowListAdapter
import com.daily.dayo.presentation.viewmodel.FollowViewModel

class FollowingListFragment : Fragment() {
    private var binding by autoCleared<FragmentFollowingListBinding> { onDestroyBindingView() }
    private val followViewModel by activityViewModels<FollowViewModel>()
    private var followingListAdapter: FollowListAdapter? = null
    private var glideRequestManager: RequestManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowingListBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
        setRvFollowingListAdapter()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        observeFollowingList()
        observeFollowSuccess()
        observeUnfollowSuccess()
    }

    private fun onDestroyBindingView() {
        glideRequestManager = null
        followingListAdapter = null
        binding.rvFollowing.adapter = null
    }

    private fun setRvFollowingListAdapter() {
        followingListAdapter = glideRequestManager?.let { requestManager ->
            FollowListAdapter(requestManager = requestManager)
        }
        binding.rvFollowing.adapter = followingListAdapter
        followingListAdapter?.setOnItemClickListener(object :
            FollowListAdapter.OnItemClickListener {
            override fun onItemClick(button: Button, follow: Follow, position: Int) {
                when (follow.isFollow) {
                    false -> { // 클릭 시 팔로우
                        requestFollow(follow.memberId)
                    }
                    true -> { // 클릭 시 언팔로우
                        val mAlertDialog = DefaultDialogConfirm.createDialog(requireContext(),
                            R.string.follow_delete_description_message,
                            true,
                            true,
                            R.string.confirm,
                            R.string.cancel,
                            { requestUnfollow(follow.memberId) },
                            { })
                        if (!mAlertDialog.isShowing) {
                            mAlertDialog.show()
                            DefaultDialogConfigure.dialogResize(
                                requireContext(),
                                mAlertDialog,
                                0.7f,
                                0.23f
                            )
                        }
                        mAlertDialog.setOnCancelListener {
                            mAlertDialog.dismiss()
                        }
                    }
                }
            }
        })
    }

    private fun requestFollowingList() {
        followViewModel.requestListAllFollowing(followViewModel.memberId)
    }

    private fun requestFollow(memberId: String) {
        followViewModel.requestCreateFollow(followerId = memberId, isFollower = false)
    }

    private fun requestUnfollow(memberId: String) {
        followViewModel.requestDeleteFollow(followerId = memberId, isFollower = false)
    }

    private fun observeFollowingList() {
        followViewModel.followingList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { followingList ->
                        binding.followingCount = followingList.size
                        followingListAdapter?.submitList(followingList)
                    }
                }
                else -> {}
            }
        }
    }

    private fun observeFollowSuccess() {
        followViewModel.followingFollowSuccess.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                requestFollowingList()
            }
        }
    }

    private fun observeUnfollowSuccess() {
        followViewModel.followingUnfollowSuccess.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                requestFollowingList()
            }
        }
    }
}