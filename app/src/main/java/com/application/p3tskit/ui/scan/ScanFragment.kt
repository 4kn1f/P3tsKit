package com.application.p3tskit.ui.scan

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.application.p3tskit.R
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import com.application.p3tskit.data.pref.AuthPreferences
import com.application.p3tskit.data.remote.repository.DiagnoseRepository

class ScanFragment : Fragment() {

    private lateinit var btnGallery: Button
    private lateinit var btnCamera: Button
    private lateinit var btnScan: Button
    private lateinit var imageView: ImageView
    private var capturedImageUri: Uri? = null

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var viewModel: DetailScanViewModel
    private lateinit var authPreferences: AuthPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authPreferences = AuthPreferences.getInstance(requireActivity().applicationContext)

        val diagnoseRepository = DiagnoseRepository.getInstance(requireContext())

        val factory = DetailScanViewModelFactory(diagnoseRepository, authPreferences, requireActivity().application)

        viewModel = ViewModelProvider(this, factory).get(DetailScanViewModel::class.java)

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) openCamera() else showToast("Camera permission required.")
        }

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                capturedImageUri = it
                displayCapturedImage(it)
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) capturedImageUri?.let { displayCapturedImage(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scan, container, false)

        btnGallery = view.findViewById(R.id.buttonGallery)
        btnCamera = view.findViewById(R.id.buttonCamera)
        btnScan = view.findViewById(R.id.buttonScan)
        imageView = view.findViewById(R.id.scanImage)

        btnCamera.setOnClickListener { checkCameraPermission() }
        btnGallery.setOnClickListener { pickImageFromGallery() }
        btnScan.setOnClickListener { uploadImage() }

        return view
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> openCamera()
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> showToast("Camera permission required.")
            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val contentResolver = requireContext().contentResolver
        val photoUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "photo_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }
        )

        if (photoUri != null) {
            capturedImageUri = photoUri
            cameraLauncher.launch(photoUri)
        } else {
            showToast("Failed to create image file. Please check permissions.")
        }
    }

    private fun pickImageFromGallery() = pickImageLauncher.launch("image/*")

    private fun displayCapturedImage(uri: Uri) {
        Glide.with(this).load(uri).into(imageView)
    }

    private fun uploadImage() {
        capturedImageUri?.let { uri ->
            showToast("Uploading image...")

            lifecycleScope.launch {
                try {
                    viewModel.analyzeImage(uri)

                    viewModel.scanResult.observe(viewLifecycleOwner) { scanResponse ->
                        scanResponse?.let { response ->
                            Log.d("ScanResult", "Received scan result: $response")

                            val predictedClass = response.predictedClass

                            if (predictedClass.isNullOrEmpty()) {
                                showToast("No disease detected. Please try again.")
                            } else {
                                showToast("Disease detected: $predictedClass")

                                val diseaseInfo = response.diseaseInfo
                                val bundle = Bundle().apply {
                                    putParcelable("scan_result", response)
                                    putParcelable("image_uri", uri)
                                }

                                diseaseInfo?.let {
                                    bundle.putParcelable("disease_info", it)
                                }

                                findNavController().navigate(
                                    R.id.action_scanFragment_to_detailScanFragment,
                                    bundle
                                )
                            }
                        }
                    }

                    viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                        errorMessage?.let {
                            showToast(it)
                        }
                    }

                } catch (e: Exception) {
                    showToast("Error: ${e.message}")
                }
            }
        } ?: showToast("No image selected.")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
