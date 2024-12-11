package com.application.p3tskit.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.p3tskit.R
import com.application.p3tskit.databinding.FragmentHomeBinding
import com.application.p3tskit.ui.NewsActivity
import com.application.p3tskit.ui.NewsAdapter
import com.application.p3tskit.ui.ViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.cardCat.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_history)
        }

        binding.tvSeeMoreNews.setOnClickListener{
            val intent = Intent(requireContext(), NewsActivity::class.java)
            intent.putExtra("EXTRA_NEWS_COUNT", 30)
            startActivity(intent)
        }

        homeViewModel.getSession().observe(viewLifecycleOwner) { user ->
            user?.let {
                val email = it.email
                val shortEmail = email.split("@").getOrNull(0)
                binding.tvUser.text = shortEmail
            }
        }

        setupRecyclerView()
        observeViewModel()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvNews.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        homeViewModel.newsList.observe(viewLifecycleOwner) { newsList ->
            if (newsList.isNotEmpty()) {
                binding.rvNews.adapter = NewsAdapter(newsList)
            } else {
                Toast.makeText(requireContext(), "No news available", Toast.LENGTH_SHORT).show()
            }
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
