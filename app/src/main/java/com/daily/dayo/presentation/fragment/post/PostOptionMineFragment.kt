package com.daily.dayo.presentation.fragment.post

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.R
import com.daily.dayo.common.DefaultDialogConfigure
import com.daily.dayo.common.DefaultDialogConfirm
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentPostOptionMineBinding
import com.daily.dayo.presentation.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostOptionMineFragment : DialogFragment() {
    private var binding by autoCleared<FragmentPostOptionMineBinding>()
    private val postViewModel by activityViewModels<PostViewModel>()
    private val args by navArgs<PostOptionFragmentArgs>()
    private lateinit var mAlertDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostOptionMineBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setGravity(Gravity.BOTTOM)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDeletePostClickListener()
        setModifyPostClickListener()
    }

    override fun onResume() {
        super.onResume()
        resizePostOptionMineDialogFragment()
    }

    private fun resizePostOptionMineDialogFragment() {
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = DefaultDialogConfigure.getDeviceWidthSize(requireContext())
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    private fun setDeletePostClickListener() {
        val deletePost = {
            postViewModel.requestDeletePost(postId = args.postId)
            findNavController().navigate(R.id.action_postOptionMineFragment_to_HomeFragment)
        }
        mAlertDialog = DefaultDialogConfirm.createDialog(
            requireContext(),
            R.string.post_option_mine_delete_alert_message,
            true,
            true,
            null,
            null,
            deletePost
        ) {
            binding.layoutPostOptionMine.visibility = View.VISIBLE
            this.mAlertDialog.dismiss()
        }
        mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.layoutPostOptionMineDelete.setOnClickListener {
            if (!mAlertDialog.isShowing) {
                binding.layoutPostOptionMine.visibility = View.INVISIBLE
                mAlertDialog.show()
                DefaultDialogConfigure.dialogResize(requireContext(), mAlertDialog, 0.7f, 0.19f)
            }
        }
    }

    private fun setModifyPostClickListener() {
        binding.layoutPostOptionMineModify.setOnClickListener {
            val navigateWithDataPassAction =
                PostOptionMineFragmentDirections.actionPostOptionMineFragmentToWriteFragment(postId = args.postId)
            findNavController().navigate(navigateWithDataPassAction)
        }
    }
}