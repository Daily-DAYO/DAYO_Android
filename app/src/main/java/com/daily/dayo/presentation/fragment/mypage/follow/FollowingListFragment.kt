package com.daily.dayo.presentation.fragment.mypage.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.R
import com.daily.dayo.common.DefaultDialogConfigure
import com.daily.dayo.common.DefaultDialogConfirm
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentFollowingListBinding
import com.daily.dayo.domain.model.Follow
import com.daily.dayo.presentation.adapter.FollowListAdapter
import com.daily.dayo.presentation.viewmodel.FollowViewModel

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
    ): View? {
        binding = FragmentFollowingListBinding.inflate(inflater, container, false)
        setRvFollowingListAdapter()
        setFollowingList()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        requestFollowingList()
    }

    private fun setRvFollowingListAdapter(){
        followingListAdapter = FollowListAdapter(requestManager = glideRequestManager)
        binding.rvFollowing.adapter = followingListAdapter
        followingListAdapter.setOnItemClickListener(object : FollowListAdapter.OnItemClickListener{
            override fun onItemClick(checkbox: CheckBox, follow: Follow, position: Int) {
                when (follow.isFollow) {
                    false -> { // ?????? ??? ?????????
                        setFollow(follow.memberId)
                    }
                    true -> { // ?????? ??? ????????????
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
                requestFollowingList()
            }
        }
    }

    private fun setUnfollow(memberId: String){
        followViewModel.requestDeleteFollow(memberId)
        followViewModel.unfollowSuccess.observe(viewLifecycleOwner) {
            if(it.getContentIfNotHandled() == true) {
                requestFollowingList()
            }
        }
    }

    private fun requestFollowingList(){
        followViewModel.memberId.observe(viewLifecycleOwner) { memberId ->
            followViewModel.requestListAllFollowing(memberId)
        }
    }

    private fun setFollowingList(){
        followViewModel.followingList.observe(viewLifecycleOwner) {
            when(it.status){
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