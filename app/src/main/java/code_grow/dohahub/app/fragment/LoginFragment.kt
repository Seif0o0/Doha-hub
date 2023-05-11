package code_grow.dohahub.app.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code_grow.dohahub.app.R
import code_grow.dohahub.app.activity.MainActivity
import code_grow.dohahub.app.databinding.FragmentLoginBinding
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.User
import code_grow.dohahub.app.repository.AuthRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.utils.Constants
import code_grow.dohahub.app.utils.CustomDialog
import code_grow.dohahub.app.view_model.LoginViewModel
import code_grow.dohahub.app.view_model.LoginViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

/*
    AAB passwords: Doha@hub@Password@123
    user -> 0134679852 - 123456 (id = 13)
    provider -> 1346798520 - 123456 (id = 14)
 */

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val viewModelFactory =
            LoginViewModelFactory(requireActivity().application, AuthRepository())

        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()


        requestGoogleSignIn()

        val googleLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    Log.d("LoginFragment", "email:${account.email},username:${account.displayName}")
                    firebaseAuthWithGoogleAccount(account)

                } catch (e: ApiException) {
                    Log.d("LoginFragment", "googleLogin exception: $e")
                }
            }

        binding.googleSignInButton.setOnClickListener {
            signInWithGoogle(googleLauncher)
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            Log.d("LoginFragment", "Firebase token: ${it.result} ")
            viewModel.firebaseToken = it.result
        }


        viewModel.startCheckSocial.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.checkSocial()
            }
        }

        viewModel.checkSocialResponse.observe(viewLifecycleOwner) {
            viewModel.startCheckSocial(false)

            if (it is Resource.Success<*>) {
                login(it.data as User)
                viewModel.convertCheckSocialToIdle()
            } else if (it is Resource.Failed) {
                if (it.message.lowercase() == "true") {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToRegisterFragment(
                            email = viewModel.email,
                            username = viewModel.username
                        )
                    )
                } else {
                    CustomDialog.showErrorDialog(
                        context = requireContext(),
                        errorMessage = it.message
                    )
                }
                viewModel.convertCheckSocialToIdle()
            }
            if (::googleSignInClient.isInitialized)
                googleSignInClient.signOut()
        }


        /* login button action */
        binding.loginButton.setOnClickListener {
            hideKeyboard()
            viewModel.loginBtnCLicked()
        }

        binding.forgetPassword.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToForgetPasswordFragment()
            )
        }
        /* register text action */
        binding.register.setOnClickListener {
            hideKeyboard()
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            )
        }

        /* observe validation flag to make login req. */
        viewModel.loginBooleanLiveData.observe(viewLifecycleOwner) {
            if (it) {
                // call login req. here
                viewModel.login()
            }
        }

        /* observe login req. response */
        viewModel.loginResponse.observe(viewLifecycleOwner) {
            viewModel.startLoginRequest(false)
            if (it is Resource.Failed) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
                viewModel.convertLoginToIdle()
            } else if (it is Resource.Success<*>) {
                login(it.data as User)
                viewModel.convertLoginToIdle()
            }
        }

        return binding.root
    }

    private fun login(user: User) {
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
//        startActivity(Intent(requireContext(), MainActivity::class.java))
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private fun requestGoogleSignIn() {

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)

    }

    private fun hideKeyboard() {
        val imm: InputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
    }

    private fun signInWithGoogle(googleLauncher: ActivityResultLauncher<Intent>) {
        val signInIntent = googleSignInClient.signInIntent
        googleLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        Firebase.auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                viewModel.startCheckSocial(
                    value = true,
                    email = account.email,
                    username = account.displayName
                )
            }.addOnFailureListener { e ->
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = String.format(
                        getString(R.string.google_sign_failed_message),
                        e.message
                    )
                )
            }
    }

}