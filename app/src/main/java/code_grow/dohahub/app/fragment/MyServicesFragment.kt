package code_grow.dohahub.app.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.dohahub.app.R
import code_grow.dohahub.app.adapter.MyServicesAdapter
import code_grow.dohahub.app.adapter.OnMyServiceItemClickListener
import code_grow.dohahub.app.databinding.ConfirmDialogLayoutBinding
import code_grow.dohahub.app.databinding.FragmentMyServicesBinding
import code_grow.dohahub.app.model.DynamicMyServiceData
import code_grow.dohahub.app.repository.MyServicesRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.utils.CustomDialog
import code_grow.dohahub.app.utils.ItemTouchHelperAdapter
import code_grow.dohahub.app.utils.SwipeHelperCallback
import code_grow.dohahub.app.view_model.MyServicesViewModel
import code_grow.dohahub.app.view_model.MyServicesViewModelFactory
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.airbnb.lottie.LottieDrawable

private const val TAG = "MyServicesFragment"

class MyServicesFragment : Fragment(), ItemTouchHelperAdapter {

    private lateinit var binding: FragmentMyServicesBinding
    private lateinit var viewModel: MyServicesViewModel
    private lateinit var adapter: MyServicesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized)
            binding
        else {
            binding = FragmentMyServicesBinding.inflate(inflater, container, false)
            val viewModelFactory =
                MyServicesViewModelFactory(
                    requireActivity().application,
                    MyServicesRepository()
                )
            viewModel = ViewModelProvider(this, viewModelFactory)[MyServicesViewModel::class.java]
            binding.viewModel = viewModel
            binding.lifecycleOwner = requireActivity()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addServiceButton.setOnClickListener {
            findNavController().navigate(
                MyServicesFragmentDirections.actionMyServicesFragmentToAddServiceFragment()
            )
        }

        adapter = MyServicesAdapter(OnMyServiceItemClickListener {
            val editServiceDialog =
                EditServiceDialogFragment.newInstance(it)

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val prevFragment =
                requireActivity().supportFragmentManager.findFragmentByTag("editDialog")
            if (prevFragment != null)
                fragmentTransaction.remove(prevFragment)
            fragmentTransaction.addToBackStack(null)
            requireActivity().supportFragmentManager.setFragmentResultListener(
                "isEdited",
                viewLifecycleOwner
            ) { requestKey, bundle ->
                Log.d(TAG, "Req.Key: $requestKey, CatId: ${bundle["edited"] as Boolean}")
                if (requestKey == "isEdited" && bundle["edited"] as Boolean)
                    viewModel.startRequestMyServices(true)

            }
            editServiceDialog.show(fragmentTransaction, "editDialog")
        })

        binding.services.layoutManager =
            LinearLayoutManager(requireContext())
        binding.services.adapter = adapter

        val callback: ItemTouchHelper.Callback = SwipeHelperCallback(this)
        val mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper.attachToRecyclerView(binding.services)

        viewModel.startRequestMyServices.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getMyServices()
            }
        }

        viewModel.servicesResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Success<*>) {
                val data = it.data as DynamicMyServiceData
                adapter.submitList(data.myServices)
            } else if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            }
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.startRequestMyServices(true)
        }


        viewModel.startRemoveService.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.removeService()
            }
        }

        viewModel.removeServiceResponse.observe(viewLifecycleOwner) {
            viewModel.startRemoveService(false, 0, -1)
            if (it is Resource.Success<*>) {
                val adapterList = adapter.currentList.toMutableList()
                adapterList.removeAt(viewModel.position)
                adapter.submitList(adapterList)
                CustomDialog.showSuccessDialog(
                    context = requireContext(),
                    successMessage = getString(R.string.delete_service_success_message),
                    navController = if (adapterList.isEmpty()) findNavController() else null
                )
            } else if (it is Resource.Failed) {
                adapter.notifyItemChanged(viewModel.position)
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = it.message
                )
            }
        }

        return binding.root
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) = false

    override fun onItemDismiss(position: Int) {
        val adapterList = adapter.currentList.toMutableList()
//        Log.d(TAG, "ID: ${adapterList[position].serviceId}")
//        viewModel.startRemoveService(true, adapterList[position].serviceId, position)
        showConfirmDialog(serviceId = adapterList[position].serviceId, position = position)

    }

    private fun showConfirmDialog(
        dialogBehavior: DialogBehavior = ModalDialog,
        serviceId: Int,
        position: Int
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
            String.format(getString(R.string.confirm_dialog_message), getString(R.string.service))

        dialogBinding.animation.setAnimation("question_animation.json")
        dialogBinding.animation.playAnimation()
        dialogBinding.animation.repeatCount = LottieDrawable.INFINITE

        dialogBinding.yesButton.setOnClickListener {
            dialogBinding.animation.cancelAnimation()
            dialog.dismiss()
            viewModel.startRemoveService(true, serviceId, position)
        }

        dialogBinding.noButton.setOnClickListener {
            adapter.notifyItemChanged(position)
            dialogBinding.animation.cancelAnimation()
            dialog.dismiss()
        }


    }
}