package code_grow.dohahub.app.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import code_grow.dohahub.app.R
import code_grow.dohahub.app.adapter.MyOrdersAdapter
import code_grow.dohahub.app.adapter.OnMyOrderItemClickListener
import code_grow.dohahub.app.databinding.FragmentOrdersBinding
import code_grow.dohahub.app.model.OrderResponse
import code_grow.dohahub.app.repository.OrdersRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.view_model.OrdersViewModel
import code_grow.dohahub.app.view_model.OrdersViewModelFactory

class MyOrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private lateinit var viewModel: OrdersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized)
            binding
        else {
            binding = FragmentOrdersBinding.inflate(inflater, container, false)
            val viewModelFactory = OrdersViewModelFactory(
                true,
                requireActivity().application,
                OrdersRepository()
            )
            viewModel = ViewModelProvider(this, viewModelFactory)[OrdersViewModel::class.java]
            binding.viewModel = viewModel
            binding.lifecycleOwner = requireActivity()
        }

        binding.header.text = getString(R.string.my_orders_header)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        val adapter = MyOrdersAdapter(OnMyOrderItemClickListener {

            if (findNavController().currentDestination?.id == R.id.myOrdersFragment)
                addFragmentListener("editing")
                findNavController().navigate(
                    MyOrdersFragmentDirections.actionMyOrdersFragmentToOrderDetailsFragment(
                        orderDetails = it
                    )
                )
        })

        binding.orders.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding.orders.adapter = adapter

        viewModel.startRequestOrders.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getOrders()
            }
        }

        viewModel.ordersResponse.observe(viewLifecycleOwner) {
            viewModel.startRequestOrders(false)
            if (it is Resource.Success<*>) {
                adapter.submitList(it.data as MutableList<OrderResponse>)
            } else if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            }
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.startRequestOrders(true)
        }

        return binding.root
    }

    private fun addFragmentListener(requestKeyValue: String) {
        setFragmentResultListener(
            requestKeyValue
        ) { requestKey, bundle ->
            if (requestKey == "editing") {
                if (bundle["refresh"] as Boolean) {
                    viewModel.startRequestOrders(true)
                }
            }

        }
    }
}