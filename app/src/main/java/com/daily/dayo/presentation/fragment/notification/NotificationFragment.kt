package com.daily.dayo.presentation.fragment.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.daily.dayo.databinding.FragmentNotificationBinding
import com.daily.dayo.presentation.adapter.NotificationListAdapter
import com.daily.dayo.common.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private var binding by autoCleared<FragmentNotificationBinding>()
    private lateinit var notificationAdapter: NotificationListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        setCommentListAdapter()

        return binding.root
    }

    private fun setCommentListAdapter() {
        notificationAdapter = NotificationListAdapter()
        binding.rvNotificationList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotificationList.adapter = notificationAdapter
    }
}