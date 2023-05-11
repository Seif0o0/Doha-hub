
package code_grow.dohahub.app.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.dohahub.app.R
import code_grow.dohahub.app.databinding.FragmentForgetPasswordBinding
import code_grow.dohahub.app.databinding.SuccessDialogLayoutBinding
import code_grow.dohahub.app.repository.AuthRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.utils.CustomDialog
import code_grow.dohahub.app.view_model.ForgetPasswordViewModel
import code_grow.dohahub.app.view_model.ForgetPasswordViewModelFactory
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.airbnb.lottie.LottieDrawable

private const val TAG = "ForgetPasswordFragment"

class ForgetPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgetPasswordBinding
    private lateinit var viewModel: ForgetPasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        if (::binding.isInitialized){
//            binding
//        }else{
        val viewModelFactory = ForgetPasswordViewModelFactory(
            requireActivity().application,
            AuthRepository()
        )
        viewModel = ViewModelProvider(
            this,
            viewModelFactory
        )[ForgetPasswordViewModel::class.java]

        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()


//        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.backToLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.sendCodeButton.setOnClickListener {
            hideKeyboard()
            viewModel.sendCodeBtnClicked()
        }

        viewModel.startSendCode.observe(viewLifecycleOwner) {
            Log.d(TAG, "SendCode : $it")
            if (it) {
                viewModel.sendCode()
            }
        }

        viewModel.sendCodeResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Success<*>) {
                viewModel.startSendCode(false)
                viewModel.changeResponseToIdle()
                showSuccessDialog()
                viewModel.emailErrorLiveData.value = ""
                binding.emailEdittext.error = null
            } else if (it is Resource.Failed) {
                viewModel.startSendCode(false)
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
        binding.successMessage.text = getString(R.string.send_code_successful_message)

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
                ForgetPasswordFragmentDirections.actionForgetPasswordFragmentToChangeForgetPasswordFragment()
            )
        }

    }
}