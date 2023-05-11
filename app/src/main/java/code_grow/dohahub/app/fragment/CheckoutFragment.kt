package code_grow.dohahub.app.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import code_grow.dohahub.app.R
import code_grow.dohahub.app.activity.AuthActivity
import code_grow.dohahub.app.databinding.FragmentCheckoutBinding
import code_grow.dohahub.app.databinding.SuccessDialogLayoutBinding
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.MessageApiResponse
import code_grow.dohahub.app.model.ProviderDetails
import code_grow.dohahub.app.model.ProviderService
import code_grow.dohahub.app.repository.HomeRepository
import code_grow.dohahub.app.repository.ProductRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.utils.CustomDialog
import code_grow.dohahub.app.view_model.AddProductViewModel
import code_grow.dohahub.app.view_model.CheckoutViewModel
import code_grow.dohahub.app.view_model.CheckoutViewModelFactory
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.airbnb.lottie.LottieDrawable
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primecalendar.common.operators.year
import com.aminography.primedatepicker.common.LabelFormatter
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.callback.SingleDayPickCallback
import com.aminography.primedatepicker.picker.theme.LightThemeFactory
import com.aminography.primedatepicker.picker.theme.base.ThemeFactory

private const val TAG = "CheckoutFragment"

class CheckoutFragment : Fragment() {
    private lateinit var binding: FragmentCheckoutBinding
    private lateinit var viewModel: CheckoutViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentCheckoutBinding.inflate(inflater, container, false)

        val providerDetails = CheckoutFragmentArgs.fromBundle(requireArguments()).provider
        val selectedService = providerDetails.services!!.find { it.isClicked }
        val viewModelFactory = CheckoutViewModelFactory(
            requireActivity().application,
            HomeRepository()
        )
        viewModel =
            ViewModelProvider(this, viewModelFactory)[CheckoutViewModel::class.java]

        binding.viewModel = viewModel
        binding.provider = providerDetails
        binding.service = selectedService
        binding.lifecycleOwner = requireActivity()


        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        val datePickerTheme = object : LightThemeFactory() {
            override val dialogBackgroundColor: Int
                get() = ContextCompat.getColor(requireContext(), R.color.home_main_background)

            override val calendarViewBackgroundColor: Int
                get() = ContextCompat.getColor(requireContext(), R.color.home_main_background)

            override val selectionBarSingleDayLabelFormatter: LabelFormatter
                get() = {
                    "${it.year}/${it.date}/${it.month.plus(1)}"
                }

        }
        val datePickerCallback = SingleDayPickCallback { date ->
            val month = date.month.plus(1)
            val day = date.dayOfMonth
//
            val dateString =
                (if (month < 10) "0$month" else "$month").plus("/")
                    .plus(if (day < 10) "0$day" else "$day").plus("/${date.year}")
//                "${date.year}/${if (day < 10) "0$day" else "$day"}/${if (month < 10) "0$month" else "$month"}"
            binding.dateEdittext.setText(dateString)
            viewModel.dateErrorLiveData.value = ""
            binding.dateEdittext.error = null
        }

        binding.dateEdittext.setOnClickListener {
            val today = CivilCalendar()
            val datePicker = PrimeDatePicker.bottomSheetWith(today)
                .pickSingleDay(datePickerCallback)
                .initiallyPickedSingleDay(today)
                .minPossibleDate(today)
                .applyTheme(datePickerTheme)
                .build()
            datePicker.show(requireActivity().supportFragmentManager, "DatePicker")
        }

        val checkoutLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    continueButtonClicked(providerDetails, selectedService!!)
                }
            }

        binding.continueButton.setOnClickListener {
            if (UserInfo.isSigned) {
                continueButtonClicked(providerDetails, selectedService!!)
            } else {
                checkoutLauncher.launch(Intent(requireContext(), AuthActivity::class.java))
            }
        }



        viewModel.startCheckoutBooleanLiveData.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.checkout()
            }
        }

        viewModel.checkoutResponse.observe(viewLifecycleOwner) {
            viewModel.startCheckout(false)
            if (it is Resource.Success<*>) {
                showSuccessDialog(
                    successMessage = it.data as String,
                )
            } else if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
            }
        }
        return binding.root
    }

    private fun continueButtonClicked(
        providerDetails: ProviderDetails,
        selectedService: ProviderService
    ) {
        viewModel.continueBtnClicked(
            providerDetails.providerId.toString(),
            selectedService.serviceId.toString(),
            selectedService.categoryId.toString(),
            selectedService.price.toString()
        )
    }

    private fun showSuccessDialog(
        dialogBehavior: DialogBehavior = ModalDialog,
        successMessage: String,
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
            findNavController().navigate(
                CheckoutFragmentDirections.actionCheckoutFragmentToMyOrdersFragment()
            )
        }

    }
}