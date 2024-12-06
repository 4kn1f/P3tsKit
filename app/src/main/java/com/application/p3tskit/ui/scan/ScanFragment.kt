package com.application.p3tskit.ui.scan

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.application.p3tskit.R
import com.application.p3tskit.data.pref.AuthPreferences
import com.application.p3tskit.data.pref.dataStore
import com.application.p3tskit.data.remote.repository.DiagnoseRepository
import com.application.p3tskit.data.remote.response.ModelScanResponse
import com.application.p3tskit.data.remote.retrofit.ApiConfig
import com.application.p3tskit.databinding.FragmentScanBinding
import kotlinx.coroutines.launch

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    private lateinit var diagnoseRepository: DiagnoseRepository
    private var selectedImageUri: Uri? = null // Holds the selected image URI

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)

        // Initialize DiagnoseRepository with ApiConfig and AuthPreferences
        val apiService = ApiConfig.getApiService() // Use ApiConfig to get the ApiService
        val authPreferences = AuthPreferences.getInstance(requireContext().dataStore)
        diagnoseRepository = DiagnoseRepository(apiService, authPreferences)

        setupButtonListeners()

        return binding.root
    }

    private fun setupButtonListeners() {
        binding.buttonUpload.setOnClickListener {
            // Open image picker (implement as needed)
            pickImageFromGallery()
        }

        binding.buttonScan.setOnClickListener {
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
