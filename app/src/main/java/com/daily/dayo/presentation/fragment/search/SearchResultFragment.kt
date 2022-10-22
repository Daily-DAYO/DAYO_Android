package com.daily.dayo.presentation.fragment.search

import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.GlideLoadUtil
import com.daily.dayo.common.HideKeyBoardUtil
import com.daily.dayo.common.ReplaceUnicode
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentSearchResultBinding
import com.daily.dayo.domain.model.Search
import com.daily.dayo.presentation.adapter.SearchTagResultPostAdapter
import com.daily.dayo.presentation.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        searchViewModel.searchKeyword(keyword)
        searchViewModel.searchTagList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { postList ->
                        if (postList.isEmpty()) {
                            binding.layoutSearchResultContents.visibility = View.INVISIBLE
                            binding.layoutSearchResultEmpty.visibility = View.VISIBLE
                        } else {
                            binding.layoutSearchResultContents.visibility = View.VISIBLE
                            binding.layoutSearchResultEmpty.visibility = View.INVISIBLE
                            binding.resultCount = postList.size
                            loadPostThumbnail(postList)
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


    private fun loadPostThumbnail(postList: List<Search>) {
        val thumbnailImgList = emptyList<Bitmap>().toMutableList()
        viewLifecycleOwner.lifecycleScope.launch {
            for (i in 0 until (if (postList.size >= 6) 6 else postList.size)) {
                thumbnailImgList.add(withContext(Dispatchers.IO) {
                    GlideLoadUtil.loadImageBackground(
                        context = requireContext(),
                        height = 158,
                        width = 158,
                        imgName = postList[i].thumbnailImage
                    )
                })
            }
        }.invokeOnCompletion { throwable ->
            when (throwable) {
                is CancellationException -> {
                    Log.e("Image Loading", "CANCELLED")
                    thumbnailImgList.clear()
                }
                null -> {
                    var loadedPostList = postList.toMutableList()
                    for (i in 0 until (if (postList.size >= 6) 6 else postList.size)) {
                        loadedPostList[i].preLoadThumbnail = thumbnailImgList[i]
                    }
                    searchTagResultPostAdapter.submitList(postList.toMutableList())
                    completeLoadPost()
                    thumbnailImgList.clear()
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