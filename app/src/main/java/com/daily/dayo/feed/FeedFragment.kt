package com.daily.dayo.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentFeedBinding
import com.daily.dayo.util.autoCleared

class FeedFragment: Fragment() {
    private var binding by autoCleared<FragmentFeedBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(inflater, container, false)

        setPostSearchButtonClickListener()
        return binding.root
    }

    private fun setPostSearchButtonClickListener() {
        binding.btnPostSearch.setOnClickListener {
            findNavController().navigate(R.id.action_FeedFragment_to_SearchFragment)
        }
    }
}