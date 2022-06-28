package com.daily.dayo.presentation.fragment.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentNotificationBinding
import com.daily.dayo.presentation.adapter.NotificationListAdapter
import com.daily.dayo.presentation.viewmodel.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private var binding by autoCleared<FragmentNotificationBinding>()
    private val notificationViewModel by viewModels<NotificationViewModel>()
    private lateinit var notificationAdapter: NotificationListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNotificationListAdapter()
    }

    override fun onResume() {
        super.onResume()
        setAlarmList()
    }

    private fun setNotificationListAdapter() {
        notificationAdapter = NotificationListAdapter()
        binding.rvNotificationList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotificationList.adapter = notificationAdapter
        notificationAdapter.setOnItemClickListener(object :
            NotificationListAdapter.OnItemClickListener {
            override fun notificationItemClick(alarmId: Int, alarmCheck: Boolean) {
                if (!alarmCheck) {
                    notificationViewModel.requestIsCheckAlarm(alarmId = alarmId)
                }
            }
        })
    }

    private fun setAlarmList() {
        notificationViewModel.requestAllAlarmList()
        notificationViewModel.alarmList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { alarmList ->
                        notificationAdapter.submitList(alarmList.toMutableList())
                        for (alarm in alarmList) {
                            if (alarm.check == true) break
                            alarm.alarmId?.let { alarmId ->
                                notificationViewModel.requestIsCheckAlarm(alarmId = alarmId)
                            }
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