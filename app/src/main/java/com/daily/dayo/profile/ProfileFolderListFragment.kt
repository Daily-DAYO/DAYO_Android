package com.daily.dayo.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.daily.dayo.databinding.FragmentProfileFolderListBinding
import com.daily.dayo.util.autoCleared

class ProfileFolderListFragment : Fragment() {
    private var binding by autoCleared<FragmentProfileFolderListBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileFolderListBinding.inflate(inflater, container, false)
        return binding.root
    }
}