package com.daily.dayo.presentation.fragment.mypage.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.daily.dayo.R
import com.daily.dayo.common.DefaultDialogConfigure
import com.daily.dayo.common.DefaultDialogConfirm
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentFollowerListBinding
import com.daily.dayo.domain.model.Follow
import com.daily.dayo.presentation.adapter.FollowListAdapter
import com.daily.dayo.presentation.viewmodel.FollowViewModel

class FollowerListFragment : Fragment(){
    private var binding by autoCleared<FragmentFollowerListBinding>()
    private val followViewModel by activityViewModels<FollowViewModel>()
    private lateinit var followerListAdapter: FollowListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowerListBinding.inflate(inflater, container, false)
        setRvFollowerListAdapter()
        setFollowerList()
        return binding.root
    }

    private fun setRvFollowerListAdapter(){
        followerListAdapter = FollowListAdapter()
        binding.rvFollower.adapter = followerListAdapter
        followerListAdapter.setOnItemClickListener(object : FollowListAdapter.OnItemClickListener{
            override fun onItemClick(checkbox: CheckBox, follow: Follow, position: Int) {
                when (follow.isFollow) {
                    false -> { // 클릭 시 팔로우
                        setFollow(follow.memberId)
                    }
                    true -> { // 클릭 시 언팔로우
                        var mAlertDialog = DefaultDialogConfirm.createDialog(requireContext(), R.string.follow_delete_description_message,
                            true, true, R.string.confirm, R.string.cancel, {setUnfollow(follow.memberId)}, {checkbox.isChecked=true})
                        if(!mAlertDialog.isShowing) {
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

    private fun setFollow(memberId: String){
        followViewModel.requestCreateFollow(memberId)
        followViewModel.followSuccess.observe(viewLifecycleOwner) {
            if(it.getContentIfNotHandled() == true) {
                setFollowerList()
            }
        }
    }

    private fun setUnfollow(memberId: String){
        followViewModel.requestDeleteFollow(memberId)
        followViewModel.unfollowSuccess.observe(viewLifecycleOwner) {
            if(it.getContentIfNotHandled() == true) {
                setFollowerList()
            }
        }
    }

    private fun setFollowerList(){
        followViewModel.memberId.observe(viewLifecycleOwner) {
            followViewModel.requestListAllFollower(it)
        }

        followViewModel.followerList.observe(viewLifecycleOwner) {
            when(it.status){
                Status.SUCCESS -> {
                    it.data?.let { followerList ->
                        followerListAdapter.submitList(followerList)
                    }
                }
            }
        }
    }
}