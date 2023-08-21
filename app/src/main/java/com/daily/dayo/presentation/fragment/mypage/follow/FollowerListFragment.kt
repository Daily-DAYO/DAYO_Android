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
import com.daily.dayo.databinding.FragmentFollowerListBinding
import daily.dayo.domain.model.MyFollower
import com.daily.dayo.presentation.adapter.FollowListAdapter
import com.daily.dayo.presentation.viewmodel.FollowViewModel

class FollowerListFragment : Fragment() {
    private var binding by autoCleared<FragmentFollowerListBinding> { onDestroyBindingView() }
    private val followViewModel by activityViewModels<FollowViewModel>()
    private var followerListAdapter: FollowListAdapter? = null
    private var glideRequestManager: RequestManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowerListBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
        setRvFollowerListAdapter()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        observeFollowerList()
        observeFollowSuccess()
        observeUnfollowSuccess()
    }

    private fun onDestroyBindingView() {
        glideRequestManager = null
        followerListAdapter = null
        binding.rvFollower.adapter = null
    }

    private fun setRvFollowerListAdapter() {
        followerListAdapter = glideRequestManager?.let { requestManager ->
            FollowListAdapter(requestManager = requestManager)
        }
        binding.rvFollower.adapter = followerListAdapter
        followerListAdapter?.setOnItemClickListener(object : FollowListAdapter.OnItemClickListener {
            override fun onItemClick(button: Button, follow: MyFollower, position: Int) {
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

    private fun requestFollow(memberId: String) {
        followViewModel.requestCreateFollow(followerId = memberId, isFollower = true)
    }

    private fun requestUnfollow(memberId: String) {
        followViewModel.requestDeleteFollow(followerId = memberId, isFollower = true)
    }

    private fun requestFollowerList() {
        followViewModel.requestListAllFollower(followViewModel.memberId)
    }

    private fun observeFollowerList() {
        followViewModel.followerList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { followerList ->
                        binding.followerCount = followerList.size
                        followerListAdapter?.submitList(followerList)
                    }
                }
                else -> {}
            }
        }
    }

    private fun observeFollowSuccess() {
        followViewModel.followerFollowSuccess.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                requestFollowerList()
            }
        }
    }

    private fun observeUnfollowSuccess() {
        followViewModel.followerUnfollowSuccess.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                requestFollowerList()
            }
        }
    }
}