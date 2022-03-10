package com.daily.dayo.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.databinding.FragmentSearchResultBinding
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchResultFragment : Fragment() {
    private var binding by autoCleared<FragmentSearchResultBinding>()
    private val args by navArgs<SearchResultFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        initEditText()
        return binding.root
    }

    private fun setBackButtonClickListener() {
        binding.btnSearchResultBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun initEditText() {
        binding.tvSearchResultKeywordInput.setText(args.searchKeyword)
    }
}