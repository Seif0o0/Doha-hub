package code_grow.dohahub.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import code_grow.dohahub.app.databinding.FragmentArticleDetailsBinding

class ArticleDetailsFragment : Fragment() {
    private lateinit var binding: FragmentArticleDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentArticleDetailsBinding.inflate(inflater, container, false)
        val article = ArticleDetailsFragmentArgs.fromBundle(requireArguments()).article
        binding.article = article
        binding.lifecycleOwner = requireActivity()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.commentsButton.setOnClickListener{
            findNavController().navigate(
                ArticleDetailsFragmentDirections.actionArticleDetailsFragmentToCommentsFragment(article.articleId,article.comments.toTypedArray())
            )
        }
        return binding.root
    }
}