package code_grow.dohahub.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.dohahub.app.R
import code_grow.dohahub.app.databinding.FragmentAddServiceBinding
import code_grow.dohahub.app.model.Category
import code_grow.dohahub.app.model.ProductCategory
import code_grow.dohahub.app.repository.MyServicesRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.utils.CustomDialog
import code_grow.dohahub.app.view_model.AddServiceViewModel
import code_grow.dohahub.app.view_model.AddServiceViewModelFactory

private const val TAG = "AddServiceFragment"

class AddServiceFragment : Fragment() {
    private lateinit var binding: FragmentAddServiceBinding
    private lateinit var viewModel: AddServiceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddServiceBinding.inflate(inflater, container, false)

        val viewModelFactory = AddServiceViewModelFactory(
            requireActivity().application,
            MyServicesRepository()
        )
        viewModel =
            ViewModelProvider(this, viewModelFactory)[AddServiceViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.submitButton.setOnClickListener {
            viewModel.submitBtnClicked()
        }

        viewModel.startRequestCategories.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getCategories()
            }
        }

        binding.autocompleteCategory.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, _, _ ->
                binding.autocompleteCategory.error = null
            }

        viewModel.categoriesResponse.observe(viewLifecycleOwner) {
            viewModel.startRequestCategories(false)
            if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                val categories = it.data as MutableList<Category>
                val categoriesName = mutableListOf<String>()
                for (category in categories) {
                    categoriesName.add(category.name)
                }
                binding.autocompleteCategory.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        R.layout.drop_down_item,
                        categoriesName
                    )
                )
            }
        }
        binding.errorView.root.setOnClickListener {
            binding.errorView.cancelAnimation = true
            viewModel.startRequestCategories(true)
        }

        viewModel.startAddingService.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.addService()
            }
        }

        viewModel.addServiceResponse.observe(viewLifecycleOwner) {
            viewModel.startAddingService(false)
            if (it is Resource.Success<*>) {
                CustomDialog.showSuccessDialog(
                    context = requireContext(),
                    successMessage = getString(R.string.add_service_successful_message),
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