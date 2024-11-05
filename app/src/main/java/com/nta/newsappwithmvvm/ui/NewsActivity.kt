package com.nta.newsappwithmvvm.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nta.newsappwithmvvm.R
import com.nta.newsappwithmvvm.databinding.ActivityNewsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController : NavController
    private val newsViewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNewsBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)

        //Initialize NavController using NavHostFragment
        setSupportActionBar(binding.toolbar)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        // Set up BottomNavigationView with NavController
        val navView: BottomNavigationView = binding.bottomNavigationView
        navView.setupWithNavController(navController)
        navView.itemIconTintList = null

        // Configure AppBar with NavController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setOnItemSelectedListener { item->
            when(item.itemId){
                R.id.breakingNewsFragment -> {
                    navController.navigate(R.id.breakingNewsFragment)
                }
                R.id.savedNewsFragment -> {
                    navController.navigate(R.id.savedNewsFragment)
                }
                R.id.searchNewsFragment -> {
                    navController.navigate(R.id.searchNewsFragment)
                }

            }
            true
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}