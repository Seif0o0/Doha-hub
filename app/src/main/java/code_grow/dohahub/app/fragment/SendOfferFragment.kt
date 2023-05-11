package code_grow.dohahub.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.dohahub.app.R
import code_grow.dohahub.app.databinding.FragmentSendOfferBinding
import code_grow.dohahub.app.repository.CampaignsRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.utils.CustomDialog
import code_grow.dohahub.app.view_model.SendOfferViewModel
import code_grow.dohahub.app.view_model.SendOfferViewModelFactory

class SendOfferFragment : Fragment() {
    private lateinit var binding: FragmentSendOfferBinding
    private lateinit var viewModel: SendOfferViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendOfferBinding.inflate(inflater, container, false)
        val campaign = SendOfferFragmentArgs.fromBundle(requireArguments()).campaign
        val viewModelFactory = SendOfferViewModelFactory(
            campaign,
            requireActivity().application,
            CampaignsRepository()
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[SendOfferViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.submitButton.setOnClickListener {
            viewModel.submitBtnClicked()
        }

        viewModel.startSendingOffer.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.sendOffer()
            }
        }

        viewModel.sendOfferResponse.observe(viewLifecycleOwner) {
            viewModel.startSendingOffer(false)
            if (it is Resource.Success<*>) {
                CustomDialog.showSuccessDialog(
                    context = requireContext(),
                    successMessage = getString(R.string.send_offer_success_message),
                    navController = findNavController()
                )
            } else if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
            }
        }


        return binding.root
    }
}