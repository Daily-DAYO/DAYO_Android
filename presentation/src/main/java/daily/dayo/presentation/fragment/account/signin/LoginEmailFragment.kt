package daily.dayo.presentation.fragment.account.signin

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.presentation.R
import daily.dayo.presentation.activity.MainActivity
import daily.dayo.presentation.common.ButtonActivation
import daily.dayo.presentation.common.HideKeyBoardUtil
import daily.dayo.presentation.common.ReplaceUnicode.trimBlankText
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.dialog.LoadingAlertDialog
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentLoginEmailBinding
import daily.dayo.presentation.viewmodel.AccountViewModel

@AndroidEntryPoint
class LoginEmailFragment : Fragment() {
    private var binding by autoCleared<FragmentLoginEmailBinding> {
        LoadingAlertDialog.hideLoadingDialog(
            loadingAlertDialog
        )
    }
    private val loginViewModel by activityViewModels<AccountViewModel>()
    private var isLoginButtonClick = false // TODO : 첫 화면에서 로그인 실패 메시지 등장으로 인한 임시 해결
    private lateinit var loadingAlertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentLoginEmailBinding.inflate(inflater, container, false)
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())
        setBackClickListener()
        setNextClickListener()
        loginSuccess()
        initEditText()
        setTextEditorActionListener()
        activationNextButton()
        setForgetAccountClickListener()
        setSignupClickListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ ->
            HideKeyBoardUtil.hide(requireContext(), binding.etLoginEmailAddress)
            HideKeyBoardUtil.hide(requireContext(), binding.etLoginEmailPassword)
            true
        }
    }

    private fun setTextEditorActionListener() {
        binding.etLoginEmailAddress.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    HideKeyBoardUtil.hide(requireContext(), binding.etLoginEmailAddress)
                    HideKeyBoardUtil.hide(requireContext(), binding.etLoginEmailPassword)
                    true
                }

                else -> false
            }
        }
    }

    private fun setBackClickListener() {
        binding.btnLoginEmailBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnLoginEmailNext.setOnDebounceClickListener {
            LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
            isLoginButtonClick = true
            loginViewModel.requestLoginEmail(
                email = trimBlankText(binding.etLoginEmailAddress.text),
                password = trimBlankText(binding.etLoginEmailPassword.text),
            )
        }
    }

    private fun loginSuccess() {
        loginViewModel.loginSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.peekContent()) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                requireActivity().finish()
            } else {
                if (isLoginButtonClick) {
                    Log.e(ContentValues.TAG, "로그인 실패")
                    LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
                    if (loginViewModel.isLoginFailByUncorrected.value?.getContentIfNotHandled() == true) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.login_email_alert_message_fail),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun initEditText() {
        with(binding.etLoginEmailAddress) {
            setOnFocusChangeListener { _, hasFocus -> //
                with(binding.layoutLoginEmailAddress) {
                    if (hasFocus) {
                        hint = ""
                        binding.etLoginEmailAddress.backgroundTintList =
                            resources.getColorStateList(
                                R.color.primary_green_23C882,
                                context?.theme
                            )
                    } else if (!binding.etLoginEmailAddress.text.isNullOrEmpty()) {
                        hint = ""
                        binding.etLoginEmailAddress.backgroundTintList = null
                    } else {
                        hint = getString(R.string.hint_login_email_address)
                        binding.etLoginEmailAddress.backgroundTintList = null
                    }
                }
            }
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    with(binding.layoutLoginEmailAddress) {
                        boxStrokeColor =
                            resources.getColor(R.color.primary_green_23C882, context?.theme)
                    }
                }
            })
        }
        with(binding.etLoginEmailPassword) {
            setOnFocusChangeListener { _, hasFocus -> // Title
                with(binding.layoutLoginEmailPassword) {
                    if (hasFocus) {
                        hint = ""
                        binding.etLoginEmailPassword.backgroundTintList =
                            resources.getColorStateList(
                                R.color.primary_green_23C882,
                                context?.theme
                            )
                    } else if (!binding.etLoginEmailPassword.text.isNullOrEmpty()) {
                        hint = ""
                        binding.etLoginEmailPassword.backgroundTintList = null
                    } else {
                        hint = getString(R.string.hint_login_email_password)
                        binding.etLoginEmailPassword.backgroundTintList = null
                    }
                }
            }
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    with(binding.layoutLoginEmailPassword) {
                        boxStrokeColor =
                            resources.getColor(R.color.primary_green_23C882, context?.theme)
                    }
                }
            })
        }
    }

    private fun activationNextButton() {
        with(binding) {
            etLoginEmailAddress.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!s.toString()
                            .isNullOrEmpty() && !etLoginEmailPassword.text.isNullOrEmpty()
                    ) {
                        ButtonActivation.setSignupButtonActive(
                            requireContext(),
                            binding.btnLoginEmailNext
                        )
                    } else {
                        ButtonActivation.setSignupButtonInactive(
                            requireContext(),
                            binding.btnLoginEmailNext
                        )
                    }
                }
            })
            etLoginEmailPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!s.toString()
                            .isNullOrEmpty() && !etLoginEmailAddress.text.isNullOrEmpty()
                    ) {
                        ButtonActivation.setSignupButtonActive(
                            requireContext(),
                            binding.btnLoginEmailNext
                        )
                    } else {
                        ButtonActivation.setSignupButtonInactive(
                            requireContext(),
                            binding.btnLoginEmailNext
                        )
                    }
                }
            })
        }
    }

    private fun setForgetAccountClickListener() {
        binding.tvLoginEmailForgotPassword.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.LoginEmailFragment,
                action = R.id.action_loginEmailFragment_to_findAccountPasswordCheckEmail
            )
        }
    }

    private fun setSignupClickListener() {
        binding.tvLoginEmailSignup.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.LoginEmailFragment,
                action = R.id.action_loginEmailFragment_to_signupEmailSetEmailAddressFragment
            )
        }
    }
}