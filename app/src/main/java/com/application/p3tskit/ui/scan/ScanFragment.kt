package com.application.p3tskit.ui.scan

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.application.p3tskit.R
import com.bumptech.glide.Glide

class ScanFragment : Fragment() {

    private lateinit var btnGallery: Button
    private lateinit var btnCamera: Button
    private lateinit var btnScan: Button
    private lateinit var imageView: ImageView
    private var capturedImageUri: Uri? = null

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Permission launcher for camera
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                showToast("Camera permission is required to capture photos.")
            }
        }

        // Image picker launcher
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                capturedImageUri = it
                displayCapturedImage(it)
            } ?: showToast("No image selected.")
        }

        // Camera launcher
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                capturedImageUri?.let {
                    displayCapturedImage(it)
                    showToast("Photo captured successfully!")
                }
            } else {
                showToast("Photo capture failed.")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_scan, container, false)

        btnGallery = view.findViewById(R.id.buttonGallery)
        btnCamera = view.findViewById(R.id.buttonCamera)
        btnScan = view.findViewById(R.id.buttonScan)
        imageView = view.findViewById(R.id.scanImage)

        btnCamera.setOnClickListener {
            checkCameraPermission()
        }

        btnGallery.setOnClickListener {
            pickImageFromGallery()
        }

        btnScan.setOnClickListener {
            capturedImageUri?.let {
                showToast("Uploading image...")
                // Add upload logic here
            } ?: showToast("No image to upload.")
        }

        return view
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showToast("Camera permission is required to capture photos.")
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
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
            showToast("Failed to create MediaStore entry.")
        }
    }

    private fun pickImageFromGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun displayCapturedImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(imageView)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
