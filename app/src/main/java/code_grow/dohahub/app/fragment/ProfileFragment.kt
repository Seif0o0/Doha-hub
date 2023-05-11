package code_grow.dohahub.app.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.dohahub.app.R
import code_grow.dohahub.app.databinding.ErrorUploadingImageDialogLayoutBinding
import code_grow.dohahub.app.databinding.FragmentProfileBinding
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.User
import code_grow.dohahub.app.repository.AuthRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.utils.Constants
import code_grow.dohahub.app.utils.CustomDialog
import code_grow.dohahub.app.view_model.ProfileViewModel
import code_grow.dohahub.app.view_model.ProfileViewModelFactory
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

private const val TAG = "ProfileFragment"

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel

    private val profilePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val file = ImagePicker.getFile(it.data)!!
                viewModel.setStartUploadingImage(true, file)
                Glide.with(this)
                    .load(file)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.profilePicture)
            } else {
                Log.d(TAG, "PickPic Failed: $it")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.profilePic = UserInfo.profilePicture
        binding.userName = UserInfo.username
        binding.userEmail = UserInfo.email
        binding.profilePic = UserInfo.profilePicture

        val viewModelFactory =
            ProfileViewModelFactory(requireActivity().application, AuthRepository())
        viewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.updateAccountButton.setOnClickListener {
            hideKeyboard()
            viewModel.editProfileBtnClicked()
        }

        binding.profilePicturePicker.setOnClickListener {
            hideKeyboard()
            ImagePicker.with(requireActivity())
                .crop(1f, 1f)
                .cropOval()
                /* Final image resolution will be less than 1080 x 1080 (Optional) */
                .maxResultSize(1080, 1080, true)
                /* make user able to pick picture from gallery or capture it */
                .provider(ImageProvider.BOTH)
                .createIntentFromDialog { profilePictureLauncher.launch(it) }
        }

        viewModel.startUploadingImage.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.uploadImage()
            }
        }

        viewModel.uploadImageResponse.observe(viewLifecycleOwner) {
            viewModel.setStartUploadingImage(false, null)
            if (it is Resource.Failed) {
                showErrorUploadingImageDialog(context = requireContext(), errorMessage = it.message)
            }
        }

        /* observe validation flag to make registration req. */
        viewModel.editProfileBooleanLiveData.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.editProfile()
            }
        }

        /* observe register req. response */
        viewModel.editProfileResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
            } else if (it is Resource.Success<*>) {
                val user = (it.data as User)
                UserInfo.isSigned = true
                UserInfo.userId = user.userId
                UserInfo.username = user.username
                UserInfo.brief = user.brief
                UserInfo.description = user.description
                UserInfo.password = user.password
                UserInfo.email = user.email
                UserInfo.phoneNumber = user.phoneNumber
                UserInfo.profilePicture =
                    "${Constants.BASE_URL}user_img/".plus(user.profilePicture)
                UserInfo.firebaseToken = user.firebaseToken ?: ""
                UserInfo.facebookLink = user.facebookLink
                UserInfo.instagramLink = user.instagramLink
                UserInfo.youtubeLink = user.youtubeLink
                UserInfo.behanceLink = user.behanceLink
                UserInfo.isProvider = user.userType == 2
                UserInfo.isVerified = user.isVerified == 1
                CustomDialog.showSuccessDialog(
                    context = requireContext(),
                    successMessage = getString(R.string.edit_profile_successful_message),
                    navController = findNavController()
                )
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

        binding.changePassword.setOnClickListener {
            val changePassDialog = ChangePasswordDialogFragment.newInstance()

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val prevFragment =
                requireActivity().supportFragmentManager.findFragmentByTag("changPassDialog")
            if (prevFragment != null)
                fragmentTransaction.remove(prevFragment)
            fragmentTransaction.addToBackStack(null)
            changePassDialog.show(fragmentTransaction, "changPassDialog")
        }

        return binding.root
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
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
}