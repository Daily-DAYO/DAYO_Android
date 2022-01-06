package com.daily.dayo.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentWriteFolderBinding
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared
import com.daily.dayo.write.adapter.WriteFolderAdapter
import com.daily.dayo.write.viewmodel.WriteFolderViewModel
import kotlinx.coroutines.launch

class WriteFolderFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteFolderBinding>()
    private val writeFolderViewModel  by activityViewModels<WriteFolderViewModel>()
    private lateinit var writeFolderAdapter: WriteFolderAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteFolderBinding.inflate(inflater, container, false)

        setBackButtonClickListener()
        setConfirmButtonClickListener()
        setFolderAddButtonClickListener()
        setRvWriteFolderListAdapter()
        setWriteFolderList()
        return binding.root
    }

    private fun setBackButtonClickListener() {
        binding.btnWriteFolderBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun setFolderAddButtonClickListener() {
        binding.tvWriteFolderAdd.setOnClickListener {
            findNavController().navigate(R.id.action_writeFolderFragment_to_folderAddFragment)
        }
    }
    private fun setConfirmButtonClickListener() {
        binding.tvWritePostFolderConfirm.setOnClickListener {
            Toast.makeText(requireContext(), "확인 버튼 클릭", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setRvWriteFolderListAdapter() {
        val layoutManager = LinearLayoutManager(this.context)
        writeFolderAdapter = WriteFolderAdapter()
        binding.rvWriteFolderListSaved.adapter = writeFolderAdapter
        binding.rvWriteFolderListSaved.layoutManager = layoutManager
    }

    private fun setWriteFolderList(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                writeFolderViewModel.folderList.observe(viewLifecycleOwner, Observer {
                    when(it.status){
                        Status.SUCCESS -> {
                            it.data?.let { folderList ->
                                writeFolderAdapter.submitList(folderList.data)
                            }
                        }
                    }
                })
            }
        }
    }
}