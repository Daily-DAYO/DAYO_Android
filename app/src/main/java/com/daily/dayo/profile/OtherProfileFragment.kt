package com.daily.dayo.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentOtherProfileBinding
import com.daily.dayo.profile.adapter.ProfileFolderListAdapter
import com.daily.dayo.profile.model.Folder
import com.daily.dayo.profile.viewmodel.OtherProfileViewModel
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared
import kotlinx.coroutines.launch

class OtherProfileFragment : Fragment() {
    private var binding by autoCleared<FragmentOtherProfileBinding>()
    private val otherProfileViewModel by activityViewModels<OtherProfileViewModel>()
    private val args by navArgs<OtherProfileFragmentArgs>()
    private lateinit var profileFolderListAdapter: ProfileFolderListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtherProfileBinding.inflate(inflater,container,false)
        setRvProfileFolderListAdapter()
        setProfileFolderList()
        setOtherProfileDescription()
        setFollowButtonState()
        setFollowerButtonClickListener()
        setFollowingButtonClickListener()
        return binding.root
    }

    private fun setRvProfileFolderListAdapter(){
        val layoutManager = LinearLayoutManager(this.context)
        profileFolderListAdapter = ProfileFolderListAdapter()
        binding.rvOtherProfileFolder.adapter = profileFolderListAdapter
        binding.rvOtherProfileFolder.layoutManager = layoutManager
        profileFolderListAdapter.setOnItemClickListener(object :ProfileFolderListAdapter.OnItemClickListener{
            override fun onItemClick(v: View, folder: Folder, pos: Int) {
                findNavController().navigate(OtherProfileFragmentDirections.actionOtherProfileFragmentToFolderFragment(folder.folderId))
            }
        })
    }

    private fun setProfileFolderList(){
        otherProfileViewModel.requestAllFolderList(args.memberId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                otherProfileViewModel.folderList.observe(viewLifecycleOwner, Observer {
                    when(it.status){
                        Status.SUCCESS -> {
                            it.data?.let { folderList ->
                                profileFolderListAdapter.submitList(folderList.data)
                            }
                        }
                    }
                })
            }
        }
    }
    private fun setOtherProfileDescription() {
        otherProfileViewModel.requestOtherProfile(args.memberId)
        otherProfileViewModel.otherProfile.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    it.data?.let { userInfo ->
                        binding.userInfo = userInfo
                        Glide.with(requireContext())
                            .load("http://117.17.198.45:8080/images/" + userInfo.profileImg)
                            .into(binding.imgOtherProfileUserProfile)
                    }
                }
            }
        })
    }

    private fun setFollowButtonState(){
        binding.btnOtherProfileFollow.setOnCheckedChangeListener { buttonView, isChecked ->
            when(isChecked){
                true -> binding.btnOtherProfileFollow.text = "팔로잉"
                false -> binding.btnOtherProfileFollow.text = "팔로우"
            }
        }
    }

    private fun setFollowerButtonClickListener(){
        binding.layoutOtherProfileFollowerCount.setOnClickListener {
            findNavController().navigate(R.id.action_otherProfileFragment_to_followFragment)
        }
    }

    private fun setFollowingButtonClickListener(){
        binding.layoutOtherProfileFollowingCount.setOnClickListener {
            findNavController().navigate(R.id.action_otherProfileFragment_to_followFragment)
        }
    }
}