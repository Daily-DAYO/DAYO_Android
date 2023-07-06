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
import kotlinx.coroutines.Dispatchers

class FollowingListFragment : Fragment() {
    private var binding by autoCleared<FragmentFollowingListBinding>()
    private val followViewModel by activityViewModels<FollowViewModel>()
    private lateinit var followingListAdapter: FollowListAdapter
    private lateinit var glideRequestManager: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowingListBinding.inflate(inflater, container, false)
        setRvFollowingListAdapter()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setFollowingList()
    }

    private fun setRvFollowingListAdapter() {
        followingListAdapter = FollowListAdapter(
            requestManager = glideRequestManager,
            mainDispatcher = Dispatchers.Main,
            ioDispatcher = Dispatchers.IO
        )
        binding.rvFollowing.adapter = followingListAdapter
        followingListAdapter.setOnItemClickListener(object : FollowListAdapter.OnItemClickListener {
            override fun onItemClick(button: Button, follow: Follow, position: Int) {
                when (follow.isFollow) {
                    false -> { // 클릭 시 팔로우
                        setFollow(follow.memberId)
                    }
                    true -> { // 클릭 시 언팔로우
                        val mAlertDialog = DefaultDialogConfirm.createDialog(requireContext(), R.string.follow_delete_description_message,
                            true, true, R.string.confirm, R.string.cancel, { setUnfollow(follow.memberId) }, { })
                        if (!mAlertDialog.isShowing) {
                            mAlertDialog.show()
                            DefaultDialogConfigure.dialogResize(requireContext(), mAlertDialog, 0.7f, 0.23f)
                        }
                        mAlertDialog.setOnCancelListener {
                            mAlertDialog.dismiss()
                        }
                    }
                }
            }
        })
    }

    private fun setFollow(memberId: String) {
        followViewModel.requestCreateFollow(memberId)
        followViewModel.followSuccess.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                requestFollowingList()
            }
        }
    }

    private fun setUnfollow(memberId: String) {
        followViewModel.requestDeleteFollow(memberId)
        followViewModel.unfollowSuccess.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                requestFollowingList()
            }
        }
    }

    private fun requestFollowingList() {
        followViewModel.requestListAllFollowing(followViewModel.memberId)
    }

    private fun setFollowingList() {
        followViewModel.followingList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { followingList ->
                        binding.followingCount = followingList.size
                        followingListAdapter.submitList(followingList)
                    }
                }
            }
        }
    }
}