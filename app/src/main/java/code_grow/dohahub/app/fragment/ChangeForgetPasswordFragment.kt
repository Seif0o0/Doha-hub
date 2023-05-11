package code_grow.dohahub.app.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.dohahub.app.R
import code_grow.dohahub.app.databinding.FragmentChangeForgetPasswordBinding
import code_grow.dohahub.app.databinding.SuccessDialogLayoutBinding
import code_grow.dohahub.app.repository.AuthRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.utils.CustomDialog
import code_grow.dohahub.app.view_model.ChangeForgetPasswordViewModel
import code_grow.dohahub.app.view_model.ChangeForgetPasswordViewModelFactory
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.airbnb.lottie.LottieDrawable

class ChangeForgetPasswordFragment : Fragment() {
    private lateinit var binding: FragmentChangeForgetPasswordBinding
    private lateinit var viewModel: ChangeForgetPasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeForgetPasswordBinding.inflate(inflater, container, false)
        val viewModelFactory = ChangeForgetPasswordViewModelFactory(
            requireActivity().application,
            AuthRepository()
        )

        viewModel =
            ViewModelProvider(this, viewModelFactory)[ChangeForgetPasswordViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.saveButton.setOnClickListener {
            hideKeyboard()
            viewModel.changePasswordClicked()
        }

        viewModel.startChangePassword.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.changePassword()
            }
        }

        viewModel.changePasswordResponse.observe(viewLifecycleOwner) {
            viewModel.startChangePassword(false)
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

    private fun hideKeyboard() {
        val imm: InputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
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
        binding.successMessage.text = getString(R.string.change_password_successful_message)

        binding.errorAnimation.setAnimation("success.json")
        binding.errorAnimation.playAnimation()
        binding.errorAnimation.repeatCount = LottieDrawable.INFINITE

        binding.okButton.setOnClickListener {
            binding.errorAnimation.cancelAnimation()
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            binding.errorAnimation.cancelAnimation()
            findNavController().navigate(
                ChangeForgetPasswordFragmentDirections.actionChangeForgetPasswordFragmentToLoginFragment()
            )
        }

    }
}