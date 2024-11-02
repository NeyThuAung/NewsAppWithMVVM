package com.nta.newsappwithmvvm.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nta.newsappmvvm.util.Resource
import com.nta.newsappwithmvvm.adapter.NewsAdapter
import com.nta.newsappwithmvvm.databinding.FragmentBreakingNewsBinding
import com.nta.newsappwithmvvm.ui.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BreakingNewsFragment : Fragment() {

    private lateinit var binding: FragmentBreakingNewsBinding
    private val newsViewModel : NewsViewModel by activityViewModels()
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBreakingNewsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        newsViewModel.getBreakingNews("us")

        newsViewModel.breakingNews.observe(viewLifecycleOwner, Observer {response ->
            when(response){
                is Resource.Error -> {
                    response.message?.let { message ->
                        Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    Log.d("LKLJLKJLK", "onViewCreated: ${response.data?.articles}")
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }

                }
            }
        })
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.recBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }
    }

}