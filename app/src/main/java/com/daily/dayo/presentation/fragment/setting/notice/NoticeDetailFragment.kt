package com.daily.dayo.presentation.fragment.setting.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentNoticeDetailBinding
import com.daily.dayo.presentation.viewmodel.NoticeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeDetailFragment : Fragment() {
    private var binding by autoCleared<FragmentNoticeDetailBinding>()
    private val args by navArgs<NoticeDetailFragmentArgs>()
    private val noticeViewModel by activityViewModels<NoticeViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNoticeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackButtonClickListener()
        setNoticeDescription()
        observeNoticeDetail()
        getNoticeDetail()
    }

    private fun setBackButtonClickListener() {
        binding.btnNoticeDetailBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNoticeDescription() {
        binding.notice = args.notice
    }

    private fun getNoticeDetail() {
        noticeViewModel.requestDetailNotice(args.notice.noticeId)
    }

    private fun observeNoticeDetail() {
        noticeViewModel.detailNotice.observe(viewLifecycleOwner) {
            with(binding.webviewNoticeDetailContents) {
                apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = false
                }
                loadDataWithBaseURL(null, it, "text/html", "UTF-8", null)
            }
        }
    }
}