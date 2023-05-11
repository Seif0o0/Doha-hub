package code_grow.dohahub.app.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import code_grow.dohahub.app.R
import code_grow.dohahub.app.activity.AuthActivity
import code_grow.dohahub.app.databinding.FragmentProductDetailsBinding
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.utils.CustomDialog

class ProductDetailsFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        val product = ProductDetailsFragmentArgs.fromBundle(requireArguments()).productDetails
        binding.product = product
        binding.lifecycleOwner = requireActivity()


        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.phoneBackground.setOnClickListener {
            makePhoneCall(product.phoneNumber)
        }
        binding.addressBackground.setOnClickListener {
            navigateToAddress(product.address)
        }

        val chatLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    findNavController().navigate(
                        ProductDetailsFragmentDirections.actionProductDetailsFragmentToChatFragment(product.userId)
                    )
                }
            }

        binding.chatBackground.setOnClickListener {
            if(!UserInfo.isSigned){
                chatLauncher.launch(Intent(requireContext(),AuthActivity::class.java))
            }else
                findNavController().navigate(
                    ProductDetailsFragmentDirections.actionProductDetailsFragmentToChatFragment(product.userId)
                )
        }

        return binding.root
    }



    private fun makePhoneCall(phoneNumber: String) {
        if (phoneNumber.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${phoneNumber}")
            startActivity(intent)
        } else {
            CustomDialog.showErrorDialog(
                context = requireContext(),
                errorMessage = getString(R.string.empty_phone_error)

            )
        }
    }

    private fun navigateToAddress(address: String) {
        if (address.isNotEmpty()) {
            startActivity(
                Intent(
                    android.content.Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=$address")
                )
            )
        } else {
            CustomDialog.showErrorDialog(
                context = requireContext(),
                errorMessage = getString(R.string.empty_address_error)
            )
        }
    }

}