package com.daily.dayo.presentation.fragment.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daily.dayo.common.HideKeyBoardUtil
import com.daily.dayo.common.ReplaceUnicode
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentSearchBinding
import com.daily.dayo.presentation.adapter.SearchKeywordRecentAdapter
import com.daily.dayo.presentation.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var binding by autoCleared<FragmentSearchBinding>()
    private val searchViewModel by activityViewModels<SearchViewModel>()
    private lateinit var searchKeywordRecentAdapter: SearchKeywordRecentAdapter
    private lateinit var searchKeywordRecentList: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        setSearchKeywordRecentListAdapter()
        setSearchEditTextListener()
        initSearchKeywordRecentList()
        setSearchKeywordRecentClickListener()
        setSearchKeywordInputDone()
        setSearchKeywordInputRemoveClickListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HideKeyBoardUtil.hideTouchDisplay(requireActivity(), view)
    }

    private fun setBackButtonClickListener() {
        binding.btnSearchBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setSearchKeywordRecentListAdapter() {
        searchKeywordRecentAdapter = SearchKeywordRecentAdapter()
        binding.rvSearchRecentKeyword.adapter = searchKeywordRecentAdapter
    }

    private fun setSearchEditTextListener() {
        binding.tvSearchKeywordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                with(binding.btnSearchRemoveEtInput) {
                    visibility = if (!s.isNullOrBlank()) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }
            }
        })
    }

    private fun setSearchKeywordInputDone() {
        binding.tvSearchKeywordInput.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    HideKeyBoardUtil.hide(requireContext(), binding.tvSearchKeywordInput)
                    binding.tvSearchKeywordInput.setText(ReplaceUnicode.replaceBlankText(binding.tvSearchKeywordInput.text.toString()))
                    if (ReplaceUnicode.replaceBlankText(binding.tvSearchKeywordInput.text.toString()).isNotBlank()) {
                        searchKeyword(ReplaceUnicode.replaceBlankText(binding.tvSearchKeywordInput.text.toString()))
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun setSearchKeywordInputRemoveClickListener() {
        binding.btnSearchRemoveEtInput.setOnDebounceClickListener {
            binding.tvSearchKeywordInput.setText("")
        }
    }

    private fun initSearchKeywordRecentList() {
        searchKeywordRecentList = searchViewModel.getSearchKeywordRecent()
        searchKeywordRecentAdapter.submitList(searchKeywordRecentList)
    }

    private fun setSearchKeywordRecentClickListener() {
        searchKeywordRecentAdapter.setOnItemClickListener(object :
            SearchKeywordRecentAdapter.OnItemClickListener {
            override fun onItemClick(v: View, keyword: String, pos: Int) {
                searchKeyword(keyword)
            }

            override fun deleteSearchKeywordRecentClick(keyword: String, pos: Int) {
                searchViewModel.deleteSearchKeywordRecent(keyword)
                searchKeywordRecentList = searchViewModel.getSearchKeywordRecent()
                searchKeywordRecentAdapter.submitList(searchKeywordRecentList)
            }
        })

        binding.tvSearchAllDelete.setOnDebounceClickListener {
            searchViewModel.clearSearchKeywordRecent()
            searchKeywordRecentList = searchViewModel.getSearchKeywordRecent()
            searchKeywordRecentAdapter.submitList(searchKeywordRecentList)
        }
    }

    private fun searchKeyword(keyword: String) {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToSearchResultFragment(
                keyword
            )
        )
    }
}