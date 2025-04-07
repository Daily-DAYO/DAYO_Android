package daily.dayo.presentation.fragment.setting.changePassword

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.presentation.R
import daily.dayo.presentation.common.ButtonActivation
import daily.dayo.presentation.common.HideKeyBoardUtil
import daily.dayo.presentation.common.ReplaceUnicode.trimBlankText
import daily.dayo.presentation.common.SetTextInputLayout
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentSettingChangePasswordCurrentBinding
import daily.dayo.presentation.viewmodel.AccountViewModel
import java.util.regex.Pattern

@AndroidEntryPoint
class SettingChangePasswordCurrentFragment : Fragment() {
    private var binding by autoCleared<FragmentSettingChangePasswordCurrentBinding>()
    private val accountViewModel by activityViewModels<AccountViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingChangePasswordCurrentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEditText()
        setBackClickListener()
        setTextChangeListener()
        setNextClickListener()
        setLimitEditTextInputType()
        setTextEditorActionListener()
        verifyPassword()
    }

    override fun onResume() {
        if (binding.etSettingChangePasswordCurrentUserInput.text.isNullOrEmpty())
            ButtonActivation.setSignupButtonInactive(requireContext(), binding.btnSettingChangePasswordCurrentNext)
        super.onResume()
    }

    private fun setBackClickListener() {
        binding.btnSettingChangePasswordCurrentBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnSettingChangePasswordCurrentNext.setOnDebounceClickListener {
            checkPassword()
        }
    }

    private fun initEditText() {
        SetTextInputLayout.setEditTextTheme(
            requireContext(),
            binding.layoutSettingChangePasswordCurrentUserInput,
            binding.etSettingChangePasswordCurrentUserInput,
            binding.etSettingChangePasswordCurrentUserInput.text.isNullOrEmpty()
        )
    }

    private fun setTextChangeListener() {
        with(binding.etSettingChangePasswordCurrentUserInput) {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    // Edit Text 상태
                    with(binding.layoutSettingChangePasswordCurrentUserInput) {
                        isErrorEnabled = false
                        SetTextInputLayout.setEditTextTheme(
                            requireContext(),
                            binding.layoutSettingChangePasswordCurrentUserInput,
                            binding.etSettingChangePasswordCurrentUserInput,
                            false
                        )
                    }

                    // 버튼 활성 상태
                    if (s.toString().isNotEmpty()) {
                        ButtonActivation.setSignupButtonActive(
                            requireContext(),
                            binding.btnSettingChangePasswordCurrentNext
                        )
                    } else {
                        ButtonActivation.setSignupButtonInactive(
                            requireContext(),
                            binding.btnSettingChangePasswordCurrentNext
                        )
                    }
                }
            })
        }
    }

    private fun checkPassword() {
        accountViewModel.requestCheckCurrentPassword(
            inputPassword = trimBlankText(binding.etSettingChangePasswordCurrentUserInput.text)
        )
    }

    private fun verifyPassword() {
//        accountViewModel.checkCurrentPasswordSuccess.observe(viewLifecycleOwner) {
//            if (it.getContentIfNotHandled() == true) {
//                // 로그인 성공 시 다름 화면으로 이동
//                findNavController().navigateSafe(
//                    currentDestinationId = R.id.SettingChangePasswordCurrentFragment,
//                    action = R.id.action_settingChangePasswordCurrentFragment_to_settingChangePasswordNewFragment
//                )
//            } else if (it.getContentIfNotHandled() == false) {
//                // 실패 시
//                SetTextInputLayout.setEditTextErrorTheme(
//                    requireContext(),
//                    binding.layoutSettingChangePasswordCurrentUserInput,
//                    binding.etSettingChangePasswordCurrentUserInput,
//                    getString(R.string.setting_change_password_current_fail_message),
//                    false
//                )
//            }
//        }
    }

    private fun setLimitEditTextInputType() {
        // InputFilter로 띄어쓰기 입력만 막고 나머지는 안내메시지로 띄워지도록 사용자에게 유도
        val filterInputCheck = InputFilter { source, start, end, dest, dstart, dend ->
            val ps = Pattern.compile("^[ ]+\$")
            if (ps.matcher(source).matches()) {
                return@InputFilter ""
            }
            null
        }
        binding.etSettingChangePasswordCurrentUserInput.filters = arrayOf(filterInputCheck)
    }

    private fun setTextEditorActionListener() {
        binding.etSettingChangePasswordCurrentUserInput.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(
                        requireContext(),
                        binding.etSettingChangePasswordCurrentUserInput
                    )
                    true
                }

                else -> false
            }
        }
    }
}