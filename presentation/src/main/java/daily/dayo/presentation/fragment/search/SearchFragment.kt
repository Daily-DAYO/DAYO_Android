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
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.presentation.R
import daily.dayo.presentation.adapter.SearchKeywordRecentAdapter
import daily.dayo.presentation.common.HideKeyBoardUtil
import daily.dayo.presentation.common.ReplaceUnicode
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentSearchBinding
import daily.dayo.presentation.viewmodel.SearchViewModel

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var binding by autoCleared<FragmentSearchBinding> { onDestroyBindingView() }
    private val searchViewModel by activityViewModels<SearchViewModel>()
    private var searchKeywordRecentAdapter: SearchKeywordRecentAdapter? = null
    private lateinit var searchKeywordRecentList: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackButtonClickListener()
        setSearchKeywordRecentListAdapter()
        setSearchEditTextListener()
        initSearchKeywordRecentList()
        setSearchKeywordRecentClickListener()
        setSearchKeywordInputDone()
        setSearchKeywordInputRemoveClickListener()
        HideKeyBoardUtil.hideTouchDisplay(requireActivity(), view)
    }

    private fun onDestroyBindingView() {
        searchKeywordRecentAdapter = null
        binding.rvSearchRecentKeyword.adapter = null
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
//        searchKeywordRecentList = searchViewModel.getSearchKeywordRecent()
        searchKeywordRecentAdapter?.submitList(searchKeywordRecentList)
    }

    private fun setSearchKeywordRecentClickListener() {
        searchKeywordRecentAdapter?.setOnItemClickListener(object :
            SearchKeywordRecentAdapter.OnItemClickListener {
            override fun onItemClick(v: View, keyword: String, pos: Int) {
                searchKeyword(keyword)
            }

            override fun deleteSearchKeywordRecentClick(keyword: String, pos: Int) {
//                searchViewModel.searchHistory.value?.data?.get(pos)?.let {
//                    searchViewModel.deleteSearchKeywordRecent(it.history, it.searchHistoryType)
//                }
//                searchKeywordRecentList = searchViewModel.getSearchKeywordRecent()
                searchKeywordRecentAdapter?.submitList(searchKeywordRecentList)
            }
        })

        binding.tvSearchAllDelete.setOnDebounceClickListener {
//            searchViewModel.clearSearchKeywordRecent()
//            searchKeywordRecentList = searchViewModel.getSearchKeywordRecent()
            searchKeywordRecentAdapter?.submitList(searchKeywordRecentList)
        }
    }

    private fun searchKeyword(keyword: String) {
        searchViewModel.searchKeyword = keyword
        findNavController().navigateSafe(
            currentDestinationId = R.id.SearchFragment,
            action = R.id.action_searchFragment_to_searchResultFragment
        )
    }
}