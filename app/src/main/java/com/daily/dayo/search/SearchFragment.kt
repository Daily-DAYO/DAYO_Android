package com.daily.dayo.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daily.dayo.databinding.FragmentSearchBinding
import com.daily.dayo.search.adapter.SearchKeywordRecentAdapter
import com.daily.dayo.search.viemodel.SearchViewModel
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var binding by autoCleared<FragmentSearchBinding>()
    private val searchViewModel by activityViewModels<SearchViewModel>()
    private lateinit var searchKeywordRecentAdapter : SearchKeywordRecentAdapter
    private lateinit var searchKeywordRecentList: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        setSearchKeywordRecentListAdapter()
        initSearchKeywordRecentList()
        setSearchKeywordRecentClickListener()
        setSearchClickListener()
        return binding.root
    }

    private fun setBackButtonClickListener() {
        binding.btnSearchBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setSearchKeywordRecentListAdapter() {
        searchKeywordRecentAdapter = SearchKeywordRecentAdapter()
        binding.rvSearchRecentKeyword.adapter = searchKeywordRecentAdapter
    }

    private fun setSearchClickListener() {
        binding.btnSearchKeyword.setOnClickListener {
            if(!binding.tvSearchKeywordInput.text.toString().trim().isNullOrBlank()) {
                searchKeyword(binding.tvSearchKeywordInput.text.toString())
            }
        }
    }

    private fun initSearchKeywordRecentList() {
        searchKeywordRecentList = searchViewModel.getSearchKeywordRecent()
        searchKeywordRecentAdapter.submitList(searchKeywordRecentList)
    }

    private fun setSearchKeywordRecentClickListener() {
        searchKeywordRecentAdapter.setOnItemClickListener(object : SearchKeywordRecentAdapter.OnItemClickListener{
            override fun onItemClick(v: View, keyword: String, pos: Int) {
                searchKeyword(keyword)
            }

            override fun DeleteSearchKeywordRecentClick(keyword: String, pos: Int) {
                searchViewModel.deleteSearchKeywordRecent(keyword)
                searchKeywordRecentAdapter.submitList(searchKeywordRecentList)
            }
        })

        binding.tvSearchAllDelete.setOnClickListener {
            searchViewModel.clearSearchKeywordRecent()
            searchKeywordRecentAdapter.submitList(searchKeywordRecentList)
        }
    }
    private fun searchKeyword(keyword: String) {
        searchViewModel.searchKeyword(keyword)
        findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToSearchResultFragment(keyword))
    }
}