package com.application.p3tskit.ui

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

            val diseasesInfo = historyItem.diseaseInfo as? DiseasesInfo

            binding.tvPredictedClass.text = historyItem.predictedClass
            binding.tvResultDescription.text = diseasesInfo?.description
            binding.symptoms.text = diseasesInfo?.symptoms?.joinToString(", ")
            binding.tvTreatment.text = diseasesInfo?.treatment?.joinToString(", ")
            binding.tvNote.text = diseasesInfo?.note
            binding.tvSource.text = diseasesInfo?.source?.joinToString(", ")
        }
    }

    fun updateData(newData: List<HistoryItem>) {
        itemHistory = newData
        notifyDataSetChanged()
    }
}
