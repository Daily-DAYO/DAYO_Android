package com.daily.dayo.post

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentPostOptionBinding
import com.daily.dayo.util.DefaultDialogConfigure
import com.daily.dayo.util.DefaultDialogExplanationConfirm
import com.daily.dayo.util.autoCleared

class PostOptionFragment : DialogFragment() {
    private var binding by autoCleared<FragmentPostOptionBinding>()
    private lateinit var mAlertDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostOptionBinding.inflate(inflater, container, false)
        // DialogFragment Radius 설정
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Android Version 4.4 이하에서 Blue Line이 상단에 나타는 것 방지
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setGravity(Gravity.BOTTOM)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHidePostClickListener()
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

    private fun setHidePostClickListener() {
        val hidePost = {
            // TODO : Hide하는 기능 구현
            findNavController().navigateUp()
            Toast.makeText(requireContext(), getString(R.string.post_option_hide_alert_message_success), Toast.LENGTH_SHORT).show()
        }

        mAlertDialog = DefaultDialogExplanationConfirm.createDialog(requireContext(), R.string.post_option_hide_alert_message_description, R.string.post_option_hide_alert_message_explanation,
            true, true, null, null,
            hidePost, { binding.layoutPostOption.visibility = View.VISIBLE
                this.mAlertDialog.dismiss()
            })
        mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.layoutPostOptionHide.setOnClickListener {
            if(!mAlertDialog.isShowing) {
                binding.layoutPostOption.visibility = View.INVISIBLE
                mAlertDialog.show()
                DefaultDialogConfigure.dialogResize(requireContext(), mAlertDialog, 0.7f, 0.21f)
            }
        }
    }
}