package code_grow.dohahub.app.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.dohahub.app.R
import code_grow.dohahub.app.activity.AuthActivity
import code_grow.dohahub.app.adapter.OnProviderItemClickListener
import code_grow.dohahub.app.adapter.ProvidersAdapter
import code_grow.dohahub.app.databinding.DynamicProvidersViewBinding
import code_grow.dohahub.app.databinding.FragmentHomeBinding
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.Category
import code_grow.dohahub.app.model.DynamicHomeData
import code_grow.dohahub.app.model.HomeData
import code_grow.dohahub.app.model.User
import code_grow.dohahub.app.repository.AuthRepository
import code_grow.dohahub.app.repository.HomeRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.utils.Constants
import code_grow.dohahub.app.view_model.HomeViewModel
import code_grow.dohahub.app.view_model.HomeViewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val viewModelFactory =
            HomeViewModelFactory(requireActivity().application, HomeRepository(), AuthRepository())

        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        val auth = Firebase.auth
        if (auth.currentUser != null) {
            auth.signOut()
        }

        if (UserInfo.isSigned) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }
                UserInfo.firebaseToken = it.result
                viewModel.startUpdateToken(true)
            }
        }
        binding.filterIcon.setOnClickListener {
            showFilterDialog()
        }

        binding.sideMenuIcon.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToSideMenuFragment()
            )
        }

        binding.searchEdittext.imeOptions = EditorInfo.IME_ACTION_SEARCH
        binding.searchEdittext.setImeActionLabel(
            getString(R.string.keyboard_search_button),
            EditorInfo.IME_ACTION_SEARCH
        )
        binding.searchEdittext.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                var query = binding.searchEdittext.text.toString()
                if (query.isNotEmpty())
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToProvidersFragment(
                            -1,/* to fetch all categories */
                            query
                        )
                    )
            }
            true
        }
        binding.searchIcon.setOnClickListener {
            var query = binding.searchEdittext.text.toString()
            if (query.isNotEmpty())
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToProvidersFragment(
                        -1,/* to fetch all categories */
                        query
                    )
                )
        }
        binding.exploreAllFeatured.setOnClickListener {
            binding.searchEdittext.setText("")
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToProvidersFragment(
                    0,
                    binding.searchEdittext.text.toString()
                )
            )
        }
        val featuredAdapter = ProvidersAdapter(OnProviderItemClickListener {
            if (findNavController().currentDestination?.id == R.id.homeFragment)
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToProviderDetailsFragment(it.providerId)
                )
        })
        binding.featuredList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.featuredList.adapter = featuredAdapter

        viewModel.startRequestHomeData.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getHomeData()
            }
        }
        viewModel.homeResponse.observe(viewLifecycleOwner) {
            viewModel.startRequestHomeData(false)
            if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            } else if (it is Resource.Success<*>) {
                val data = it.data as HomeData
                // handle static list
                if (data.featuredList.isNullOrEmpty()) {
                    //best hospitals not found
                    binding.emptyFeaturedListText.visibility = View.VISIBLE
                    binding.featuredList.visibility = View.GONE
                } else {
                    binding.emptyFeaturedListText.visibility = View.GONE
                    binding.featuredList.visibility = View.VISIBLE
                    featuredAdapter.submitList(data.featuredList)
                }

                // handle dynamic lists
                val dynamicData = data.dynamicData
                if (dynamicData.isNullOrEmpty()) {
                    binding.dynamicListsContainer.visibility = View.GONE
                } else {
                    for (data in dynamicData) {
                        addProviders(data)
                    }
                }
            }
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.startRequestHomeData(true)
        }

        viewModel.startUpdateToken.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.updateToken()
            }
        }

        viewModel.updateTokenResponse.observe(viewLifecycleOwner) {
            viewModel.startUpdateToken(false)
            if (it is Resource.Success<*>) {
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
            } else if (it is Resource.Failed) {
                logout()
            }
        }
        return binding.root
    }

    private fun logout() {
        UserInfo.isSigned = false
        UserInfo.username = ""
        UserInfo.password = ""
        UserInfo.email = ""
        UserInfo.phoneNumber = ""
        UserInfo.profilePicture = ""
        UserInfo.firebaseToken = ""
        requireActivity().finish()
        startActivity(Intent(requireActivity(), AuthActivity::class.java).apply {
            putExtra("LoggedOut", true)
        })
    }

    private fun addProviders(dynamicHomeData: DynamicHomeData) {
        val dynamicBinding = DynamicProvidersViewBinding.inflate(layoutInflater)
        dynamicBinding.categoryTitle.text = dynamicHomeData.category

        dynamicBinding.exploreAllProviders.setOnClickListener {
            binding.searchEdittext.setText("")
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToProvidersFragment(
                    dynamicHomeData.categoryId!!,
                    binding.searchEdittext.text.toString()
                )
            )
        }
        val dynamicAdapter = ProvidersAdapter(OnProviderItemClickListener {
            if (findNavController().currentDestination?.id == R.id.homeFragment)
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToProviderDetailsFragment(it.providerId)
                )
        })
        dynamicAdapter.submitList(dynamicHomeData.providers)
        dynamicBinding.providerList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        dynamicBinding.providerList.adapter = dynamicAdapter
        binding.dynamicListsContainer.addView(dynamicBinding.root)
    }

    private fun showFilterDialog() {
        val filterDialog =
            FilterDialogFragment.newInstance((viewModel.categoriesResponse.value as Resource.Success<*>).data as MutableList<Category>)

        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val prevFragment =
            requireActivity().supportFragmentManager.findFragmentByTag("filterDialog")
        if (prevFragment != null)
            fragmentTransaction.remove(prevFragment)
        fragmentTransaction.addToBackStack(null)
        /* listen to filter dialog action */
        requireActivity().supportFragmentManager.setFragmentResultListener(
            "categoryId",
            viewLifecycleOwner
        ) { requestKey, bundle ->
            Log.d(TAG, "Req.Key: $requestKey, CatId: ${bundle["data"] as Int}")
            if (requestKey == "categoryId")
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToProvidersFragment(
                        bundle["data"] as Int,
                        binding.searchEdittext.text.toString()
                    )
                )

        }
        filterDialog.show(fragmentTransaction, "filterDialog")
    }
}