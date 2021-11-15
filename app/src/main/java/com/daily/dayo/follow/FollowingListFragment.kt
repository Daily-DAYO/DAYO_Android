package com.daily.dayo.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.daily.dayo.databinding.FragmentFollowingListBinding

class FollowingListFragment : Fragment() {
    private var _binding: FragmentFollowingListBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFollowingListBinding.inflate(inflater, container, false)
        return binding.root
    }
}