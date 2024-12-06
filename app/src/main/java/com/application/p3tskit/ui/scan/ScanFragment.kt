package com.application.p3tskit.ui.scan

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.application.p3tskit.R
import com.application.p3tskit.data.pref.AuthPreferences
import com.application.p3tskit.data.pref.dataStore
import com.application.p3tskit.data.remote.repository.DiagnoseRepository
import com.application.p3tskit.data.remote.response.ModelScanResponse
import com.application.p3tskit.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch

class ScanFragment : Fragment() {

    private lateinit var diagnoseRepository: DiagnoseRepository
    private var selectedImageUri: Uri? = null
    private lateinit var btnUpload: Button
    private lateinit var btnScan: Button

    private val pickImageResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->

        uri?.let {
            selectedImageUri = it
            showToast("Image selected!")
        } ?: showToast("No image selected.")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_scan, container, false)

        btnUpload = view.findViewById(R.id.buttonUpload)
        btnScan = view.findViewById(R.id.buttonScan)

        val apiService = ApiConfig.getApiService()
        val authPreferences = AuthPreferences.getInstance(requireContext().dataStore)
        diagnoseRepository = DiagnoseRepository(apiService, authPreferences)

        setupButtonListeners()

        return view
    }

    private fun setupButtonListeners() {
        btnUpload.setOnClickListener {
            // Open image picker
            pickImageFromGallery()
        }

        btnScan.setOnClickListener {
            selectedImageUri?.let {
                performDiagnosis(it)
            } ?: showToast("Please select an image first!")
        }
    }

    private fun performDiagnosis(imageUri: Uri) {
        lifecycleScope.launch {
            try {
                val response = diagnoseRepository.diagnoseImage(imageUri)
                if (response?.isSuccessful == true) {
                    val result = response.body()
                    navigateToDetail(result)
                } else {
                    showToast("Diagnosis failed: ${response?.message() ?: "Unknown error"}")
                }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }

    private fun navigateToDetail(result: ModelScanResponse?) {
        val bundle = Bundle().apply {
            putString("predictedClass", result?.predictedClass)
            putString("description", result?.diseaseInfo?.description)
            putString("symptoms", result?.diseaseInfo?.symptoms)
            putStringArrayList("treatment", ArrayList(result?.diseaseInfo?.treatment ?: emptyList()))
            putStringArrayList("causes", ArrayList(result?.diseaseInfo?.causes ?: emptyList()))
        }
        findNavController().navigate(R.id.action_scanFragment_to_detailScanFragment, bundle)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun pickImageFromGallery() {

        pickImageResult.launch("image/*")
    }
}
