package daily.dayo.presentation.fragment.mypage.folder

import android.app.AlertDialog
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
import daily.dayo.domain.model.FolderOrder
import daily.dayo.presentation.R
import daily.dayo.presentation.adapter.FolderSettingAdapter
import daily.dayo.presentation.common.ButtonActivation
import daily.dayo.presentation.common.ItemTouchHelperCallback
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.dialog.LoadingAlertDialog
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentFolderSettingBinding
import daily.dayo.presentation.viewmodel.FolderViewModel
import kotlinx.coroutines.launch

class FolderSettingFragment : Fragment() {
    private var binding by autoCleared<FragmentFolderSettingBinding> {
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
        onDestroyBindingView()
    }
    private val folderViewModel by activityViewModels<FolderViewModel>()
    private var folderSettingAdapter: FolderSettingAdapter? = null
    private var folderOrderList: MutableList<FolderOrder> = mutableListOf()
    private lateinit var loadingAlertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderSettingBinding.inflate(inflater, container, false)
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())

        setBackButtonClickListener()
        setFolderAddButtonClickListener()
        setRvFolderSettingListAdapter()
        setProfileFolderList()
        setChangeOrderButtonClickListener()
        setSaveButtonClickListener()

        return binding.root
    }

    private fun onDestroyBindingView() {
        folderSettingAdapter = null
        binding.rvFolderSettingListSaved.adapter = null
    }

    private fun setBackButtonClickListener() {
        binding.btnFolderSettingBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setFolderAddButtonClickListener() {
        binding.tvFolderSettingAdd.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.FolderSettingFragment,
                action = R.id.action_folderSettingFragment_to_folderSettingAddFragment
            )
        }
    }

    private fun setRvFolderSettingListAdapter() {
        folderSettingAdapter = FolderSettingAdapter(false)
        binding.rvFolderSettingListSaved.adapter = folderSettingAdapter
    }

    private fun setProfileFolderList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                folderViewModel.requestAllMyFolderList()
                folderViewModel.folderList.observe(viewLifecycleOwner, Observer {
                    when (it.status) {
                        Status.SUCCESS -> {
                            it.data?.let { folderList ->
                                folderSettingAdapter?.submitList(folderList)
                                if (folderList.size < 5) {
                                    ButtonActivation.setTextViewButtonActive(
                                        requireContext(),
                                        binding.tvFolderSettingAdd
                                    )
                                } else {
                                    ButtonActivation.setTextViewButtonInactive(
                                        requireContext(),
                                        binding.tvFolderSettingAdd
                                    )
                                }
                            }
                        }

                        else -> {}
                    }
                })
            }
        }
    }

    private fun setProfileOrderFolderList() {
        folderViewModel.folderList.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { folderList ->
                        val size = folderList.size
                        folderOrderList = mutableListOf()
                        for (i in 0 until size) {
                            folderOrderList.add(
                                FolderOrder(
                                    folderList[i].folderId!!,
                                    i
                                )
                            )
                        }
                        folderSettingAdapter?.submitList(folderList)
                    }
                }

                else -> {}
            }
        })
    }

    private fun setChangeOrderButtonClickListener() {
        binding.btnFolderSettingChangeOrderOption.setOnDebounceClickListener {
            binding.btnFolderSettingSave.visibility = View.VISIBLE
            folderSettingAdapter = FolderSettingAdapter(true)
            setProfileOrderFolderList()
            changeOrder()
        }
    }

    private fun setSaveButtonClickListener() {
        binding.btnFolderSettingSave.setOnDebounceClickListener {
            //변경된 순서 저장
            folderViewModel.requestOrderFolder(folderOrderList)

            //순서 저장 완료 후 변경 불가능한 상태로 돌아가기
            setRvFolderSettingListAdapter()
            binding.btnFolderSettingSave.visibility = View.GONE
            folderViewModel.orderFolderSuccess.observe(viewLifecycleOwner) {
                if (it.getContentIfNotHandled() == true) {
                    setProfileFolderList()
                }
            }
        }
    }

    private fun changeOrder() {
        folderSettingAdapter?.folderOrderList = folderOrderList
        val callback = folderSettingAdapter?.let { ItemTouchHelperCallback(it) }
        val touchHelper = callback?.let { ItemTouchHelper(it) }
        touchHelper?.attachToRecyclerView(binding.rvFolderSettingListSaved)
        binding.rvFolderSettingListSaved.adapter = folderSettingAdapter
        folderSettingAdapter?.startDrag(object : FolderSettingAdapter.OnStartDragListener {
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                touchHelper?.startDrag(viewHolder)
            }
        })
    }

}