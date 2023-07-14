package com.daily.dayo.presentation.fragment.policy

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.BuildConfig
import com.daily.dayo.R
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentPolicyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PolicyFragment : Fragment() {
    private var binding by autoCleared<FragmentPolicyBinding>()
    private val args by navArgs<PolicyFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPolicyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initActionbar()
        showPolicy(args.informationType)
        setWebViewBackClickListener()
    }

    private fun initActionbar() {
        binding.btnPolicyActionBarBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
        binding.tvPolicyActionBarTitle.text =
            if (args.informationType == "privacy") getString(R.string.policy_privacy)
            else getString(R.string.policy_terms)
    }

    private fun showPolicy(informationType: String) {
        with(binding.webviewPolicyContents) {
            visibility = View.VISIBLE
            apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = false
            }
            loadUrl("${BuildConfig.BASE_URL}/$informationType.html")
        }
        binding.webviewPolicyContents.visibility = View.VISIBLE
    }

    private fun setWebViewBackClickListener() {
        binding.webviewPolicyContents.setOnKeyListener(
            View.OnKeyListener { v, keyCode, event ->
                if (event.action !== KeyEvent.ACTION_DOWN) return@OnKeyListener true
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    findNavController().navigateUp()
                    return@OnKeyListener true
                }
                false
            }
        )
    }
}