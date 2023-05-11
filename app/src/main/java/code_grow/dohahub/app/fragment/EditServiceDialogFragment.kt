package code_grow.dohahub.app.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import code_grow.dohahub.app.R
import code_grow.dohahub.app.databinding.FragmentDialogEditServiceBinding
import code_grow.dohahub.app.databinding.SuccessDialogLayoutBinding
import code_grow.dohahub.app.model.Category
import code_grow.dohahub.app.model.MyService
import code_grow.dohahub.app.repository.MyServicesRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.utils.CustomDialog
import code_grow.dohahub.app.view_model.EditServiceViewModel
import code_grow.dohahub.app.view_model.EditServiceViewModelFactory
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.airbnb.lottie.LottieDrawable

class EditServiceDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentDialogEditServiceBinding
    private lateinit var viewModel: EditServiceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDialogEditServiceBinding.inflate(inflater, container, false)

        val service = requireArguments().getParcelable<MyService>("service")

        val viewModelFactory = EditServiceViewModelFactory(
            service, requireActivity().application,
            MyServicesRepository()
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[EditServiceViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        binding.submitButton.setOnClickListener {
            viewModel.submitBtnClicked()
        }

        binding.autocompleteCategory.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, _, _ ->
                binding.autocompleteCategory.error = null
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

                binding.autocompleteCategory.setText(service!!.categoryName, false)
            }
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.cancelAnimation = true
            viewModel.startRequestCategories(true)
        }

        viewModel.startEditingService.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.editService()
            }
        }

        viewModel.editServiceResponse.observe(viewLifecycleOwner) {
            viewModel.startEditingService(false)
            if (it is Resource.Success<*>) {
                showSuccessDialog()
            } else if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
            }
        }


        return binding.root
    }

    private fun showSuccessDialog(
        dialogBehavior: DialogBehavior = ModalDialog,
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
        binding.successMessage.text = getString(R.string.edit_service_successful_message)

        binding.errorAnimation.setAnimation("success.json")
        binding.errorAnimation.playAnimation()
        binding.errorAnimation.repeatCount = LottieDrawable.INFINITE

        binding.okButton.setOnClickListener {
            binding.errorAnimation.cancelAnimation()
            setFragmentResult("isEdited", bundleOf("edited" to true))
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            binding.errorAnimation.cancelAnimation()
            dismiss()
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Theme_Material_Light_Dialog_NoMinWidth);
    }

    companion object {
        @JvmStatic
        fun newInstance(service: MyService) = EditServiceDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable("service", service)
            }
        }
    }
}