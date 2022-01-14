package com.daily.dayo.post

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentPostOptionMineBinding
import com.daily.dayo.post.viewmodel.PostViewModel
import com.daily.dayo.util.autoCleared

class PostOptionMineFragment : DialogFragment() {
    private var binding by autoCleared<FragmentPostOptionMineBinding>()
    private val postViewModel by activityViewModels<PostViewModel>()
    private val args by navArgs<PostOptionFragmentArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostOptionMineBinding.inflate(inflater, container, false)
        // DialogFragment Radius 설정
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Android Version 4.4 이하에서 Blue Line이 상단에 나타는 것 방지
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setGravity(Gravity.BOTTOM)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCancelClickListener()
        setDeletePostClickListener()
    }

    private fun setDeletePostClickListener() {
        binding.layoutPostOptionMineDelete.setOnClickListener {
            postViewModel.requestDeletePost(args.id)
            findNavController().navigate(R.id.action_postOptionMineFragment_to_HomeFragment)
        }
    }
    private fun setCancelClickListener() {
        binding.layoutPostOptionMineCancel.setOnClickListener {
           findNavController().navigateUp()
        }
    }
}