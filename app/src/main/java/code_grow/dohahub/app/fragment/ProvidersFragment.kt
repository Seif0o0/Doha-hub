package code_grow.dohahub.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.dohahub.app.R
import code_grow.dohahub.app.adapter.GridProvidersAdapter
import code_grow.dohahub.app.adapter.OnGridProviderItemClickListener
import code_grow.dohahub.app.adapter.OnProviderItemClickListener
import code_grow.dohahub.app.adapter.ProvidersAdapter
import code_grow.dohahub.app.databinding.FragmentProvidersBinding
import code_grow.dohahub.app.model.DynamicHomeData
import code_grow.dohahub.app.repository.HomeRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.view_model.ProvidersViewModel
import code_grow.dohahub.app.view_model.ProvidersViewModelFactory

class ProvidersFragment : Fragment() {
    private lateinit var binding: FragmentProvidersBinding
    private lateinit var viewModel: ProvidersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (this::binding.isInitialized) {
            return binding.root
        } else {
            val categoryId = ProvidersFragmentArgs.fromBundle(requireArguments()).categoryId
            val query = ProvidersFragmentArgs.fromBundle(requireArguments()).query
            binding = FragmentProvidersBinding.inflate(inflater, container, false)
            val viewModelFactory =
                ProvidersViewModelFactory(
                    categoryId,
                    query,
                    requireActivity().application,
                    HomeRepository()
                )
            viewModel = ViewModelProvider(this, viewModelFactory)[ProvidersViewModel::class.java]
            binding.viewModel = viewModel
            binding.lifecycleOwner = requireActivity()

            binding.backButton.setOnClickListener {
                findNavController().popBackStack()
            }
            val adapter = GridProvidersAdapter(OnGridProviderItemClickListener {
                if (findNavController().currentDestination?.id == R.id.providersFragment)
                    findNavController().navigate(
                        ProvidersFragmentDirections.actionProvidersFragmentToProviderDetailsFragment(
                            it.userId!!
                        )
                    )
            })

            binding.providers.layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            binding.providers.adapter = adapter

            viewModel.startRequestProviders.observe(viewLifecycleOwner) {
                if (it) {
                    viewModel.getProviders()
                }
            }

            viewModel.providersResponse.observe(viewLifecycleOwner) {
                if (it is Resource.Success<*>) {
                    val data = it.data as DynamicHomeData
                    binding.header.text =
                        query.ifEmpty { if (categoryId == 0) getString(R.string.featured) else data.category }
                    adapter.submitList(data.providers)
                } else if (it is Resource.Failed) {
                    binding.errorView.error = it.message
                    binding.errorView.cancelAnimation = false
                }
            }

            binding.errorView.root.setOnClickListener {
                binding.errorView.root.visibility = View.GONE
                binding.errorView.cancelAnimation = true
                viewModel.startRequestProviders(true)
            }
            return binding.root
        }
    }
}