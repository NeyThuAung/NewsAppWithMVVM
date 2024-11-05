package com.nta.newsappwithmvvm.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nta.newsappmvvm.util.Resource
import com.nta.newsappwithmvvm.R
import com.nta.newsappwithmvvm.adapter.NewsAdapter
import com.nta.newsappwithmvvm.databinding.FragmentSearchNewsBinding
import com.nta.newsappwithmvvm.modals.Article
import com.nta.newsappwithmvvm.ui.NewsViewModel
import com.nta.newsappwithmvvm.utils.Constants
import com.nta.newsappwithmvvm.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.nta.newsappwithmvvm.utils.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(), NewsAdapter.OnItemClickListener {

    private lateinit var binding: FragmentSearchNewsBinding
    private val newsViewModel: NewsViewModel by activityViewModels()
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchNewsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        var job : Job? = null
        binding.etSearchNews.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()){
                        newsViewModel.searchNews(editable.toString())
                    }
                }
            }


        }

        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer {response ->
            when(response){
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Success -> {
                    hideProgressBar()
                    Log.d("LKLJLKJLK", "onViewCreated: ${response.data?.articles}")
                    response.data?.let { newsResponse ->
                        // add toList() to add automatically next page data when paginate without notify data set change
                        newsAdapter.differ.submitList(newsResponse.articles.toList())

                        //to determine isLast page or not and pagination should paginate or stop
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2 //
                        isLastPage = newsViewModel.searchNewsPage == totalPages

                        if (isLastPage) {
                            binding.recSearchNews.setPadding(0, 0, 0, 0)
                        }
                    }

                }
            }
        })

    }

    private fun showProgressBar() {
        binding.progressLoad.isVisible = true
        isLoading = true
    }

    private fun hideProgressBar() {
        binding.progressLoad.isVisible = false
        isLoading = false
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0

            // if first total count is 10 , request 20 item per page to api, so data have only 10 item
            //so, we don't need to paginate
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                        && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                newsViewModel.searchNews(binding.etSearchNews.toString())
                isScrolling = false
            }
        }
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter(this)
        binding.recSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }

    override fun onItemClick(article: Article) {
        newsViewModel.articleUrl = article.url.toString()
        findNavController().navigate(
            R.id.action_searchNewsFragment_to_articleFragment
        )
    }

}