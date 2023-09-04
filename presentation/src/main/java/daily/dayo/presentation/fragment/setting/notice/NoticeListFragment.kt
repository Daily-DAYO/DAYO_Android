package daily.dayo.presentation.fragment.setting.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import daily.dayo.presentation.R
import daily.dayo.presentation.common.CustomDividerDecoration
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.dp
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentNoticeListBinding
import daily.dayo.presentation.adapter.NoticeListAdapter
import daily.dayo.presentation.viewmodel.NoticeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoticeListFragment : Fragment() {
    private var binding by autoCleared<FragmentNoticeListBinding> { onDestroyBindingView() }
    private val noticeViewModel by activityViewModels<NoticeViewModel>()
    private var noticeListAdapter: NoticeListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoticeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackButtonClickListener()
        getNoticeList()
        initNoticeListAdapter()
    }

    private fun onDestroyBindingView() {
        noticeListAdapter = null
        binding.rvNoticePost.adapter = null
    }

    private fun setBackButtonClickListener() {
        binding.btnNoticeBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initNoticeListAdapter() {
        noticeListAdapter = NoticeListAdapter()
        with(binding.rvNoticePost) {
            adapter = noticeListAdapter
            addItemDecoration(
                CustomDividerDecoration(
                    1.dp.toFloat(),
                    18.dp.toFloat(),
                    ContextCompat.getColor(requireContext(), R.color.gray_6_F0F1F3)
                )
            )
        }

        observeNoticeList()
    }

    private fun getNoticeList() {
        noticeViewModel.requestAllNoticeList()
    }

    private fun observeNoticeList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                noticeViewModel.noticeList.collectLatest { noticeList ->
                    noticeListAdapter?.submitData(noticeList)
                }
            }
        }
    }
}