package com.daily.dayo.login.findAccount

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentFindAccountPasswordCompleteBinding
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindAccountPasswordCompleteFragment : Fragment() {
    private var binding by autoCleared<FragmentFindAccountPasswordCompleteBinding>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindAccountPasswordCompleteBinding.inflate(inflater, container, false)
        setBackClickListener()
        setNextClickListener()
        return binding.root
    }

    private fun setBackClickListener() {
        binding.btnFindAccountPasswordCompleteBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnFindAccountPasswordCompleteNext.setOnClickListener {
            findNavController().navigate(R.id.action_findAccountPasswordCompleteFragment_to_loginEmailFragment)
        }
    }
}