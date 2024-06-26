package daily.dayo.presentation.fragment.post

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.presentation.R
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.dialog.DefaultDialogConfigure
import daily.dayo.presentation.common.dialog.DefaultDialogExplanationConfirm
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentPostOptionBinding
import daily.dayo.presentation.viewmodel.PostViewModel

@AndroidEntryPoint
class PostOptionFragment : DialogFragment() {
    private val postViewModel by viewModels<PostViewModel>()
    private var binding by autoCleared<FragmentPostOptionBinding>()
    private val args by navArgs<PostOptionFragmentArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostOptionBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setGravity(Gravity.BOTTOM)
        setBlockUserClickListener()
        setReportUserClickListener()
        setReportPostClickListener()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        resizePostOptionDialogFragment()
    }

    private fun resizePostOptionDialogFragment() {
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = DefaultDialogConfigure.getDeviceWidthSize(requireContext())
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    private fun setBlockUserClickListener() {
        binding.layoutPostOptionBlockUser.setOnDebounceClickListener {
            val blockAlertDialog = DefaultDialogExplanationConfirm.createDialog(requireContext(),
                R.string.post_block_message,
                R.string.post_block_explanation_message,
                true,
                true,
                R.string.confirm,
                R.string.cancel,
                { blockUser() },
                { dismiss() })

            if (!blockAlertDialog.isShowing) {
                blockAlertDialog.show()
                DefaultDialogConfigure.dialogResize(
                    requireContext(),
                    blockAlertDialog,
                    0.7f,
                    0.23f
                )
            }
        }
    }

    private fun blockUser() {
        postViewModel.requestBlockMember(args.memberId)
        postViewModel.blockSuccess.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                Toast.makeText(
                    requireContext(),
                    R.string.post_block_success_message,
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun setReportUserClickListener() {
        binding.layoutPostOptionReportUser.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.PostOptionFragment,
                action = R.id.action_postOptionFragment_to_reportUserFragment,
                args = PostOptionFragmentDirections.actionPostOptionFragmentToReportUserFragment(
                    memberId = args.memberId
                ).arguments
            )
        }
    }

    private fun setReportPostClickListener() {
        binding.layoutPostOptionReportPost.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.PostOptionFragment,
                action = R.id.action_postOptionFragment_to_reportPostFragment,
                args = PostOptionFragmentDirections.actionPostOptionFragmentToReportPostFragment(
                    postId = args.postId
                ).arguments
            )
        }
    }
}