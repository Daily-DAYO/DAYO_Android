package daily.dayo.presentation.fragment.setting.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import daily.dayo.presentation.BuildConfig
import daily.dayo.presentation.R
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentInformationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InformationFragment : Fragment() {
    private var binding by autoCleared<FragmentInformationBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackButtonClickListener()
        setTermsClickListener()
        setPrivacyClickListener()
        setAppVersion()
    }

    private fun setBackButtonClickListener() {
        binding.btnInformationBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setTermsClickListener() {
        binding.layoutInformationContentsPolicyTerms.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.InformationFragment,
                action = R.id.action_informationFragment_to_policyFragment,
                args = InformationFragmentDirections.actionInformationFragmentToPolicyFragment(
                    informationType = "terms"
                ).arguments
            )
        }
    }

    private fun setPrivacyClickListener() {
        binding.layoutInformationContentsPolicyPrivacy.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.InformationFragment,
                action = R.id.action_informationFragment_to_policyFragment,
                args = InformationFragmentDirections.actionInformationFragmentToPolicyFragment(
                    informationType = "privacy"
                ).arguments
            )
        }
    }

    private fun setAppVersion() {
        binding.versionName = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0).versionName
    }
}