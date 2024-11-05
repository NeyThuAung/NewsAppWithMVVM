package com.nta.newsappwithmvvm.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nta.newsappwithmvvm.R
import com.nta.newsappwithmvvm.modals.Article
import com.nta.newsappwithmvvm.databinding.ItemArticlePreviewBinding

class NewsAdapter(
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(article: Article)
    }

    inner class ArticleViewHolder(private val binding: ItemArticlePreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {

            Glide.with(this.itemView)
                .load(article.urlToImage)
                .placeholder(R.color.blue_500)
                .into(binding.ivArticleImage)

            binding.tvTitle.text = article.title
            binding.tvSource.text = article.source?.name
            binding.tvDescription.text = article.description
            binding.tvPublishedAt.text = article.publishedAt

            binding.root.setOnClickListener {
                Log.d("HKHJHK", "bind: ")
//                setOnClickListener {
//                    onItemClickListener?.let {
//                        it(article)
//                    }
//                }
                onItemClickListener.onItemClick(article)
            }
        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

    //calculate the differ and this is list
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
//        return ArticleViewHolder(
//            LayoutInflater.from(parent.context).inflate(
//                R.layout.item_article_preview,
//                parent,
//                false
//            )
//        )
        return ArticleViewHolder(
            ItemArticlePreviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.bind(article)
    }

//     var onItemClickListener : ((Article) -> Unit) ?= null
//
//    fun setOnClickListener(listener : (Article) -> Unit){
//        onItemClickListener = listener
//    }
}