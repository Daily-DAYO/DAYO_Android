package daily.dayo.presentation.fragment.post

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.presentation.R
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.dialog.DefaultDialogConfigure
import daily.dayo.presentation.common.dialog.DefaultDialogConfirm
import daily.dayo.presentation.common.dialog.LoadingAlertDialog
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentPostOptionMineBinding
import daily.dayo.presentation.viewmodel.PostViewModel

@AndroidEntryPoint
class PostOptionMineFragment : DialogFragment() {
    private var binding by autoCleared<FragmentPostOptionMineBinding> {
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }
    private val postViewModel by activityViewModels<PostViewModel>()
    private val args by navArgs<PostOptionMineFragmentArgs>()
    private lateinit var mAlertDialog: AlertDialog
    private lateinit var loadingAlertDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostOptionMineBinding.inflate(inflater, container, false)
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())
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
            LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
            postViewModel.requestDeletePost(postId = args.postId)
            findNavController().navigateSafe(
                currentDestinationId = R.id.PostOptionMineFragment,
                action = R.id.action_postOptionMineFragment_to_HomeFragment
            )
        }

        mAlertDialog = DefaultDialogConfirm.createDialog(
            requireContext(),
            R.string.post_option_mine_delete_alert_message,
            true,
            true,
            null,
            null,
            { deletePost() }
        ) {
            binding.layoutPostOptionMine.visibility = View.VISIBLE
            this.mAlertDialog.dismiss()
        }
        mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.layoutPostOptionMineDelete.setOnDebounceClickListener {
            if (!mAlertDialog.isShowing) {
                binding.layoutPostOptionMine.visibility = View.INVISIBLE
                mAlertDialog.show()
                DefaultDialogConfigure.dialogResize(requireContext(), mAlertDialog, 0.7f, 0.19f)
            }
        }
    }

    private fun setModifyPostClickListener() {
        binding.layoutPostOptionMineModify.setOnDebounceClickListener {
            val navigateWithDataPassAction =
                PostOptionMineFragmentDirections.actionPostOptionMineFragmentToWriteFragment(postId = args.postId)
            findNavController().navigateSafe(
                currentDestinationId = R.id.PostOptionMineFragment,
                action = navigateWithDataPassAction
            )
        }
    }
}