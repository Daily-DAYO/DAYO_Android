package daily.dayo.presentation.fragment.search

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
import androidx.paging.LoadState
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.domain.model.Search
import daily.dayo.presentation.R
import daily.dayo.presentation.adapter.SearchTagResultPostAdapter
import daily.dayo.presentation.common.HideKeyBoardUtil
import daily.dayo.presentation.common.ReplaceUnicode
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentSearchResultBinding
import daily.dayo.presentation.viewmodel.SearchViewModel

@AndroidEntryPoint
class SearchResultFragment : Fragment() {
    private var binding by autoCleared<FragmentSearchResultBinding> { onDestroyBindingView() }
    private val searchViewModel by activityViewModels<SearchViewModel>()
    private var searchTagResultPostAdapter: SearchTagResultPostAdapter? = null
    private var glideRequestManager: RequestManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            getSearchTagList(searchViewModel.searchKeyword)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSearchResult()
        observeSearchTagList()
        observeSearchTagCount()
        setBackButtonClickListener()
        setSearchEditTextListener()
        setSearchTagResultPostAdapter()
        setAdapterLoadStateListener()
        setSearchTagResultPostClickListener()
        setSearchKeywordInputDone()
        setSearchKeywordInputRemoveClickListener()
        HideKeyBoardUtil.hideTouchDisplay(requireActivity(), view)
    }

    private fun onDestroyBindingView() {
        glideRequestManager = null
        searchTagResultPostAdapter = null
        binding.rvSearchResultContentsPostList.adapter = null
    }

    private fun setBackButtonClickListener() {
        binding.btnSearchResultBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setSearchResult() {
        loadingPost()
        binding.tvSearchResultKeywordInput.setText(searchViewModel.searchKeyword)
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
            glideRequestManager?.let { SearchTagResultPostAdapter(requestManager = it) }
        binding.rvSearchResultContentsPostList.adapter = searchTagResultPostAdapter
    }

    private fun setSearchTagResultPostClickListener() {
        searchTagResultPostAdapter?.setOnItemClickListener(object :
            SearchTagResultPostAdapter.OnItemClickListener {
            override fun onItemClick(v: View, search: Search, position: Int) {
                findNavController().navigateSafe(
                    currentDestinationId = R.id.SearchResultFragment,
                    action = R.id.action_searchResultFragment_to_postFragment,
                    args = SearchResultFragmentDirections.actionSearchResultFragmentToPostFragment(
                        search.postId
                    ).arguments
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
                        searchViewModel.searchKeyword = ReplaceUnicode.replaceBlankText(binding.tvSearchResultKeywordInput.text.toString())
                        getSearchTagList(searchViewModel.searchKeyword)
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

    private fun getSearchTagList(keyword: String) {
        searchViewModel.searchKeyword(keyword)
    }

    private fun observeSearchTagList() {
        searchViewModel.searchTagList.observe(viewLifecycleOwner) {
            searchTagResultPostAdapter?.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun observeSearchTagCount() {
        searchViewModel.searchTotalCount.observe(viewLifecycleOwner) {
            binding.resultCount = it
        }
    }

    private fun setAdapterLoadStateListener() {
        searchTagResultPostAdapter?.let {
            it.addLoadStateListener { loadState ->
                if (loadState.refresh is LoadState.NotLoading) {
                    val isListEmpty = it.itemCount == 0
                    if (isListEmpty) {
                        binding.layoutSearchResultContents.visibility = View.INVISIBLE
                        binding.layoutSearchResultEmpty.visibility = View.VISIBLE
                    } else {
                        binding.layoutSearchResultContents.visibility = View.VISIBLE
                        binding.layoutSearchResultEmpty.visibility = View.INVISIBLE
                    }

                    if (isListEmpty || loadState.append is LoadState.NotLoading) {
                        completeLoadPost()
                    }
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