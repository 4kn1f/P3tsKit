package com.application.p3tskit.ui.scan

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.application.p3tskit.R
import com.application.p3tskit.data.pref.AuthPreferences
import com.application.p3tskit.data.remote.repository.DiagnoseRepository
import com.application.p3tskit.data.remote.response.ModelScanResponse
import com.bumptech.glide.Glide
import com.google.gson.Gson

class DetailScanFragment : Fragment() {

    private lateinit var predictedClassTextView: TextView
    private lateinit var diseaseInfoTextView: TextView
    private lateinit var symptomsTextView: TextView
    private lateinit var treatmentTextView: TextView
    private lateinit var imageView: ImageView
    private lateinit var sourceTextView: TextView
    private lateinit var noteTextView: TextView
    private lateinit var viewModel: DetailScanViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_scan_page, container, false)

        // Initialize views
        predictedClassTextView = view.findViewById(R.id.tv_predicted_class)
        diseaseInfoTextView = view.findViewById(R.id.tv_result_description)
        symptomsTextView = view.findViewById(R.id.symptoms)
        treatmentTextView = view.findViewById(R.id.tv_treatment)
        imageView = view.findViewById(R.id.result_image)
        sourceTextView = view.findViewById(R.id.tv_source)
        noteTextView = view.findViewById(R.id.tv_note)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel with factory
        val factory = DetailScanViewModelFactory(
            DiagnoseRepository.getInstance(requireContext()),
            AuthPreferences.getInstance(requireContext()),
            requireActivity().application
        )
        viewModel = ViewModelProvider(this, factory)[DetailScanViewModel::class.java]

        // Observe scan result LiveData
        viewModel.scanResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.predictedClass != null) {
                    updateUIWithScanResult(it)
                } else {
                    Log.e("DetailScanFragment", "No disease detected.")
                    // Handle the case where no disease was detected
                    // You can show a message or navigate elsewhere
                }
            } ?: run {
                Log.e("DetailScanFragment", "Scan result is null")
            }
        }

        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Log.e("DetailScanFragment", "Error: $error")
            }
        }

        // Check for arguments and handle API request or data binding
        val scanResultJson = arguments?.getString("scan_result_json")
        val imageUri: Uri? = arguments?.getParcelable("image_uri")

        if (scanResultJson != null) {
            val scanResult = Gson().fromJson(scanResultJson, ModelScanResponse::class.java)
            updateUIWithScanResult(scanResult)
        } else if (imageUri != null) {
            viewModel.analyzeImage(imageUri)
        }

        // Load image using Glide
        imageUri?.let { uri ->
            Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(imageView)
        }
    }

    private fun updateUIWithScanResult(result: ModelScanResponse) {
        predictedClassTextView.text = "Diagnosis: ${result.predictedClass ?: "Not available"}"
        diseaseInfoTextView.text = result.diseaseInfo?.description ?: "Description not available"

        // Fix symptoms and treatment: Convert List<String> to String with line breaks
        val symptoms = result.diseaseInfo?.symptoms?.let {
            if (it.isNotEmpty()) it.joinToString("\n") else "No symptoms available"
        } ?: "No symptoms available"
        symptomsTextView.text = symptoms

        val treatment = result.diseaseInfo?.treatment?.let {
            if (it.isNotEmpty()) it.joinToString("\n") else "No treatment available"
        } ?: "No treatment available"
        treatmentTextView.text = treatment

        noteTextView.text = result.diseaseInfo?.note ?: "No additional notes"

        // Convert List<String> to String with line breaks for sources
        sourceTextView.text = result.diseaseInfo?.source?.let {
            if (it.isNotEmpty()) it.joinToString("\n") else "No sources available"
        } ?: "No sources available"
    }
}
