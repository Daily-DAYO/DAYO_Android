package com.daily.dayo.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentFollowingListBinding
import com.daily.dayo.follow.adapter.FollowListAdapter
import com.daily.dayo.follow.viewmodel.FollowViewModel
import com.daily.dayo.profile.model.FollowInfo
import com.daily.dayo.util.DefaultDialogConfigure
import com.daily.dayo.util.DefaultDialogConfirm
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared

class FollowingListFragment : Fragment() {
    private var binding by autoCleared<FragmentFollowingListBinding>()
    private val followViewModel by activityViewModels<FollowViewModel>()
    private lateinit var followingListAdapter: FollowListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowingListBinding.inflate(inflater, container, false)
        setRvFollowingListAdapter()
        setFollowingList()
        return binding.root
    }

    private fun setRvFollowingListAdapter(){
        followingListAdapter = FollowListAdapter()
        binding.rvFollowing.adapter = followingListAdapter
        followingListAdapter.setOnItemClickListener(object : FollowListAdapter.OnItemClickListener{
            override fun onItemClick(checkbox: CheckBox, followInfo: FollowInfo, pos: Int) {
                checkbox.setOnCheckedChangeListener { compoundButton, b ->
                    when (compoundButton.isChecked) {
                        true and !followInfo.isFollow -> { // 클릭 시 팔로우
                            setFollow(followInfo.memberId)
                        }
                        !false and followInfo.isFollow -> { // 클릭 시 언팔로우
                            var mAlertDialog = DefaultDialogConfirm.createDialog(requireContext(), R.string.follow_delete_description_message,
                                true, true, R.string.confirm, R.string.cancel, {setUnfollow(followInfo.memberId)}, {checkbox.isChecked=true})
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
                followingListAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun setFollow(memberId: String){
        followViewModel.requestCreateFollow(memberId)
    }

    private fun setUnfollow(memberId: String){
        followViewModel.requestDeleteFollow(memberId)
    }


    private fun setFollowingList(){
        followViewModel.memberId.observe(viewLifecycleOwner, Observer {
            followViewModel.requestListAllFollowing(it)
        })

        followViewModel.followingList.observe(viewLifecycleOwner, Observer {
            when(it.status){
                Status.SUCCESS -> {
                    it.data?.let { followingList ->
                        followingListAdapter.submitList(followingList.data)
                    }
                }
            }
        })
    }
}