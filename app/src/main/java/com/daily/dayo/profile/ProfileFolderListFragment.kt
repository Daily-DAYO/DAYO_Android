package com.daily.dayo.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.daily.dayo.databinding.FragmentProfileFolderListBinding

class ProfileFolderListFragment : Fragment() {
    private var _binding: FragmentProfileFolderListBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileFolderListBinding.inflate(inflater, container, false)
        return binding.root
    }
}