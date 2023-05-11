package code_grow.dohahub.app.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import code_grow.dohahub.app.R
import code_grow.dohahub.app.activity.AuthActivity
import code_grow.dohahub.app.activity.MainActivity
import code_grow.dohahub.app.databinding.FragmentSideMenuBinding
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.utils.Constants

class SideMenuFragment : Fragment() {
    private lateinit var binding: FragmentSideMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSideMenuBinding.inflate(inflater, container, false)
        updateUIWithUserInfo()

        val myProfileLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    updateUIWithUserInfo()
                    navigateTo(SideMenuFragmentDirections.actionSideMenuFragmentToProfileFragment())
                }
            }
        binding.myProfile.setOnClickListener {
            navigateTo(
                SideMenuFragmentDirections.actionSideMenuFragmentToProfileFragment(),
                launcher = myProfileLauncher,
                check = true
            )
        }
        binding.myProfileIcon.setOnClickListener {
            navigateTo(
                SideMenuFragmentDirections.actionSideMenuFragmentToProfileFragment(),
                launcher = myProfileLauncher,
                check = true
            )
        }

        binding.home.setOnClickListener { findNavController().popBackStack() }
        binding.home.setOnClickListener { findNavController().popBackStack() }

        val myItemsLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    updateUIWithUserInfo()
                    navigateTo(SideMenuFragmentDirections.actionSideMenuFragmentToMyProductsFragment())
                }
            }
        binding.myItems.setOnClickListener {
            navigateTo(
                SideMenuFragmentDirections.actionSideMenuFragmentToMyProductsFragment(),
                launcher = myItemsLauncher,
                check = true
            )
        }
        binding.allItems.setOnClickListener { navigateTo(SideMenuFragmentDirections.actionSideMenuFragmentToMarketHubFragment()) }
        binding.hubMarket.setOnClickListener { if(!UserInfo.isSigned) navigateTo(SideMenuFragmentDirections.actionSideMenuFragmentToMarketHubFragment()) }
        binding.hubMarketIcon.setOnClickListener { if(!UserInfo.isSigned) navigateTo(SideMenuFragmentDirections.actionSideMenuFragmentToMarketHubFragment()) }

        binding.allArticles.setOnClickListener { navigateTo(SideMenuFragmentDirections.actionSideMenuFragmentToArticlesFragment()) }
        binding.articles.setOnClickListener { if(!UserInfo.isSigned) navigateTo(SideMenuFragmentDirections.actionSideMenuFragmentToArticlesFragment()) }
        binding.articlesIcon.setOnClickListener { if(!UserInfo.isSigned) navigateTo(SideMenuFragmentDirections.actionSideMenuFragmentToArticlesFragment()) }

        val myArticlesLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    updateUIWithUserInfo()
                    navigateTo(SideMenuFragmentDirections.actionSideMenuFragmentToMyArticlesFragment())
                }
            }
        binding.myArticles.setOnClickListener {
            navigateTo(
                SideMenuFragmentDirections.actionSideMenuFragmentToMyArticlesFragment(),
                launcher = myArticlesLauncher,
                check = true
            )
        }

        binding.myOrdersProvider.setOnClickListener { navigateTo(SideMenuFragmentDirections.actionSideMenuFragmentToMyOrdersFragment()) }

        val myOrdersLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    updateUIWithUserInfo()
                    navigateTo(SideMenuFragmentDirections.actionSideMenuFragmentToMyOrdersFragment())
                }
            }
        binding.myOrdersUserIcon.setOnClickListener {
            navigateTo(
                SideMenuFragmentDirections.actionSideMenuFragmentToMyOrdersFragment(),
                launcher = myOrdersLauncher,
                check = true
            )
        }
        binding.myOrdersUser.setOnClickListener {
            navigateTo(
                SideMenuFragmentDirections.actionSideMenuFragmentToMyOrdersFragment(),
                launcher = myOrdersLauncher,
                check = true
            )
        }

        binding.clientOrders.setOnClickListener { navigateTo(SideMenuFragmentDirections.actionSideMenuFragmentToClientOrdersFragment()) }

        binding.myServices.setOnClickListener { navigateTo(SideMenuFragmentDirections.actionSideMenuFragmentToMyServicesFragment()) }
        binding.myServicesIcon.setOnClickListener { navigateTo(SideMenuFragmentDirections.actionSideMenuFragmentToMyServicesFragment()) }



        binding.myCampaigns.setOnClickListener { navigateTo(SideMenuFragmentDirections.actionSideMenuFragmentToMyCampaignsFragment()) }
        binding.clientCampaigns.setOnClickListener { navigateTo(SideMenuFragmentDirections.actionSideMenuFragmentToClientCampaignsFragment()) }


        binding.termsConditions.setOnClickListener { navigateTo(Constants.TERMS_CONDITIONS) }
        binding.termsConditionsIcon.setOnClickListener { navigateTo(Constants.TERMS_CONDITIONS) }

        binding.privacyPolicy.setOnClickListener { navigateTo(Constants.PRIVACY_POLICY) }
        binding.privacyPolicyIcon.setOnClickListener { navigateTo(Constants.PRIVACY_POLICY) }

        val loginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    updateUIWithUserInfo()
                    findNavController().popBackStack()
                }
            }
        binding.logoutIcon.setOnClickListener {
            if (UserInfo.isSigned) logout()
            else loginLauncher.launch(Intent(requireContext(), AuthActivity::class.java))
        }
        binding.logout.setOnClickListener {
            if (UserInfo.isSigned) logout()
            else loginLauncher.launch(Intent(requireContext(), AuthActivity::class.java))
        }

        return binding.root
    }

    private fun navigateTo(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun navigateTo(
        direction: NavDirections,
        launcher: ActivityResultLauncher<Intent>? = null,
        check: Boolean = false
    ) {
        if (check) {
            if (!UserInfo.isSigned) {
                launcher?.launch(Intent(requireContext(), AuthActivity::class.java))
            } else
                findNavController().navigate(direction)
        } else
            findNavController().navigate(direction)
    }

    private fun updateUIWithUserInfo() {
        if (UserInfo.isSigned) {
            binding.userName = UserInfo.username
            binding.email.visibility = View.VISIBLE
            binding.userEmail = UserInfo.email
            binding.profilePic = UserInfo.profilePicture
            binding.logout.text = getString(R.string.logout)
            binding.articles.setTextColor(ContextCompat.getColor(requireContext(),R.color.side_menu_subtitle_text_color))
            binding.hubMarket.setTextColor(ContextCompat.getColor(requireContext(),R.color.side_menu_subtitle_text_color))
            binding.loggedInGroup.visibility = View.VISIBLE
            if (UserInfo.isProvider) {
                binding.providerItems.visibility = View.VISIBLE
                binding.userItems.visibility = View.GONE
            } else {
                binding.providerItems.visibility = View.GONE
                binding.userItems.visibility = View.VISIBLE
            }
        } else {
            binding.userName = getString(R.string.welcome_message)
            binding.email.visibility = View.GONE
            binding.userEmail = ""
            binding.profilePic = "logo"
            binding.logout.text = getString(R.string.login)
            binding.loggedInGroup.visibility = View.GONE
            binding.providerItems.visibility = View.GONE
            binding.userItems.visibility = View.GONE
            binding.articles.setTextColor(ContextCompat.getColor(requireContext(),R.color.side_menu_title_text_color))
            binding.hubMarket.setTextColor(ContextCompat.getColor(requireContext(),R.color.side_menu_title_text_color))
        }
    }

    private fun logout() {
        UserInfo.isSigned = false
        UserInfo.username = ""
        UserInfo.password = ""
        UserInfo.email = ""
        UserInfo.phoneNumber = ""
        UserInfo.profilePicture = ""
        UserInfo.firebaseToken = ""
        updateUIWithUserInfo()
        requireActivity().finish()
        startActivity(Intent(requireActivity(), MainActivity::class.java).apply {
            putExtra("LoggedOut", true)
        })
    }
}