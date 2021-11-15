package com.daily.dayo.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentFeedBinding

class FeedFragment: Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)

        setPostSearchButtonClickListener()
        return binding.root
    }

    private fun setPostSearchButtonClickListener() {
        binding.btnPostSearch.setOnClickListener {
            findNavController().navigate(R.id.action_FeedFragment_to_SearchFragment)
        }
    }
}