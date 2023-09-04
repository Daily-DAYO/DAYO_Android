package daily.dayo.presentation.fragment.account.findAccount

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import daily.dayo.presentation.R
import daily.dayo.presentation.databinding.FragmentFindAccountPasswordCompleteBinding
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.setOnDebounceClickListener

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
        binding.btnFindAccountPasswordCompleteBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnFindAccountPasswordCompleteNext.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_findAccountPasswordCompleteFragment_to_loginEmailFragment)
        }
    }
}