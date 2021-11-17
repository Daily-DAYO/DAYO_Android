package com.daily.dayo.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.daily.dayo.databinding.FragmentFollowerListBinding
import com.daily.dayo.util.autoCleared

class FollowerListFragment : Fragment(){
    private var binding by autoCleared<FragmentFollowerListBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowerListBinding.inflate(inflater, container, false)
        return binding.root
    }
}