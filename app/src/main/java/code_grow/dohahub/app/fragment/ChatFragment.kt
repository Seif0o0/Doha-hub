package code_grow.dohahub.app.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.dohahub.app.R
import code_grow.dohahub.app.adapter.ChatAdapter
import code_grow.dohahub.app.databinding.FragmentChatBinding
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.Article
import code_grow.dohahub.app.model.Message
import code_grow.dohahub.app.repository.ChatRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.view_model.ChatViewModel
import code_grow.dohahub.app.view_model.ChatViewModelFactory
import com.google.android.material.snackbar.Snackbar

private const val TAG = "ChatFragment"

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var viewModel: ChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        val receiverId = ChatFragmentArgs.fromBundle(requireArguments()).receiverId
        val viewModelFactory = ChatViewModelFactory(
            requireActivity().application,
            UserInfo.userId,
            receiverId,
            ChatRepository()
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[ChatViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.sendIcon.setOnClickListener {
            viewModel.sendMessageBtnClicked()
        }

        val adapter = ChatAdapter()
        binding.messages.adapter = adapter
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        layoutManager.stackFromEnd = true
        binding.messages.layoutManager = layoutManager


        viewModel.startRequestMessages.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getMessages()
            }
        }

        viewModel.messagesResponse.observe(viewLifecycleOwner) {
            viewModel.startRequestMessages(false)
            if (it is Resource.Success<*>) {
                adapter.submitList(it.data as MutableList<Message>)
                binding.messages.scrollToPosition(0)
            } else if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            }
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.startRequestMessages(true)
        }

        viewModel.startSendMessage.observe(viewLifecycleOwner) {
            if (it)
                viewModel.sendMessage()
        }

        viewModel.sendMessageResponse.observe(viewLifecycleOwner){
            viewModel.startSendMessage(false)
            if (it is Resource.Failed){
                val snackBar = Snackbar.make(binding.root,getString(R.string.send_message_failed),Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.resend_message)){
                        viewModel.startSendMessage(true)
                    }

                val snackBarView = snackBar.view
                snackBarView.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.orders_main_background))
                val snackBarTextView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
                snackBarTextView.setTextColor(ContextCompat.getColor(requireContext(),R.color.error_dialog_message_color))
                snackBarTextView.textSize = 15f
                snackBarTextView.isAllCaps = false
                val snackBarAction = snackBarView.findViewById(com.google.android.material.R.id.snackbar_action) as TextView
                snackBarAction.setTextColor(ContextCompat.getColor(requireContext(),R.color.error_dialog_retry_text_background_color))
                snackBarAction.textSize = 15f
                snackBarAction.isAllCaps = false

                snackBar.show()
            }
        }

        return binding.root
    }
}