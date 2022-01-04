package com.daily.dayo.profile

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.daily.dayo.databinding.FragmentProfileFolderListBinding
import com.daily.dayo.profile.adapter.ProfileFolderListAdapter
import com.daily.dayo.profile.viewmodel.MyProfileViewModel
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared
import kotlinx.coroutines.launch

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
    }

    private fun setProfileFolderList(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
    }
}