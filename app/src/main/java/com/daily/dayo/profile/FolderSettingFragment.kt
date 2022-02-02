package com.daily.dayo.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentFolderSettingBinding
import com.daily.dayo.profile.adapter.FolderSettingAdapter
import com.daily.dayo.profile.model.FolderOrder
import com.daily.dayo.profile.viewmodel.FolderSettingViewModel
import com.daily.dayo.util.ItemTouchHelperCallback
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared
import kotlinx.coroutines.launch

class FolderSettingFragment : Fragment() {
    private var binding by autoCleared<FragmentFolderSettingBinding>()
    private val folderSettingViewModel by activityViewModels<FolderSettingViewModel>()
    private lateinit var folderSettingAdapter: FolderSettingAdapter
    private var folderOrderList : MutableList<FolderOrder> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderSettingBinding.inflate(inflater, container, false)

        setBackButtonClickListener()
        setFolderAddButtonClickListener()
        setRvFolderSettingListAdapter()
        setProfileFolderList()
        setChangeOrderButtonClickListener()
        setSaveButtonClickListener()

        return binding.root
    }

    private fun setBackButtonClickListener() {
        binding.btnFolderSettingBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setFolderAddButtonClickListener() {
        binding.tvFolderSettingAdd.setOnClickListener {
            findNavController().navigate(R.id.action_folderSettingFragment_to_folderSettingAddFragment)
        }
    }

    private fun setRvFolderSettingListAdapter() {
        folderSettingAdapter = FolderSettingAdapter(false)
        binding.rvFolderSettingListSaved.adapter = folderSettingAdapter
    }

    private fun setProfileFolderList(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                folderSettingViewModel.folderList.observe(viewLifecycleOwner, Observer {
                    when(it.status){
                        Status.SUCCESS -> {
                            it.data?.let { folderList ->
                                folderSettingAdapter.submitList(folderList.data)
                            }
                        }
                    }
                })
            }
        }
    }

    private fun setProfileOrderFolderList(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                folderSettingViewModel.folderList.observe(viewLifecycleOwner, Observer {
                    when(it.status){
                        Status.SUCCESS -> {
                            it.data?.let { folderList ->
                                val size = folderList.data.size
                                folderOrderList = mutableListOf()
                                for(i in 0 until size){
                                    folderOrderList.add(FolderOrder(folderList.data[i].folderId,i))
                                }
                                folderSettingAdapter.submitList(folderList.data)
                            }
                        }
                    }
                })
            }
        }
    }

    private fun setChangeOrderButtonClickListener(){
        binding.btnFolderSettingChangeOrderOption.setOnClickListener {
            binding.btnFolderSettingSave.visibility = View.VISIBLE
            folderSettingAdapter = FolderSettingAdapter(true)
            setProfileOrderFolderList()
            changeOrder()
        }
    }

    private fun setSaveButtonClickListener(){
        binding.btnFolderSettingSave.setOnClickListener {
            //변경된 순서 저장
            folderSettingViewModel.requestOrderFolder(folderOrderList)

            //순서 저장 완료 후 변경 불가능한 상태로 돌아가기
            setRvFolderSettingListAdapter()
            binding.btnFolderSettingSave.visibility = View.GONE
            folderSettingViewModel.orderFolderSuccess.observe(viewLifecycleOwner, Observer {
                setProfileFolderList()
            })
        }
    }

    private fun changeOrder(){
        folderSettingAdapter.folderOrderList = folderOrderList
        val callback = ItemTouchHelperCallback(folderSettingAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.rvFolderSettingListSaved)
        binding.rvFolderSettingListSaved.adapter = folderSettingAdapter
        folderSettingAdapter.startDrag(object : FolderSettingAdapter.OnStartDragListener {
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                touchHelper.startDrag(viewHolder)
            }
        })
    }

}