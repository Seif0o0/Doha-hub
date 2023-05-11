package code_grow.dohahub.app.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.dohahub.app.R
import code_grow.dohahub.app.adapter.OnProductCategoryItemClickListener
import code_grow.dohahub.app.adapter.OnProductItemClickListener
import code_grow.dohahub.app.adapter.ProductCategoriesAdapter
import code_grow.dohahub.app.adapter.ProductsAdapter
import code_grow.dohahub.app.databinding.FragmentMyProductsBinding
import code_grow.dohahub.app.model.Product
import code_grow.dohahub.app.model.ProductCategory
import code_grow.dohahub.app.repository.ProductRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.view_model.MyProductsViewModel
import code_grow.dohahub.app.view_model.MyProductsViewModelFactory

class MyProductsFragment : Fragment() {
    private lateinit var binding: FragmentMyProductsBinding
    private lateinit var viewModel: MyProductsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized) {
            binding
        } else {
            binding = FragmentMyProductsBinding.inflate(inflater, container, false)
            val viewModelFactory = MyProductsViewModelFactory(
                requireActivity().application,
                ProductRepository()
            )
            viewModel = ViewModelProvider(this, viewModelFactory)[MyProductsViewModel::class.java]
            binding.viewModel = viewModel
            binding.lifecycleOwner = requireActivity()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addProductButton.setOnClickListener {
            findNavController().navigate(
                MyProductsFragmentDirections.actionMyProductsFragmentToAddProductFragment()
            )
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
            if (findNavController().currentDestination?.id == R.id.myProductsFragment)
                findNavController().navigate(
                    MyProductsFragmentDirections.actionMyProductsFragmentToEditProductFragment(it)
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

        return binding.root
    }
}