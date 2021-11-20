package com.daily.dayo.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentPostBinding
import com.daily.dayo.util.autoCleared

class PostFragment : Fragment() {
    private var binding by autoCleared<FragmentPostBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        setCommentListAdapter()
        setPostOptionClickListener()
        return binding.root
    }

    private fun setCommentListAdapter() {
        val swipeListAdapter = SwipeListAdapter()
        val swipeHelperCallback = SwipeHelperCallback().apply {
            setClamp(200f)
        }

        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvPostCommentList)

        binding.rvPostCommentList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = swipeListAdapter

            setOnTouchListener { _, _ ->
                swipeHelperCallback.removePreviousClamp(this)
                false
            }
        }
    }

    private fun setPostOptionClickListener() {
        binding.btnPostOption.setOnClickListener {
            //PostOptionFragment().show(requireActivity().supportFragmentManager, "PostOptionDialog")
            findNavController().navigate(R.id.action_postFragment_to_postOptionFragment)
        }
    }
}