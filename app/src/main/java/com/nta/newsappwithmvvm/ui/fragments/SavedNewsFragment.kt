package com.nta.newsappwithmvvm.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nta.newsappwithmvvm.R
import com.nta.newsappwithmvvm.adapter.NewsAdapter
import com.nta.newsappwithmvvm.databinding.FragmentSavedNewsBinding
import com.nta.newsappwithmvvm.modals.Article
import com.nta.newsappwithmvvm.ui.NewsViewModel

class SavedNewsFragment : Fragment(),NewsAdapter.OnItemClickListener {

    private lateinit var binding: FragmentSavedNewsBinding
    private val newsViewModel: NewsViewModel by activityViewModels()
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSavedNewsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                newsViewModel.deleteArticle(article)
                Snackbar.make(view,"Successfully deleted articles.",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        newsViewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.recBreakingNews)
        }

        newsViewModel.getSaveNews().observe(viewLifecycleOwner, Observer { article ->
            newsAdapter.differ.submitList(article)
        })
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter(this)
        binding.recBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }
    }

    override fun onItemClick(article: Article) {
        newsViewModel.articleUrl = article.url.toString()
        findNavController().navigate(
            R.id.action_savedNewsFragment_to_articleFragment
        )
    }

}