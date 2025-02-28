package com.nta.newsappwithmvvm.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.nta.newsappwithmvvm.databinding.FragmentArticleBinding
import com.nta.newsappwithmvvm.ui.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleFragment : Fragment() {

    private lateinit var binding: FragmentArticleBinding
    private val newsViewModel : NewsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentArticleBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("HLKJHLKJL", "onViewCreated: ${newsViewModel.articleUrl}")
        binding.webview.settings.setJavaScriptEnabled(true)
        binding.webview.apply {
            webViewClient = WebViewClient()
            loadUrl(newsViewModel.articleUrl)
        }

        binding.fab.setOnClickListener {
            newsViewModel.saveArticle(newsViewModel.savedArticle)
            Snackbar.make(view, "Article saved successfully", Snackbar.LENGTH_SHORT).show()
        }

    }
    
}