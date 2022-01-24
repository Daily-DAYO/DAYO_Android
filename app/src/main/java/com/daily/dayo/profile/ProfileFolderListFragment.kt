package com.daily.dayo.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.daily.dayo.databinding.FragmentProfileFolderListBinding
import com.daily.dayo.profile.adapter.ProfileFolderListAdapter
import com.daily.dayo.profile.model.Folder
import com.daily.dayo.profile.viewmodel.MyProfileViewModel
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared

class ProfileFolderListFragment : Fragment() {
    private var binding by autoCleared<FragmentProfileFolderListBinding>()
    private val myProfileViewModel by activityViewModels<MyProfileViewModel>()
    private lateinit var profileFolderListAdapter: ProfileFolderListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileFolderListBinding.inflate(inflater, container, false)
        setRvProfileFolderListAdapter()
        setProfileFolderList()
        return binding.root
    }

    private fun setRvProfileFolderListAdapter() {
        val layoutManager = LinearLayoutManager(this.context)
        profileFolderListAdapter = ProfileFolderListAdapter()
        binding.rvProfileFolder.adapter = profileFolderListAdapter
        binding.rvProfileFolder.layoutManager = layoutManager
        profileFolderListAdapter.setOnItemClickListener(object :ProfileFolderListAdapter.OnItemClickListener{
            override fun onItemClick(v: View, folder: Folder, pos: Int) {
                findNavController().navigate(MyProfileFragmentDirections.actionMyProfileFragmentToFolderFragment(folder.folderId))
            }
        })
    }

    private fun setProfileFolderList(){
        myProfileViewModel.folderList.observe(viewLifecycleOwner, Observer {
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