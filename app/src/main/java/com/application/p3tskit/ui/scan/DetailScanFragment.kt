package com.application.p3tskit.ui.scan

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.application.p3tskit.R
import com.application.p3tskit.data.pref.AuthPreferences
import com.application.p3tskit.data.remote.repository.DiagnoseRepository
import com.application.p3tskit.data.remote.response.ModelScanResponse
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.application.p3tskit.databinding.FragmentDetailScanPageBinding

class DetailScanFragment : Fragment() {

    private lateinit var binding: FragmentDetailScanPageBinding
    private lateinit var viewModel: DetailScanViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDetailScanPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = DetailScanViewModelFactory(
            DiagnoseRepository.getInstance(requireContext()),
            AuthPreferences.getInstance(requireContext()),
            requireActivity().application
        )
        viewModel = ViewModelProvider(this, factory)[DetailScanViewModel::class.java]

        binding.progressBar.visibility = View.VISIBLE

        viewModel.scanResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.visibility = View.GONE
            result?.let {
                if (it.predictedClass != null) {
                    updateUIWithScanResult(it)
                } else {
                    Log.e("DetailScanFragment", "No disease detected.")
                }
            } ?: run {
                Log.e("DetailScanFragment", "Scan result is null")
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            binding.progressBar.visibility = View.GONE
            if (!error.isNullOrEmpty()) {
                Log.e("DetailScanFragment", "Error: $error")
            }
        }

        val scanResultJson = arguments?.getString("scan_result_json")
        val imageUri: Uri? = arguments?.getParcelable("image_uri")

        if (scanResultJson != null) {
            val scanResult = Gson().fromJson(scanResultJson, ModelScanResponse::class.java)
            updateUIWithScanResult(scanResult)
            binding.progressBar.visibility = View.GONE
        } else if (imageUri != null) {
            viewModel.analyzeImage(imageUri)
        }

        imageUri?.let { uri ->
            Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.resultImage)
        }
    }

    private fun updateUIWithScanResult(result: ModelScanResponse) {
        Log.d("DetailScanFragment", "Received result: $result")

        binding.tvPredictedClass.text = getString(R.string.diagnosis, result.predictedClass ?: "Not available")

        result.diseaseInfo?.let { diseaseInfo ->
            binding.tvResultDescription.text = getString(R.string.descriptions, diseaseInfo.description ?: "Not Available")

            val symptoms = diseaseInfo.symptoms.takeIf { it.isNotEmpty() }?.joinToString("\n") ?: "Not Available"
            binding.symptoms.text = symptoms

            val treatment = diseaseInfo.treatment.takeIf { it.isNotEmpty() }?.joinToString("\n") ?: "Not Available"
            binding.tvTreatment.text = treatment

            binding.tvNote.text = diseaseInfo.note ?: "Not Available"

            val source = diseaseInfo.source.takeIf { it.isNotEmpty() }?.joinToString("\n") ?: "Not Available"
            binding.tvSource.text = source

        } ?: run {
            Log.e("DetailScanFragment", "Disease info is null, using default values")

            binding.tvResultDescription.text = "Description: ${getString(R.string.description_not_available)}"
            binding.symptoms.text = "Symptoms: ${getString(R.string.symptoms_not_available)}"
            binding.tvTreatment.text = "Treatment: ${getString(R.string.treatment_not_available)}"
            binding.tvNote.text = "Note: ${getString(R.string.note_not_available)}"
            binding.tvSource.text = "Source: ${getString(R.string.source_not_available)}"
        }
    }
}
