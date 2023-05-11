package code_grow.dohahub.app.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import code_grow.dohahub.app.R
import code_grow.dohahub.app.databinding.ChangePasswordDialogLayoutBinding
import code_grow.dohahub.app.databinding.SuccessDialogLayoutBinding
import code_grow.dohahub.app.repository.AuthRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.utils.CustomDialog
import code_grow.dohahub.app.view_model.ChangePasswordViewModel
import code_grow.dohahub.app.view_model.ChangePasswordViewModelFactory
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.airbnb.lottie.LottieDrawable

private const val TAG = "ChangePasswordDialogFragment"

class ChangePasswordDialogFragment : DialogFragment() {
    private lateinit var binding: ChangePasswordDialogLayoutBinding
    private lateinit var viewModel: ChangePasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChangePasswordDialogLayoutBinding.inflate(inflater, container, false)
        val viewModelFactory =
            ChangePasswordViewModelFactory(requireActivity().application, AuthRepository())

        viewModel = ViewModelProvider(this, viewModelFactory)[ChangePasswordViewModel::class.java]

        binding.viewModel = viewModel/* set viewModel to the veiw */
        binding.lifecycleOwner = requireActivity()

        binding.saveButton.setOnClickListener {
            hideKeyboard()
            viewModel.changePasswordClicked()
        }

        viewModel.changePasswordBooleanLiveData.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.changePassword()
            }
        }

        viewModel.changePasswordResponse.observe(viewLifecycleOwner) {
            viewModel.startChangePasswordRequest(false)
            if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
            } else if (it is Resource.Success<*>) {
                showSuccessDialog(
                    context = requireContext(),
                    successMessage = getString(R.string.change_password_successful_message),

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

    private fun showSuccessDialog(
        dialogBehavior: DialogBehavior = ModalDialog,
        context: Context,
        successMessage: String,
    ) {
        val dialog = MaterialDialog(context, dialogBehavior).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            cancelable(false)
            customView(
                R.layout.success_dialog_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val binding = SuccessDialogLayoutBinding.bind(dialog.getCustomView())
        binding.successMessage.text = successMessage

        binding.errorAnimation.setAnimation("success.json")
        binding.errorAnimation.playAnimation()
        binding.errorAnimation.repeatCount = LottieDrawable.INFINITE

        binding.okButton.setOnClickListener {
            binding.errorAnimation.cancelAnimation()
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            binding.errorAnimation.cancelAnimation()
            this@ChangePasswordDialogFragment.dismiss()
        }

    }

    companion object{
        @JvmStatic
        fun newInstance() = ChangePasswordDialogFragment()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Theme_Material_Light_Dialog_NoMinWidth);
    }
}