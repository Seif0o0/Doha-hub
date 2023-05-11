package code_grow.dohahub.app.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.dohahub.app.R
import code_grow.dohahub.app.activity.MainActivity
import code_grow.dohahub.app.databinding.FragmentRegisterBinding
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.User
import code_grow.dohahub.app.repository.AuthRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.utils.Constants
import code_grow.dohahub.app.utils.CustomDialog
import code_grow.dohahub.app.view_model.RegisterViewModel
import code_grow.dohahub.app.view_model.RegisterViewModelFactory

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val viewModelFactory =
            RegisterViewModelFactory(requireActivity().application, AuthRepository())

        viewModel = ViewModelProvider(this, viewModelFactory)[RegisterViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()
        val args = RegisterFragmentArgs.fromBundle(requireArguments())
        val email = args.email
        val username = args.username
        viewModel.emailLiveData.value = email?:""
        viewModel.userNameLiveData.value = username?:""
        /* register button action */
        binding.registerButton.setOnClickListener {
            hideKeyboard()
            viewModel.registerBtnClicked()
        }

        binding.login.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.userOption.setOnClickListener {
            viewModel.changeUserType(false)
        }

        binding.providerOption.setOnClickListener {
            viewModel.changeUserType(true)
        }

        /* observe validation flag to make register req. */
        viewModel.registerBooleanLiveData.observe(viewLifecycleOwner) {
            if (it) {
                // call register req. here
                viewModel.register()
            }
        }

        /* observe register req. response */
        viewModel.registerResponse.observe(viewLifecycleOwner) {
            viewModel.startRegisterRequest(false)
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
                requireActivity().setResult(Activity.RESULT_OK)
                requireActivity().finish()
//                startActivity(Intent(requireContext(), MainActivity::class.java))
            }
        }

        return binding.root
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
    }
}