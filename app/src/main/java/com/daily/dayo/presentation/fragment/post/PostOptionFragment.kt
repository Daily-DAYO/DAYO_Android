package com.daily.dayo.presentation.fragment.post

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.databinding.FragmentPostOptionBinding
import com.daily.dayo.common.DefaultDialogConfigure
import com.daily.dayo.common.autoCleared

class PostOptionFragment : DialogFragment() {
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

    private fun setReportUserClickListener() {
        binding.layoutPostOptionReportUser.setOnClickListener {

        }
    }

    private fun setReportPostClickListener() {
        binding.layoutPostOptionReportPost.setOnClickListener {
            findNavController().navigate(PostOptionFragmentDirections.actionPostOptionFragmentToPostReportFragment(postId = args.postId))
        }
   }
}