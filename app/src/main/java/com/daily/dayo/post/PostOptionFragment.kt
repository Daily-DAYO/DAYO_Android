package com.daily.dayo.post

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.daily.dayo.databinding.FragmentPostOptionBinding
import com.daily.dayo.util.autoCleared

class PostOptionFragment : DialogFragment() {
    private var binding by autoCleared<FragmentPostOptionBinding>()
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
        binding.layoutPostOptionCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}