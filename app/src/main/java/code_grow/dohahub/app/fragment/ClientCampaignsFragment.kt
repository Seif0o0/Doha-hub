package code_grow.dohahub.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import code_grow.dohahub.app.R
import code_grow.dohahub.app.adapter.MyCampaignsAdapter
import code_grow.dohahub.app.adapter.OnMyCampaignItemClickListener
import code_grow.dohahub.app.databinding.FragmentCampaignsBinding
import code_grow.dohahub.app.model.Campaign
import code_grow.dohahub.app.model.OrderResponse
import code_grow.dohahub.app.repository.CampaignsRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.view_model.CampaignsViewModel
import code_grow.dohahub.app.view_model.CampaignsViewModelFactory

class ClientCampaignsFragment : Fragment() {
    private lateinit var binding: FragmentCampaignsBinding
    private lateinit var viewModel: CampaignsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized) {
            binding
        } else {
            binding = FragmentCampaignsBinding.inflate(inflater, container, false)
            val viewModelFactory = CampaignsViewModelFactory(
                false,
                requireActivity().application,
                CampaignsRepository()
            )

            viewModel = ViewModelProvider(this, viewModelFactory)[CampaignsViewModel::class.java]
            binding.viewModel = viewModel
            binding.lifecycleOwner = requireActivity()
        }

        binding.header.text = getString(R.string.client_campaigns_header)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addCampaignButton.setOnClickListener {
            if(findNavController().currentDestination?.id == R.id.clientCampaignsFragment){
                addFragmentListener("adding")
                findNavController().navigate(ClientCampaignsFragmentDirections.actionClientCampaignsFragmentToAddCampaignFragment())
            }
        }

        val adapter = MyCampaignsAdapter(OnMyCampaignItemClickListener {
            if(findNavController().currentDestination?.id == R.id.clientCampaignsFragment){
                findNavController().navigate(ClientCampaignsFragmentDirections.actionClientCampaignsFragmentToCampaignDetailsFragment(it))
            }
        })

        binding.campaigns.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding.campaigns.adapter = adapter

        viewModel.startRequestCampaigns.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getCampaigns()
            }
        }

        viewModel.campaignsResponse.observe(viewLifecycleOwner) {
            viewModel.startRequestCampaigns(false)
            if (it is Resource.Success<*>) {
                adapter.submitList(it.data as MutableList<Campaign>)
            } else if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            }
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.startRequestCampaigns(true)
        }

        return binding.root
    }

    private fun addFragmentListener(requestKeyValue: String) {
        setFragmentResultListener(
            requestKeyValue
        ) { requestKey, bundle ->
            if (requestKey == "editing" || requestKey == "adding") {
                if (bundle["refresh"] as Boolean) {
                    viewModel.startRequestCampaigns(true)
                }
            }

        }
    }
}