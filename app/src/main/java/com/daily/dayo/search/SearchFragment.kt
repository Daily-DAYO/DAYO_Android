package com.daily.dayo.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentSearchBinding
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var binding by autoCleared<FragmentSearchBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        setSearchClickListener()
        return binding.root
    }

    private fun setBackButtonClickListener() {
        binding.btnSearchBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setSearchClickListener() {
        binding.btnSearchKeyword.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_searchResultFragment)
        }
    }
}