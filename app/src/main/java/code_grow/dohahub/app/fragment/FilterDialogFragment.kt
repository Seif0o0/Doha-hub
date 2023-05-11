package code_grow.dohahub.app.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.dohahub.app.R
import code_grow.dohahub.app.adapter.CategoriesAdapter
import code_grow.dohahub.app.adapter.OnCategoryItemClickListener
import code_grow.dohahub.app.databinding.FilterDialogLayoutBinding
import code_grow.dohahub.app.model.Category
import code_grow.dohahub.app.repository.HomeRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.utils.CustomDialog
import code_grow.dohahub.app.view_model.FilterDialogViewModel
import code_grow.dohahub.app.view_model.FilterDialogViewModelFactory

private const val TAG = "FilterDialogFragment"

class FilterDialogFragment : DialogFragment() {
    private lateinit var binding: FilterDialogLayoutBinding
    private lateinit var viewModel: FilterDialogViewModel
    private val categories = mutableListOf<Category>()
    private var selectedCategoryId = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FilterDialogLayoutBinding.inflate(inflater, container, false)
        val viewModelFactory =
            FilterDialogViewModelFactory(requireActivity().application, HomeRepository())

        viewModel = ViewModelProvider(this, viewModelFactory)[FilterDialogViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        val argsList = requireArguments().getParcelableArray("categories")

        val adapter = CategoriesAdapter(OnCategoryItemClickListener { selectedCategory ->

            val prevSelectedCategory = categories.find {
                it.isClicked
            }
            val prevSelectedCategoryIndex = categories.indexOf(prevSelectedCategory)
            if (prevSelectedCategory != null)
                prevSelectedCategory.isClicked = false

            val selectedCategoryIndex = categories.indexOf(selectedCategory)
            selectedCategory.isClicked = true

            (binding.categoryList.adapter as CategoriesAdapter).submitList(categories)
            (binding.categoryList.adapter as CategoriesAdapter).notifyItemChanged(
                prevSelectedCategoryIndex, Unit
            )
            (binding.categoryList.adapter as CategoriesAdapter).notifyItemChanged(
                selectedCategoryIndex, Unit
            )
            selectedCategoryId = selectedCategory.categoryId
        })

        binding.categoryList.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryList.adapter = adapter

        if (argsList.isNullOrEmpty()) {
            // Req. that called in HomeFragment is failed so retry here
            viewModel.startRequestCategories(true)
        } else {
            adapter.submitList(submitAdapterList((argsList as Array<Category>).toMutableList()))
        }

        viewModel.startRequestCategories.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getCategories()
            }
        }

        viewModel.categoriesResponse.observe(viewLifecycleOwner) {
            viewModel.startRequestCategories(false)
            if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                adapter.submitList(submitAdapterList(it.data as MutableList<Category>))
            }
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.startRequestCategories(true)
        }


        binding.confirmationButton.setOnClickListener {
            if (selectedCategoryId == 0) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = getString(R.string.pick_category_to_filter)
                )
                return@setOnClickListener
            }
            setFragmentResult("categoryId", bundleOf("data" to selectedCategoryId))
            dismiss()
        }


        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Theme_Material_Light_Dialog_NoMinWidth);
    }

    private fun submitAdapterList(list: MutableList<Category>): MutableList<Category> {
        categories.clear()
        categories.addAll(list)
//        categories.add(0, Category(0, getString(R.string.all), "", true))
        return categories
    }

    override fun onDestroy() {
        super.onDestroy()
        categories.onEach { category ->
            category.isClicked = category.categoryId == 0
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(categories: MutableList<Category>?) =
            FilterDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArray("categories", categories?.toTypedArray())
                }
            }
    }
}