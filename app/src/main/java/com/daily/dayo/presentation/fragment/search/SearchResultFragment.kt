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
import androidx.navigation.fragment.navArgs
import com.daily.dayo.DayoApplication
import com.daily.dayo.common.HideKeyBoardUtil
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentSearchResultBinding
import com.daily.dayo.domain.model.Search
import com.daily.dayo.presentation.adapter.SearchTagResultPostAdapter
import com.daily.dayo.presentation.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchResultFragment : Fragment() {
    private var binding by autoCleared<FragmentSearchResultBinding>()
    private val searchViewModel by activityViewModels<SearchViewModel>()
    private val args by navArgs<SearchResultFragmentArgs>()
    private lateinit var searchTagResultPostAdapter: SearchTagResultPostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        initUI()
        setSearchEditTextListener()
        setSearchTagResultPostAdapter()
        setSearchTagResultPostClickListener()
        setSearchKeywordInputDone()
        setSearchKeywordInputRemoveClickListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HideKeyBoardUtil.hideTouchDisplay(requireActivity(), view)
    }

    private fun setBackButtonClickListener() {
        binding.btnSearchResultBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initUI() {
        binding.tvSearchResultKeywordInput.setText(args.searchKeyword)
        searchTagList(args.searchKeyword)
    }

    private fun setSearchEditTextListener() {
        binding.tvSearchResultKeywordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                with(binding.btnSearchResultRemoveEtInput) {
                    visibility = if (!s.isNullOrBlank()) {
                        View.VISIBLE
                    } else {
                        View.INVISIBLE
                    }
                }
            }
        })
    }

    private fun setSearchTagResultPostAdapter() {
        searchTagResultPostAdapter = SearchTagResultPostAdapter()
        binding.rvSearchResultContentsPostList.adapter = searchTagResultPostAdapter
    }

    private fun setSearchTagResultPostClickListener() {
        searchTagResultPostAdapter.setOnItemClickListener(object :
            SearchTagResultPostAdapter.OnItemClickListener {
            override fun onItemClick(v: View, search: Search, position: Int) {
                findNavController().navigate(SearchResultFragmentDirections.actionSearchResultFragmentToPostFragment(
                    search.postId))
            }
        })
    }

    private fun setSearchKeywordInputDone() {
        binding.tvSearchResultKeywordInput.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(), binding.tvSearchResultKeywordInput)
                    if (!binding.tvSearchResultKeywordInput.text.toString().trim()
                            .isNullOrBlank()
                    ) {
                        searchTagList(binding.tvSearchResultKeywordInput.text.toString())
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun setSearchKeywordInputRemoveClickListener() {
        binding.btnSearchResultRemoveEtInput.setOnClickListener {
            binding.tvSearchResultKeywordInput.setText("")
        }
    }

    private fun searchTagList(keyword: String) {
        searchViewModel.searchKeyword(keyword)
        searchViewModel.searchTagList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { postList ->
                        if (postList.isEmpty()) {
                            binding.layoutSearchResultContents.visibility = View.INVISIBLE
                            binding.imgSearchResultEmpty.visibility = View.VISIBLE
                        } else {
                            binding.layoutSearchResultContents.visibility = View.VISIBLE
                            binding.imgSearchResultEmpty.visibility = View.INVISIBLE
                            searchTagResultPostAdapter.submitList(postList.toMutableList())
                            binding.resultCount = postList.size
                        }
                    }
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                }
            }
        }
    }
}