package com.application.p3tskit.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.application.p3tskit.R
import com.application.p3tskit.databinding.FragmentHomeBinding
import com.application.p3tskit.ui.ViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        homeViewModel.getSession().observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                binding.tvUser.text = it.email
            }
        })

        return binding.root
    }
}