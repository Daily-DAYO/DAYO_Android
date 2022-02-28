package com.daily.dayo.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daily.dayo.databinding.FragmentSearchResultBinding
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchResultFragment : Fragment() {
    private var binding by autoCleared<FragmentSearchResultBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }
}