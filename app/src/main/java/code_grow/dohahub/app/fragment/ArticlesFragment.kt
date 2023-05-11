package code_grow.dohahub.app.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import code_grow.dohahub.app.R
import code_grow.dohahub.app.activity.AuthActivity
import code_grow.dohahub.app.adapter.ArticlesAdapter
import code_grow.dohahub.app.adapter.OnArticleItemClickListener
import code_grow.dohahub.app.databinding.FragmentArticlesBinding
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.model.Article
import code_grow.dohahub.app.repository.ArticleRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.view_model.ArticlesViewModel
import code_grow.dohahub.app.view_model.ArticlesViewModelFactory
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class ArticlesFragment : Fragment() {
    private lateinit var binding: FragmentArticlesBinding
    private lateinit var viewModel: ArticlesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized) {
            binding
        } else {
            binding = FragmentArticlesBinding.inflate(inflater, container, false)
            val viewModelFactory = ArticlesViewModelFactory(
                requireActivity().application,
                ArticleRepository()
            )
            viewModel = ViewModelProvider(this, viewModelFactory)[ArticlesViewModel::class.java]
            binding.viewModel = viewModel
            binding.lifecycleOwner = requireActivity()
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        val addArticleLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    findNavController().navigate(
                        ArticlesFragmentDirections.actionArticlesFragmentToAddArticleFragment()
                    )
                }
            }
        binding.addArticleButton.setOnClickListener {
            if (UserInfo.isSigned)
                findNavController().navigate(
                    ArticlesFragmentDirections.actionArticlesFragmentToAddArticleFragment()
                )
            else
                addArticleLauncher.launch(Intent(requireContext(), AuthActivity::class.java))
        }

        val adapter = ArticlesAdapter(OnArticleItemClickListener {
            if (findNavController().currentDestination?.id == R.id.articlesFragment)
                findNavController().navigate(
                    ArticlesFragmentDirections.actionArticlesFragmentToArticleDetailsFragment(
                        it
                    )
                )
        })

        binding.articles.adapter = adapter
        binding.articles.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        viewModel.startRequestArticles.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getArticles()
            }
        }

        viewModel.articlesResponse.observe(viewLifecycleOwner) {
            viewModel.startRequestArticles(false)
            if (it is Resource.Success<*>) {
                adapter.submitList(it.data as MutableList<Article>)
            } else if (it is Resource.Failed) {
                binding.errorView.error = it.message
                binding.errorView.cancelAnimation = false
            }

        }

        binding.errorView.root.setOnClickListener {
            binding.errorView.root.visibility = View.GONE
            binding.errorView.cancelAnimation = true
            viewModel.startRequestArticles(true)
        }

        binding.searchEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.startSearching(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        KeyboardVisibilityEvent.setEventListener(
            requireActivity()
        ) {
            if (it) {
                binding.addArticleButton.visibility = View.GONE
            } else {
                binding.addArticleButton.visibility = View.VISIBLE
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}