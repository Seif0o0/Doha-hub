package code_grow.dohahub.app.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.dohahub.app.R
import code_grow.dohahub.app.databinding.ErrorUploadingImageDialogLayoutBinding
import code_grow.dohahub.app.databinding.FragmentAddArticleBinding
import code_grow.dohahub.app.databinding.SuccessDialogLayoutBinding
import code_grow.dohahub.app.repository.ArticleRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.utils.CustomDialog
import code_grow.dohahub.app.view_model.AddArticleViewModel
import code_grow.dohahub.app.view_model.AddArticleViewModelFactory
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

private const val TAG = "AddArticleFragment"

class AddArticleFragment : Fragment() {
    private lateinit var binding: FragmentAddArticleBinding
    private lateinit var viewModel: AddArticleViewModel

    private lateinit var photoLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddArticleBinding.inflate(inflater, container, false)

        val viewModelFactory = AddArticleViewModelFactory(
            requireActivity().application,
            ArticleRepository()
        )
        viewModel =
            ViewModelProvider(this, viewModelFactory)[AddArticleViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        photoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val file = ImagePicker.getFile(it.data)!!
                    viewModel.setStartUploadingImage(true, file)
                    binding.photo.visibility = View.VISIBLE
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

        binding.submitButton.setOnClickListener {
            viewModel.submitBtnClicked()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.removePhoto.setOnClickListener {
            binding.photo.setImageDrawable(null)
            viewModel.removePhoto()
            binding.photoGroup.visibility = View.GONE
            binding.uploadPhotoTitle.text = getString(R.string.upload_photo)
        }

        viewModel.startUploadingImage.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.uploadReport()
            }
        }

        viewModel.uploadImageResponse.observe(viewLifecycleOwner) {
            viewModel.setStartUploadingImage(false, null)
            if (viewModel.uploadedImage.isEmpty()) {
                binding.uploadPhotoTitle.text = getString(R.string.upload_photo)
            } else {
                binding.uploadPhotoTitle.text = getString(R.string.change_photo)
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

        viewModel.startAddingArticle.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.addArticle()
            }
        }

        viewModel.addArticleResponse.observe(viewLifecycleOwner) {
            viewModel.startAddingArticle(false)
            if (it is Resource.Success<*>) {
                val prevFragmentId = findNavController().previousBackStackEntry?.destination?.id
                if (prevFragmentId == R.id.myArticlesFragment) {
                    showSuccessDialog()
                } else {
                    CustomDialog.showSuccessDialog(
                        context = requireContext(),
                        successMessage = getString(R.string.add_article_successful_message),
                        navController = findNavController()
                    )
                }

            } else if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
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

    fun showSuccessDialog(
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
        binding.successMessage.text = getString(R.string.add_article_successful_message)

        binding.errorAnimation.setAnimation("success.json")
        binding.errorAnimation.playAnimation()
        binding.errorAnimation.repeatCount = LottieDrawable.INFINITE

        binding.okButton.setOnClickListener {
            binding.errorAnimation.cancelAnimation()
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            binding.errorAnimation.cancelAnimation()
            setFragmentResult("adding", bundleOf("refresh" to true))
            findNavController().popBackStack()
        }

    }
}