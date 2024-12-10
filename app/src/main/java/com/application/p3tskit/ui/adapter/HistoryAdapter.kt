package com.application.p3tskit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.application.p3tskit.data.remote.response.DiseasesInfo
import com.application.p3tskit.data.remote.response.HistoryItem
import com.application.p3tskit.databinding.ItemHistoryBinding

class HistoryAdapter(private var itemHistory: List<HistoryItem>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = itemHistory[position]
        holder.bind(history)
    }

    override fun getItemCount(): Int = itemHistory.size

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(historyItem: HistoryItem) {
            // Safely cast `diseaseInfo` and handle null values gracefully
            val diseasesInfo = historyItem.diseaseInfo as? DiseasesInfo

            // Set default fallback values for TextViews in case of null data
            binding.tvPredictedClass.text = historyItem.predictedClass ?: "N/A"
            binding.tvResultDescription.text = diseasesInfo?.description ?: "No description available"
            binding.symptoms.text = diseasesInfo?.symptoms?.joinToString(", ") ?: "No symptoms listed"
            binding.tvTreatment.text = diseasesInfo?.treatment?.joinToString(", ") ?: "No treatment information"
            binding.tvNote.text = diseasesInfo?.note ?: "No additional notes"
            binding.tvSource.text = diseasesInfo?.source?.joinToString(", ") ?: "No source provided"
        }
    }

    fun updateData(newData: List<HistoryItem>) {
        itemHistory = newData
        notifyDataSetChanged()
    }
}
