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
import code_grow.dohahub.app.adapter.ArticlesAdapter
import code_grow.dohahub.app.adapter.OnArticleItemClickListener
import code_grow.dohahub.app.databinding.FragmentMyArticlesBinding
import code_grow.dohahub.app.model.Article
import code_grow.dohahub.app.repository.ArticleRepository
import code_grow.dohahub.app.retrofit.Resource
import code_grow.dohahub.app.view_model.MyArticlesViewModel
import code_grow.dohahub.app.view_model.MyArticlesViewModelFactory
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

private const val TAG = "MyArticlesFragment"

class MyArticlesFragment : Fragment() {
    private lateinit var binding: FragmentMyArticlesBinding
    private lateinit var viewModel: MyArticlesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized) {
            binding
        } else {
            binding = FragmentMyArticlesBinding.inflate(inflater, container, false)
            val viewModelFactory = MyArticlesViewModelFactory(
                requireActivity().application,
                ArticleRepository()
            )
            viewModel = ViewModelProvider(this, viewModelFactory)[MyArticlesViewModel::class.java]
            binding.viewModel = viewModel
            binding.lifecycleOwner = requireActivity()

        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

//KeyboardVisibilityEvent.setEventListener(
//        getActivity(),
//        getLifecycleOwner(),
//        new KeyboardVisibilityEventListener() {
//            @Override
//            public void onVisibilityChanged(boolean isOpen) {
//                // some code depending on keyboard visiblity status
//            }
//        });


        binding.addArticleButton.setOnClickListener {
            addFragmentListener("adding")
            findNavController().navigate(
                MyArticlesFragmentDirections.actionMyArticlesFragmentToAddArticleFragment()
            )
        }

        val adapter = ArticlesAdapter(OnArticleItemClickListener {
            if (findNavController().currentDestination?.id == R.id.myArticlesFragment) {
                addFragmentListener("editing")
                findNavController().navigate(
                    MyArticlesFragmentDirections.actionMyArticlesFragmentToEditArticleFragment(
                        it
                    )
                )
            }

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
        return binding.root
    }

    private fun addFragmentListener(requestKeyValue: String) {
        setFragmentResultListener(
            requestKeyValue
        ) { requestKey, bundle ->
            Log.d(TAG, "Req.Key: $requestKey, CatId: ${bundle["refresh"] as Boolean}")
            if (requestKey == "editing" || requestKey == "adding") {
                if (bundle["refresh"] as Boolean) {
                    viewModel.startRequestArticles(true)
                }
            }

        }
    }
}