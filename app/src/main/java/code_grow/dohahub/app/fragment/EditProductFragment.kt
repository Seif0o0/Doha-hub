package code_grow.dohahub.app.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.dohahub.app.R
import code_grow.dohahub.app.databinding.ConfirmDialogLayoutBinding
import code_grow.dohahub.app.databinding.ErrorUploadingImageDialogLayoutBinding
import code_grow.dohahub.app.databinding.FragmentEditProductBinding
import code_grow.dohahub.app.model.City
import code_grow.dohahub.app.model.ProductCategory
import code_grow.dohahub.app.repository.ProductRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.utils.CustomDialog
import code_grow.dohahub.app.view_model.EditProductViewModel
import code_grow.dohahub.app.view_model.EditProductViewModelFactory
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider

private const val TAG = "EditProductFragment"

class EditProductFragment : Fragment() {
    private lateinit var binding: FragmentEditProductBinding
    private lateinit var viewModel: EditProductViewModel

    private lateinit var photoLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProductBinding.inflate(inflater, container, false)
        val product = EditProductFragmentArgs.fromBundle(requireArguments()).productDetails

        val viewModelFactory = EditProductViewModelFactory(
            product,
            requireActivity().application,
            ProductRepository()
        )
        viewModel =
            ViewModelProvider(this, viewModelFactory)[EditProductViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.startRequestCategoriesLiveData.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getCategories()
            }
        }

        viewModel.categoriesResponse.observe(viewLifecycleOwner) {
            viewModel.startRequestCategories(false)
            if (it is Resource.Failed) {
                binding.categoriesErrorView.error = it.message
                binding.categoriesErrorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                val categories = it.data as MutableList<ProductCategory>
                val categoriesName = mutableListOf<String>()
                var productCategory = ""
                for (category in categories) {
                    if (category.categoryId == product.storeId) {
                        productCategory = category.name
                    }
                    categoriesName.add(category.name)
                }
                binding.autocompleteCategory.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        R.layout.drop_down_item,
                        categoriesName
                    )
                )
                binding.autocompleteCategory.setText(productCategory, false)

                viewModel.startRequestCities(true)
            }
        }

        binding.categoriesErrorView.root.setOnClickListener {
            binding.categoriesErrorView.cancelAnimation = true
            viewModel.startRequestCategories(true)
        }

        viewModel.startRequestCitiesLiveData.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getCities()
            }
        }
        viewModel.citiesResponse.observe(viewLifecycleOwner) {
            viewModel.startRequestCities(false)
            if (it is Resource.Failed) {
                binding.citiesErrorView.error = it.message
                binding.citiesErrorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                val cities = it.data as MutableList<City>
                val citiesName = mutableListOf<String>()
                var productCity = ""
                for (city in cities) {
                    if (city.cityId == product.cityId) {
                        productCity = city.name
                    }
                    citiesName.add(city.name)
                }

                binding.autocompleteCity.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        R.layout.drop_down_item,
                        citiesName
                    )
                )

                binding.autocompleteCity.setText(productCity, false)
            }
        }

        binding.citiesErrorView.root.setOnClickListener {
            binding.citiesErrorView.cancelAnimation = true
            viewModel.startRequestCategories(true)
        }

        binding.autocompleteCategory.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, _, _ ->
                binding.autocompleteCategory.error = null
            }
        binding.autocompleteCity.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, _, _ ->
                binding.autocompleteCity.error = null
            }



        /* updating process */
        photoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val file = ImagePicker.getFile(it.data)!!
                    viewModel.setStartUploadingImage(true, file)
                    Glide.with(this)
                        .load(file)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.photo)
                } else {
                    Log.d(TAG, "PickPic Failed: $it")
                }
            }

        binding.uploadPhotoTitle.setOnClickListener {
            pickPhoto()
        }
        binding.uploadPhotoIcon.setOnClickListener {
            pickPhoto()
        }

        binding.updateButton.setOnClickListener {
            viewModel.updateBtnClicked()
        }

        binding.removePhoto.setOnClickListener {
            binding.photo.setImageDrawable(null)
            viewModel.removePhoto()
            binding.photoGroup.visibility = View.GONE
            binding.uploadPhotoTitle.text = getString(R.string.upload_photo)
        }

        viewModel.startUploadingImage.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.uploadImage()
            }
        }

        viewModel.uploadImageResponse.observe(viewLifecycleOwner) {
            viewModel.setStartUploadingImage(false, null)
            if (viewModel.uploadedImage.isEmpty()) {
                binding.uploadPhotoTitle.text = getString(R.string.upload_photo)
                binding.photoGroup.visibility = View.GONE

            } else {
                binding.uploadPhotoTitle.text = getString(R.string.change_photo)
                    if (product.productPhoto.contains(viewModel.uploadedImage)) {
                        /* the first creation of fragment to set peoduct photo */
                        Glide.with(this)
                            .load(product.productPhoto)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(binding.photo)
                    }
            }
            if (it is Resource.Failed) {
                showErrorUploadingImageDialog(context = requireContext(), errorMessage = it.message)
            }
        }

        viewModel.imageMessageLiveData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it
                )
            }
        }

        viewModel.startEditingProductBooleanLiveData.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.editProduct()
            }
        }

        viewModel.editProductResponse.observe(viewLifecycleOwner) {
            viewModel.startEditingProduct(false)
            if (it is Resource.Success<*>) {
                CustomDialog.showSuccessDialog(
                    context = requireContext(),
                    successMessage = getString(R.string.update_product_successful_message),
                    navController = findNavController()
                )
            } else if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
            }
        }

        /* deletion process */
        binding.deleteButton.setOnClickListener {
            showConfirmDialog()
        }

        viewModel.startDeletingProduct.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.deleteProduct()
            }
        }

        viewModel.deleteProductResponse.observe(viewLifecycleOwner) {
            viewModel.startDeletingProduct(false)
            if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
            } else if (it is Resource.Success<*>) {
                CustomDialog.showSuccessDialog(
                    context = requireContext(),
                    successMessage = getString(R.string.delete_product_successful_message),
                    navController = findNavController()
                )
            }
        }


        return binding.root
    }

    private fun pickPhoto() {
        ImagePicker.with(requireActivity())
            .crop(1f, 1f)
            .cropSquare()
            /* Final image resolution will be less than 1080 x 1080 (Optional) */
            .maxResultSize(1080, 1080, true)
            /* make user able to pick picture from gallery or capture it */
            .provider(ImageProvider.BOTH)
            .createIntentFromDialog { photoLauncher.launch(it) }
    }

    private fun showErrorUploadingImageDialog(
        dialogBehavior: DialogBehavior = ModalDialog,
        context: Context,
        errorMessage: String
    ) {
        val dialog = MaterialDialog(context, dialogBehavior).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            cancelOnTouchOutside(true)
            customView(
                R.layout.error_uploading_image_dialog_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val binding = ErrorUploadingImageDialogLayoutBinding.bind(dialog.getCustomView())
        binding.errorMessage.text = errorMessage

        binding.errorAnimation.setAnimation("error_dialog_animation.json")
        binding.errorAnimation.playAnimation()
        binding.errorAnimation.repeatCount = LottieDrawable.INFINITE

        binding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        binding.retryButton.setOnClickListener {
            dialog.dismiss()
            viewModel.setStartUploadingImage(
                true,
                null /* null because file is already exist in viewModel */
            )
        }

        dialog.setOnDismissListener {
            binding.errorAnimation.cancelAnimation()
        }

    }

    private fun showConfirmDialog(
        dialogBehavior: DialogBehavior = ModalDialog
    ) {
        val dialog = MaterialDialog(requireContext(), dialogBehavior).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            cancelable(false)
            customView(
                R.layout.confirm_dialog_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }
        val dialogBinding = ConfirmDialogLayoutBinding.bind(dialog.getCustomView())
        dialogBinding.message.text =
            String.format(getString(R.string.confirm_dialog_message), getString(R.string.product))

        dialogBinding.animation.setAnimation("question_animation.json")
        dialogBinding.animation.playAnimation()
        dialogBinding.animation.repeatCount = LottieDrawable.INFINITE

        dialogBinding.yesButton.setOnClickListener {
            dialogBinding.animation.cancelAnimation()
            dialog.dismiss()
            viewModel.startDeletingProduct(true)
        }

        dialogBinding.noButton.setOnClickListener {
            dialogBinding.animation.cancelAnimation()
            dialog.dismiss()
        }


    }
}