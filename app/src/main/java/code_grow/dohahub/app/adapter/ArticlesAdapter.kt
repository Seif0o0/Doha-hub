package code_grow.dohahub.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.dohahub.app.databinding.ArticleItemBinding
import code_grow.dohahub.app.model.Article

class ArticlesAdapter(private val clickListener: OnArticleItemClickListener) :
    ListAdapter<Article, ArticlesAdapter.ViewHolder>(ArticlesDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            getItem(position),
            clickListener
        )
    }

    class ViewHolder private constructor(private val binding: ArticleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            article: Article,
            clickListener: OnArticleItemClickListener
        ) {
            binding.article = article
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ArticleItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

open class OnArticleItemClickListener(val clickListener: (article: Article) -> Unit) {
    fun onArticleClicked(article: Article) = clickListener(article)
}

class ArticlesDiffUtil : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article) =
        oldItem.articleId == newItem.articleId


    override fun areContentsTheSame(oldItem: Article, newItem: Article) = oldItem == newItem
}