package code_grow.dohahub.app.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.dohahub.app.R
import code_grow.dohahub.app.activity.AuthActivity
import code_grow.dohahub.app.adapter.*
import code_grow.dohahub.app.databinding.FragmentHubMarketBinding
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.Product
import code_grow.dohahub.app.model.ProductCategory
import code_grow.dohahub.app.repository.ProductRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.view_model.HubMarketViewModelFactory
import code_grow.dohahub.app.view_model.MarketHubViewModel
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

private const val TAG = "HubMarketFragment"

class HubMarketFragment : Fragment() {
    private lateinit var binding: FragmentHubMarketBinding
    private lateinit var viewModel: MarketHubViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (this::binding.isInitialized) {
            binding
        } else {
            binding = FragmentHubMarketBinding.inflate(inflater, container, false)
            val viewModelFactory = HubMarketViewModelFactory(
                requireActivity().application,
                ProductRepository()
            )
            viewModel = ViewModelProvider(this, viewModelFactory)[MarketHubViewModel::class.java]
            binding.viewModel = viewModel
            binding.lifecycleOwner = requireActivity()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        val addProductLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    findNavController().navigate(
                        HubMarketFragmentDirections.actionMarketHubFragmentToAddProductFragment()
                    )
                }
            }
        binding.addProductButton.setOnClickListener {
            if (UserInfo.isSigned)
                findNavController().navigate(
                    HubMarketFragmentDirections.actionMarketHubFragmentToAddProductFragment()
                )
            else
                addProductLauncher.launch(Intent(requireContext(), AuthActivity::class.java))
        }
        val categoriesAdapter =
            ProductCategoriesAdapter(OnProductCategoryItemClickListener { selectedCategory ->
                val adapter = binding.categoriesList.adapter as ProductCategoriesAdapter
                val categories = adapter.currentList
                val prevSelectedCategory = categories.find {
                    it.isClicked
                }
                val prevSelectedCategoryIndex = categories.indexOf(prevSelectedCategory)
                prevSelectedCategory!!.isClicked = false

                val selectedCategoryIndex = categories.indexOf(selectedCategory)
                selectedCategory.isClicked = true

                adapter.submitList(categories)
                adapter.notifyItemChanged(prevSelectedCategoryIndex, Unit)
                adapter.notifyItemChanged(selectedCategoryIndex, Unit)

                viewModel.startFilteringByCategory(selectedCategory.categoryId)

            })

        binding.categoriesList.adapter = categoriesAdapter
        binding.categoriesList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        viewModel.startRequestCategories.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getCategories()
            }
        }
        viewModel.categoriesResponse.observe(viewLifecycleOwner) {
            viewModel.startRequestCategories(false)
            if (it is Resource.Success<*>) {
                categoriesAdapter.submitList(it.data as MutableList<ProductCategory>)
                viewModel.startRequestProducts(true)
            } else if (it is Resource.Failed) {
                binding.fullErrorView.error = it.message
                binding.fullErrorView.cancelAnimation = false
            }
        }

        binding.fullErrorView.root.setOnClickListener {
            binding.fullErrorView.root.visibility = View.GONE
            binding.fullErrorView.cancelAnimation = true
            viewModel.startRequestCategories(true)
        }


        viewModel.startRequestProducts.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getProducts()
            }
        }

        val productsAdapter = ProductsAdapter(OnProductItemClickListener {
            if (findNavController().currentDestination?.id == R.id.hubMarketFragment)
                findNavController().navigate(
                    HubMarketFragmentDirections.actionHubMarketFragmentToProductDetailsFragment(it)
                )
        })

        binding.productsList.adapter = productsAdapter
        binding.productsList.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        viewModel.productsResponse.observe(viewLifecycleOwner) {
            viewModel.startRequestProducts(false)
            if (it is Resource.Success<*>) {
                productsAdapter.submitList(it.data as MutableList<Product>)
            } else if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            }
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.startRequestProducts(true)
        }


        binding.searchEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.startSearching(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        KeyboardVisibilityEvent.setEventListener(
            requireActivity()
        ) {
            if (it) {
                binding.addProductButton.visibility = View.GONE
            } else {
                binding.addProductButton.visibility = View.VISIBLE
            }
        }

        return binding.root
    }

}