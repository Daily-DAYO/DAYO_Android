package com.daily.dayo.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daily.dayo.databinding.FragmentNewPostListBinding
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewPostListFragment : Fragment() {
    private var binding by autoCleared<FragmentNewPostListBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewPostListBinding.inflate(inflater, container, false)
        return binding.root
    }
}