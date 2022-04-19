package com.daily.dayo.write

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentWriteFolderBinding
import com.daily.dayo.profile.model.Folder
import com.daily.dayo.util.ButtonActivation
import com.daily.dayo.util.Event
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared
import com.daily.dayo.write.adapter.WriteFolderAdapter
import com.daily.dayo.write.viewmodel.WriteViewModel
import kotlinx.coroutines.launch

class WriteFolderFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteFolderBinding>()
    private val writeViewModel  by activityViewModels<WriteViewModel>()
    private val writeFolderAdapter by lazy {
        WriteFolderAdapter(
            this::onFolderClicked,
            writeViewModel.postFolderId.value!!
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteFolderBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        setFolderAddButtonClickListener()
        setRvWriteFolderListAdapter()
        setWriteFolderList()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                writeViewModel.showWriteOptionDialog.value = Event(true)
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun setBackButtonClickListener() {
        binding.btnWriteFolderBack.setOnClickListener {
            writeViewModel.showWriteOptionDialog.value = Event(true)
            findNavController().navigateUp()
        }
    }
    private fun setFolderAddButtonClickListener() {
        binding.tvWriteFolderAdd.setOnClickListener {
            findNavController().navigate(R.id.action_writeFolderFragment_to_folderAddFragment)
        }
    }

    private fun setRvWriteFolderListAdapter() {
        val layoutManager = LinearLayoutManager(this.context)
        binding.rvWriteFolderListSaved.adapter = writeFolderAdapter
        binding.rvWriteFolderListSaved.layoutManager = layoutManager
    }

    private fun onFolderClicked(folder:Folder){
        writeViewModel.postFolderId.value = folder.folderId.toString()
        writeViewModel.postFolderName.value = folder.name
        writeViewModel.showWriteOptionDialog.value = Event(true)
        findNavController().navigateUp()
    }

    private fun setWriteFolderList(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                writeViewModel.requestAllMyFolderList()
                writeViewModel.folderList.observe(viewLifecycleOwner, Observer {
                    when(it.status){
                        Status.SUCCESS -> {
                            it.data?.let { folderList ->
                                writeFolderAdapter.submitList(folderList.data)
                                if(folderList.count < 5){
                                    ButtonActivation.setTextViewButtonActive(requireContext(), binding.tvWriteFolderAdd)
                                }
                                else{
                                    ButtonActivation.setTextViewButtonInactive(requireContext(), binding.tvWriteFolderAdd)
                                }
                            }
                        }
                    }
                })
            }
        }
    }

}