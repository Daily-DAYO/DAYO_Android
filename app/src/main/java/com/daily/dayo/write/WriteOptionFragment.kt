package com.daily.dayo.write

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentWriteOptionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WriteOptionFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentWriteOptionBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWriteOptionBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnWriteOptionConfirm.setOnClickListener {
            Toast.makeText(requireContext(),"업로드 버튼 클릭", Toast.LENGTH_SHORT).show()
        }
        binding.layoutWriteOptionTag.setOnClickListener {
            findNavController().navigate(R.id.action_writeOptionFragment_to_writeTagFragment)
        }
    }
}