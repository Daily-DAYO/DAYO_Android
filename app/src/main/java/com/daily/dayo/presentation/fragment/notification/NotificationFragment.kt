package com.daily.dayo.presentation.fragment.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentNotificationBinding
import com.daily.dayo.presentation.adapter.NotificationListAdapter
import com.daily.dayo.presentation.viewmodel.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers

@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private var binding by autoCleared<FragmentNotificationBinding> { onDestroyBindingView() }
    private val notificationViewModel by viewModels<NotificationViewModel>()
    private var notificationAdapter: NotificationListAdapter? = null
    private var glideRequestManager: RequestManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNotificationListAdapter()
        setAdapterLoadStateListener()
        setAlarmList()
    }

    override fun onResume() {
        super.onResume()
        getAlarmList()
    }

    private fun onDestroyBindingView() {
        glideRequestManager = null
        notificationAdapter = null
        binding.rvNotificationList.adapter = null
    }

    private fun setNotificationListAdapter() {
        notificationAdapter = glideRequestManager?.let {
            NotificationListAdapter(requestManager = it)
        }
        binding.rvNotificationList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotificationList.adapter = notificationAdapter
        notificationAdapter?.setOnItemClickListener(object :
            NotificationListAdapter.OnItemClickListener {
            override fun notificationItemClick(alarmId: Int, alarmCheck: Boolean, position: Int) {
                if (!alarmCheck) {
                    notificationViewModel.requestIsCheckAlarm(alarmId = alarmId)
                    notificationViewModel.checkAlarmSuccess.observe(viewLifecycleOwner) {
                        if (it == true) notificationAdapter?.notifyItemChanged(position)
                    }
                }
            }
        })
    }

    private fun getAlarmList() {
        notificationViewModel.requestAllAlarmList()
    }

    private fun setAlarmList() {
        notificationViewModel.alarmList.observe(viewLifecycleOwner) {
            notificationAdapter?.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun setAdapterLoadStateListener() {
        var isInitialLoad = false
        notificationAdapter?.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.NotLoading && !isInitialLoad) {
                val isListEmpty = notificationAdapter?.itemCount == 0
                binding.isEmpty = isListEmpty
                if (isListEmpty || loadState.append is LoadState.NotLoading) {
                    isInitialLoad = true
                }
            }
        }
    }
}