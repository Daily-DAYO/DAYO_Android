package com.daily.dayo.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.DayoApplication
import com.daily.dayo.SharedManager
import com.daily.dayo.databinding.FragmentSearchResultBinding
import com.daily.dayo.search.adapter.SearchTagResultPostAdapter
import com.daily.dayo.search.model.SearchTagPostContent
import com.daily.dayo.search.viemodel.SearchViewModel
import com.daily.dayo.util.HideKeyBoardUtil
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared
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
            override fun onItemClick(v: View, postContent: SearchTagPostContent, pos: Int) {
                findNavController().navigate(SearchResultFragmentDirections.actionSearchResultFragmentToPostFragment(
                    postContent.postId,
                    SharedManager(DayoApplication.applicationContext()).getCurrentUser().nickname!!))
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
        searchViewModel.searchTagList.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { postDetail ->
                        if (postDetail.count == 0) {
                            binding.layoutSearchResultContents.visibility = View.INVISIBLE
                            binding.imgSearchResultEmpty.visibility = View.VISIBLE
                        } else {
                            binding.layoutSearchResultContents.visibility = View.VISIBLE
                            binding.imgSearchResultEmpty.visibility = View.INVISIBLE

                            searchTagResultPostAdapter.submitList(postDetail.data.toMutableList())
                            binding.resultPost = postDetail
                        }
                    }
                }
                Status.LOADING -> {
                }
                Status.ERROR -> {
                }
            }
        })
    }
}