package daily.dayo.presentation.fragment.account.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentSignupEmailCompleteBinding
import daily.dayo.presentation.activity.MainActivity
import daily.dayo.presentation.viewmodel.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupEmailCompleteFragment : Fragment() {
    private var binding by autoCleared<FragmentSignupEmailCompleteBinding>()
    private val loginViewModel by activityViewModels<AccountViewModel>()
    private val args by navArgs<SignupEmailCompleteFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupEmailCompleteBinding.inflate(inflater, container, false)
        setCloseClickListener()
        setUserNickname()
        return binding.root
    }

    private fun setUserNickname() {
        binding.userNickname = args.nickname
    }

    private fun setCloseClickListener() {
        binding.btnSignupEmailCompleteClose.setOnDebounceClickListener {
            if (loginViewModel.loginSuccess.value?.peekContent() == true) {
                val intent = Intent(activity, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                activity?.finish()
            } else {
                findNavController().navigateUp()
            }
        }
    }
}