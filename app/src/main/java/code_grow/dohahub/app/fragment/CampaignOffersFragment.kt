package code_grow.dohahub.app.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import code_grow.dohahub.app.R
import code_grow.dohahub.app.adapter.CampaignOffersAdapter
import code_grow.dohahub.app.adapter.OnOfferItemClickListener
import code_grow.dohahub.app.databinding.FragmentCampaignOffersBinding
import code_grow.dohahub.app.databinding.OfferDetailsDialogLayoutBinding
import code_grow.dohahub.app.databinding.SuccessDialogLayoutBinding
import code_grow.dohahub.app.model.CampaignOffer
import code_grow.dohahub.app.repository.CampaignsRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.utils.CustomDialog
import code_grow.dohahub.app.view_model.CampaignOffersViewModel
import code_grow.dohahub.app.view_model.CampaignOffersViewModelFactory
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.airbnb.lottie.LottieDrawable

class CampaignOffersFragment : Fragment() {
    private lateinit var binding: FragmentCampaignOffersBinding
    private lateinit var viewModel: CampaignOffersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCampaignOffersBinding.inflate(inflater, container, false)
        val campaignId = CampaignOffersFragmentArgs.fromBundle(requireArguments()).campaignId
        val viewModelFactory = CampaignOffersViewModelFactory(
            campaignId,
            requireActivity().application,
            CampaignsRepository()
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[CampaignOffersViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()


        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }


        val adapter = CampaignOffersAdapter(OnOfferItemClickListener { campaignOffer, showDetails ->
            if (showDetails) {
                showOfferDetails(campaignOffer.details)
            } else {
                if (findNavController().currentDestination?.id == R.id.campaignOffersFragment) {
                    viewModel.startAcceptOffer(
                        true,
                        offerId = campaignOffer.offerId,
                        providerId = campaignOffer.providerId
                    )
                }
            }

        })
        binding.offers.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding.offers.adapter = adapter

        viewModel.startRequestOffers.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getOffers()
            }
        }

        viewModel.offersResponse.observe(viewLifecycleOwner) {
            viewModel.startRequestOffers(false)
            if (it is Resource.Success<*>) {
                adapter.submitList(it.data as MutableList<CampaignOffer>)
            } else if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            }
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.cancelAnimation = true
            viewModel.startRequestOffers(true)
        }


        viewModel.startAcceptOffer.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.acceptOffer()
            }
        }

        viewModel.acceptOfferResponse.observe(viewLifecycleOwner) {
            viewModel.startAcceptOffer(false)
            if (it is Resource.Success<*>) {
                // success
                showSuccessDialog(successMessage = getString(R.string.offer_accept_success_message))
            } else if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
            }
        }




        return binding.root
    }

    private fun showOfferDetails(offerDetails: String) {
        val dialog = MaterialDialog(requireContext(), ModalDialog).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            cancelable(false)
            customView(
                R.layout.offer_details_dialog_layout,
                noVerticalPadding = true,
                dialogWrapContent = true,
                scrollable = true
            )
        }

        val detailsBinding = OfferDetailsDialogLayoutBinding.bind(dialog.getCustomView())
        detailsBinding.offerDetails = offerDetails

        detailsBinding.dismissText.setOnClickListener {
            dialog.dismiss()
        }
    }


fun showSuccessDialog(
    dialogBehavior: DialogBehavior = ModalDialog,
    successMessage: String,
) {
    val dialog = MaterialDialog(requireContext(), dialogBehavior).show {
        cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
        cancelable(false)
        customView(
            R.layout.success_dialog_layout,
            noVerticalPadding = true,
            dialogWrapContent = true
        )
    }

    val binding = SuccessDialogLayoutBinding.bind(dialog.getCustomView())
    binding.successMessage.text = successMessage

    binding.errorAnimation.setAnimation("success.json")
    binding.errorAnimation.playAnimation()
    binding.errorAnimation.repeatCount = LottieDrawable.INFINITE

    binding.okButton.setOnClickListener {
        binding.errorAnimation.cancelAnimation()
        dialog.dismiss()
    }

    dialog.setOnDismissListener {
        binding.errorAnimation.cancelAnimation()
//            setFragmentResult("acceptOffer", bundleOf("refresh" to true))
        findNavController().popBackStack(R.id.myCampaignsFragment, false)
    }

}
}