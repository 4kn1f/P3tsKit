package com.application.p3tskit.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.p3tskit.databinding.ActivityNewsBinding

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private lateinit var newsAdapter: NewsAdapter
    private val newsViewModel: NewsViewModel by viewModels<NewsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newsCount = intent.getIntExtra("EXTRA_NEWS_COUNT", 30)
        setupRecyclerView()
        observeViewModel()
        newsViewModel.getNews(newsCount)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeViewModel() {
        newsViewModel.newsResult.observe(this) { articles ->
            if (articles.isNotEmpty()) {
                newsAdapter = NewsAdapter(articles)
                binding.rvNews.adapter = newsAdapter
            } else {
                Toast.makeText(this, "No news available", Toast.LENGTH_SHORT).show()
            }
        }

        newsViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        newsViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvNews.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter(emptyList())
        binding.rvNews.adapter = newsAdapter
    }
}
