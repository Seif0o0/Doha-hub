package code_grow.dohahub.app.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import code_grow.dohahub.app.R
import code_grow.dohahub.app.databinding.ConfirmDialogLayoutBinding
import code_grow.dohahub.app.databinding.FragmentMyOrderDetailsBinding
import code_grow.dohahub.app.databinding.SuccessDialogLayoutBinding
import code_grow.dohahub.app.repository.OrdersRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.utils.CustomDialog
import code_grow.dohahub.app.view_model.MyOrderDetailsViewModel
import code_grow.dohahub.app.view_model.MyOrderDetailsViewModelFactory
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.airbnb.lottie.LottieDrawable

class MyOrderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentMyOrderDetailsBinding
    private lateinit var viewModel: MyOrderDetailsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyOrderDetailsBinding.inflate(inflater, container, false)
        val order = MyOrderDetailsFragmentArgs.fromBundle(requireArguments()).orderDetails
        binding.order = order
        val viewModelFactory = MyOrderDetailsViewModelFactory(
            order.orderDetails.userId,
            order.orderDetails.orderId,
            requireActivity().application,
            OrdersRepository()
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[MyOrderDetailsViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()


        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.sendMessageBackground.setOnClickListener {
            findNavController().navigate(
                MyOrderDetailsFragmentDirections.actionOrderDetailsFragmentToChatFragment(order.orderDetails.userId)
            )
        }

        binding.submitButton.setOnClickListener {
            viewModel.submitBtnClicked(order.orderDetails.status)
        }

        viewModel.showConfirmDialog.observe(viewLifecycleOwner) {
            if (it)
                showConfirmDialog()
        }
        viewModel.startDeletingOrder.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.deleteOrder()
            }
        }

        viewModel.deleteOrderResponse.observe(viewLifecycleOwner) {
            viewModel.startDeletingOrder(false)
            if (it is Resource.Success<*>) {
                showSuccessDialog(
                    successMessage = getString(R.string.delete_order_success_message),
                )
            } else if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
            }
        }

        viewModel.startFinishingOrder.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.finishOrder()
            }
        }

        viewModel.finishOrderResponse.observe(viewLifecycleOwner) {
            viewModel.startFinishingOrder(false)
            if (it is Resource.Success<*>) {
                showSuccessDialog(
                    successMessage = getString(R.string.finish_order_success_message),
                )
            } else if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
            }
        }

        viewModel.startSendingFeedback.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.sendFeedback()
            }
        }

        viewModel.sendFeedbackResponse.observe(viewLifecycleOwner) {
            viewModel.startSendingFeedback(false)
            if (it is Resource.Success<*>) {
                showSuccessDialog(
                    successMessage = getString(R.string.feedback_success_message),
                )
            } else if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
            }
        }

        binding.facebook.setOnClickListener {
            val url = order.provider.facebookLink
            if (url.isEmpty()) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = getString(R.string.social_media_link_error_message)
                )
                return@setOnClickListener
            }

            navigateToSocialMedia(url)
        }

        binding.instagram.setOnClickListener {
            val url = order.provider.instagramLink
            if (url.isEmpty()) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = getString(R.string.social_media_link_error_message)
                )
                return@setOnClickListener
            }

            navigateToSocialMedia(url)
        }

        return binding.root
    }

    private fun navigateToSocialMedia(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data =
                Uri.parse(if (!url.startsWith("http://") || !url.startsWith("https://")) "https://$url" else url)
        })
    }

    fun showSuccessDialog(
        dialogBehavior: DialogBehavior = ModalDialog,
        successMessage: String
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
            setFragmentResult("editing", bundleOf("refresh" to true))
            findNavController().popBackStack()
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
            String.format(getString(R.string.confirm_dialog_message), getString(R.string.order))

        dialogBinding.animation.setAnimation("question_animation.json")
        dialogBinding.animation.playAnimation()
        dialogBinding.animation.repeatCount = LottieDrawable.INFINITE

        dialogBinding.yesButton.setOnClickListener {
            dialogBinding.animation.cancelAnimation()
            dialog.dismiss()
            viewModel.startDeletingOrder(true)
        }

        dialogBinding.noButton.setOnClickListener {
            dialogBinding.animation.cancelAnimation()
            dialog.dismiss()
        }
    }
}