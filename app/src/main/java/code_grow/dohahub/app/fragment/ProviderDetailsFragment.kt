package code_grow.dohahub.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.dohahub.app.R
import code_grow.dohahub.app.adapter.OnPortfolioItemClickListener
import code_grow.dohahub.app.adapter.OnProviderServiceItemClickListener
import code_grow.dohahub.app.adapter.PortfolioAdapter
import code_grow.dohahub.app.adapter.ProviderServicesAdapter
import code_grow.dohahub.app.databinding.FragmentProviderDetailsBinding
import code_grow.dohahub.app.model.ProviderDetails
import code_grow.dohahub.app.model.ProviderService
import code_grow.dohahub.app.repository.HomeRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.utils.CustomDialog
import code_grow.dohahub.app.view_model.ProviderDetailsViewModel
import code_grow.dohahub.app.view_model.ProviderDetailsViewModelFactory

class ProviderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentProviderDetailsBinding
    private lateinit var viewModel: ProviderDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val providerId = ProviderDetailsFragmentArgs.fromBundle(requireArguments()).providerId
        if (this::binding.isInitialized) {
            binding
        } else {
            binding = FragmentProviderDetailsBinding.inflate(inflater, container, false)
            val viewModelFactory = ProviderDetailsViewModelFactory(
                providerId,
                requireActivity().application,
                HomeRepository()
            )
            viewModel =
                ViewModelProvider(this, viewModelFactory)[ProviderDetailsViewModel::class.java]
            binding.viewModel = viewModel
            binding.provider = ProviderDetails()
            binding.lifecycleOwner = requireActivity()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.checkoutButton.setOnClickListener {
            if (!isServiceSelected) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = getString(R.string.provider_service_required)
                )
                return@setOnClickListener
            }

            findNavController().navigate(
                ProviderDetailsFragmentDirections.actionProviderDetailsFragmentToCheckoutFragment(
                    (viewModel.providerDetailsResponse.value as Resource.Success<*>).data as ProviderDetails
                )
            )
        }

        viewModel.startRequestProviderDetails.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getProviderDetails()
            }
        }

        viewModel.providerDetailsResponse.observe(viewLifecycleOwner) {
            viewModel.startRequestProviderDetails(false)
            if (it is Resource.Success<*>) {
                val data = it.data as ProviderDetails
                binding.provider = data
                binding.details.text =
                    if (data.brief.isEmpty()) getString(R.string.empty_details) else data.brief
                initServices(data.services!!)
                initPortfolio(data.portfolio)
            } else if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            }
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.cancelAnimation = true
            viewModel.startRequestProviderDetails(true)
        }

        return binding.root
    }

    private var isServiceSelected = false
    private fun initServices(services: MutableList<ProviderService>) {
        val adapter =
            ProviderServicesAdapter(OnProviderServiceItemClickListener { selectedService, position ->
                val adapter = binding.packagesList.adapter as ProviderServicesAdapter
                val servicesList = adapter.currentList
                val prevSelectedService = servicesList.find {
                    it.isClicked
                }
                val prevSelectedServiceIndex = servicesList.indexOf(prevSelectedService)
                prevSelectedService?.isClicked = false

                servicesList[position].isClicked = true

                adapter.submitList(servicesList)
                adapter.notifyItemChanged(prevSelectedServiceIndex, Unit)
                adapter.notifyItemChanged(position, Unit)
                isServiceSelected = true
            })
        if (services.isEmpty()) {
            binding.emptyServicesListText.visibility = View.VISIBLE
        } else {
            adapter.submitList(services)
            binding.packagesList.adapter = adapter
            binding.packagesList.layoutManager = LinearLayoutManager(requireContext())
            binding.emptyServicesListText.visibility = View.GONE
        }


    }

    private fun initPortfolio(portfolio: MutableList<String>?) {
        if (portfolio.isNullOrEmpty()) {
            binding.portfolioList.visibility = View.GONE
            binding.emptyPortfolioListText.visibility = View.VISIBLE
        } else {
            binding.portfolioList.visibility = View.VISIBLE
            binding.emptyPortfolioListText.visibility = View.GONE
            val adapter = PortfolioAdapter(OnPortfolioItemClickListener { position ->
                if (findNavController().currentDestination?.id == R.id.providerDetailsFragment)
                    findNavController().navigate(
                        ProviderDetailsFragmentDirections.actionProviderDetailsFragmentToImagePortfolioViewerFragment(
                            portfolio = portfolio.toTypedArray(),
                            position = position
                        )
                    )
            })
            adapter.submitList(portfolio)
            binding.portfolioList.adapter = adapter
            binding.portfolioList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

    }
}