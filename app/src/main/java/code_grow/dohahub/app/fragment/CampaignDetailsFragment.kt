package code_grow.dohahub.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import code_grow.dohahub.app.databinding.FragmentCampaignDetailsBinding

class CampaignDetailsFragment : Fragment() {
    private lateinit var binding: FragmentCampaignDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCampaignDetailsBinding.inflate(inflater, container, false)
        val campaign = CampaignDetailsFragmentArgs.fromBundle(requireArguments()).campaignDetails
        binding.campaign = campaign
        binding.lifecycleOwner = requireActivity()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.submitOfferButton.setOnClickListener {
            findNavController().navigate(
                CampaignDetailsFragmentDirections.actionCampaignDetailsFragmentToSendOfferFragment(
                    campaign
                )
            )
        }

        binding.sendMessageButton.setOnClickListener {
            findNavController().navigate(
                CampaignDetailsFragmentDirections.actionCampaignDetailsFragmentToChatFragment(campaign.userId)
            )
        }

        return binding.root
    }
}