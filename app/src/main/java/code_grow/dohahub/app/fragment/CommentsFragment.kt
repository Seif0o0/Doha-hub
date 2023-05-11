package code_grow.dohahub.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code_grow.dohahub.app.R
import code_grow.dohahub.app.adapter.CommentsAdapter
import code_grow.dohahub.app.databinding.FragmentCommentsBinding
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.Article
import code_grow.dohahub.app.model.Comment
import code_grow.dohahub.app.repository.ArticleRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.view_model.CommentsViewModel
import code_grow.dohahub.app.view_model.CommentsViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class CommentsFragment : Fragment() {
    private lateinit var binding: FragmentCommentsBinding
    private lateinit var viewModel: CommentsViewModel
    private lateinit var adapter: CommentsAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentsBinding.inflate(inflater, container, false)

        val args = CommentsFragmentArgs.fromBundle(requireArguments())
        val articleId = args.articleId
        val viewModelFactory = CommentsViewModelFactory(
            requireActivity().application,
            articleId,
            ArticleRepository()
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[CommentsViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        if (!UserInfo.isSigned) {
            binding.commentGroup.visibility = View.GONE
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.sendIcon.setOnClickListener {
            viewModel.addCommentBtnClicked()
        }

        adapter = CommentsAdapter()
        binding.comments.adapter = adapter
        binding.comments.layoutManager = LinearLayoutManager(requireContext())

        viewModel.startRequestComments.observe(viewLifecycleOwner) {
            if (it) {
                if (viewModel.callApi)
                    viewModel.getComments()
            }
        }

        viewModel.commentsResponse.observe(viewLifecycleOwner) {
            viewModel.startRequestComments(value = false, callApi = false)
            if (it is Resource.Success<*>) {
                adapter.submitList(it.data as MutableList<Comment>)
            } else if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            }
        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.startRequestComments(value = true, callApi = true)
        }

        viewModel.startSendComment.observe(viewLifecycleOwner) {
            if (it)
                viewModel.sendComment()
        }

        viewModel.sendCommentResponse.observe(viewLifecycleOwner) {
            viewModel.startSendComment(false)
            if (it is Resource.Failed) {
                val snackBar = Snackbar.make(
                    binding.root, getString(R.string.send_message_failed),
                    Snackbar.LENGTH_SHORT
                )
                    .setAction(getString(R.string.resend_message)) {
                        viewModel.startSendComment(true)
                    }

                val snackBarView = snackBar.view
                snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orders_main_background
                    )
                )
                val snackBarTextView =
                    snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
                snackBarTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.error_dialog_message_color
                    )
                )
                snackBarTextView.textSize = 15f
                snackBarTextView.isAllCaps = false
                val snackBarAction =
                    snackBarView.findViewById(com.google.android.material.R.id.snackbar_action) as TextView
                snackBarAction.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.error_dialog_retry_text_background_color
                    )
                )
                snackBarAction.textSize = 15f
                snackBarAction.isAllCaps = false

                snackBar.show()
            } else if (it is Resource.Success<*>) {
//                val comments = adapter.currentList.toMutableList()
                val date = Calendar.getInstance().time
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formatedDate = sdf.format(date)
//                comments.add(
//                    Comment(
//                        if (comments.isEmpty()) 0 else comments[comments.size - 1].commentId + 1,
//                        UserInfo.userId,
//                        viewModel.commentLiveData.value!!,
//                        formatedDate,
//                        UserInfo.username,
//                        UserInfo.profilePicture
//                    )
//                )
//                if (comments.isEmpty()) 0 else comments[comments.size - 1].commentId + 1
                viewModel.addComment(
                    comment = Comment(
                        (100..200).random(),
                        UserInfo.userId,
                        viewModel.commentLiveData.value!!,
                        formatedDate,
                        UserInfo.username,
                        UserInfo.profilePicture
                    )
                )
                /*if (comments.size == 1) {
                    binding.emptyCommentsText.visibility = View.GONE
                    adapter = CommentsAdapter()
                    binding.comments.adapter = adapter
                    adapter.submitList(comments)
                    binding.comments.layoutManager = LinearLayoutManager(requireContext())
                } else {
                    adapter.notifyItemInserted(comments.size - 1)
                    adapter.notifyItemChanged(comments.size - 2)
                    binding.comments.smoothScrollToPosition(comments.size - 1)
                }*/
            }
        }

        return binding.root
    }
}