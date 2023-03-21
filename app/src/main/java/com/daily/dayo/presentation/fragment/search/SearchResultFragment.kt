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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.HideKeyBoardUtil
import com.daily.dayo.common.ReplaceUnicode
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
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
    private lateinit var glideRequestManager: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        initUI()
        setSearchEditTextListener()
        setSearchTagResultPostAdapter()
        setAdapterLoadStateListener()
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
        binding.btnSearchResultBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initUI() {
        loadingPost()
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
                        View.GONE
                    }
                }
            }
        })
    }

    private fun setSearchTagResultPostAdapter() {
        searchTagResultPostAdapter =
            SearchTagResultPostAdapter(requestManager = glideRequestManager)
        binding.rvSearchResultContentsPostList.adapter = searchTagResultPostAdapter
    }

    private fun setSearchTagResultPostClickListener() {
        searchTagResultPostAdapter.setOnItemClickListener(object :
            SearchTagResultPostAdapter.OnItemClickListener {
            override fun onItemClick(v: View, search: Search, position: Int) {
                findNavController().navigate(
                    SearchResultFragmentDirections.actionSearchResultFragmentToPostFragment(
                        search.postId
                    )
                )
            }
        })
    }

    private fun setSearchKeywordInputDone() {
        binding.tvSearchResultKeywordInput.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    HideKeyBoardUtil.hide(requireContext(), binding.tvSearchResultKeywordInput)
                    binding.tvSearchResultKeywordInput.setText(ReplaceUnicode.replaceBlankText(binding.tvSearchResultKeywordInput.text.toString()))
                    if (ReplaceUnicode.replaceBlankText(binding.tvSearchResultKeywordInput.text.toString()).isNotBlank()) {
                        searchTagList(ReplaceUnicode.replaceBlankText(binding.tvSearchResultKeywordInput.text.toString()))
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun setSearchKeywordInputRemoveClickListener() {
        binding.btnSearchResultRemoveEtInput.setOnDebounceClickListener {
            binding.tvSearchResultKeywordInput.setText("")
        }
    }

    private fun searchTagList(keyword: String) {
        lifecycleScope.launchWhenResumed {
            searchViewModel.searchKeyword(keyword).collect {
                searchTagResultPostAdapter.submitData(it)

            }
        }
    }

    private fun setAdapterLoadStateListener() {
        var isInitialLoad = false
        searchTagResultPostAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.NotLoading && !isInitialLoad) {
                val isListEmpty = searchTagResultPostAdapter.itemCount == 0
                if (isListEmpty) {
                    binding.layoutSearchResultContents.visibility = View.INVISIBLE
                    binding.layoutSearchResultEmpty.visibility = View.VISIBLE
                } else {
                    binding.layoutSearchResultContents.visibility = View.VISIBLE
                    binding.layoutSearchResultEmpty.visibility = View.INVISIBLE
                    binding.resultCount = searchTagResultPostAdapter.itemCount
                }

                if (isListEmpty || loadState.append is LoadState.NotLoading) {
                    completeLoadPost()
                    isInitialLoad = true
                }
            }
        }
    }

    private fun loadingPost() {
        binding.layoutSearchResultPostShimmer.startShimmer()
        binding.layoutSearchResultPostShimmer.visibility = View.VISIBLE
        binding.rvSearchResultContentsPostList.visibility = View.INVISIBLE
    }

    private fun completeLoadPost() {
        binding.layoutSearchResultPostShimmer.stopShimmer()
        binding.layoutSearchResultPostShimmer.visibility = View.GONE
        binding.rvSearchResultContentsPostList.visibility = View.VISIBLE
    }
}