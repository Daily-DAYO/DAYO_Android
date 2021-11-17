package com.daily.dayo.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.daily.dayo.databinding.FragmentWriteTagBinding
import com.daily.dayo.util.autoCleared

class WriteTagFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteTagBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteTagBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        return binding.root
    }

    private fun setBackButtonClickListener(){
        binding.btnWriteTagBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}