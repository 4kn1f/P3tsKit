package com.application.p3tskit.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.p3tskit.databinding.FragmentHistoryBinding
import com.application.p3tskit.ui.HistoryAdapter
import com.application.p3tskit.ui.ViewModelFactory
import android.util.Log

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private val historyViewModel by viewModels<HistoryViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HistoryAdapter(listOf())
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

        historyViewModel.historyResult.observe(viewLifecycleOwner) { historyList ->
            Log.d("HistoryFragment", "History data received: $historyList")
            if (historyList.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "No history available", Toast.LENGTH_SHORT).show()
            } else {
                adapter.updateData(historyList)
            }
        }

        historyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        historyViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        historyViewModel.getHistory()
    }
}
