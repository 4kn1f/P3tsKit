package com.application.p3tskit.ui.scan

import ModelScanResponse
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.application.p3tskit.R
import com.bumptech.glide.Glide

class DetailScanFragment : Fragment() {

    private lateinit var diseaseInfoTextView: TextView
    private lateinit var symptomsTextView: TextView
    private lateinit var treatmentTextView: TextView
    private lateinit var predictedClassTextView: TextView
    private lateinit var imageView: ImageView
    private lateinit var causeTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var sourceTextView: TextView
    private lateinit var noteTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_scan_page, container, false)

        diseaseInfoTextView = view.findViewById(R.id.tv_result_description)
        symptomsTextView = view.findViewById(R.id.symptoms)
        treatmentTextView = view.findViewById(R.id.tv_treatment)
        predictedClassTextView = view.findViewById(R.id.tv_cause)
        imageView = view.findViewById(R.id.result_image)
        causeTextView = view.findViewById(R.id.tv_cause)
        descriptionTextView = view.findViewById(R.id.tv_result_description)
        sourceTextView = view.findViewById(R.id.tv_source)
        noteTextView = view.findViewById(R.id.tv_note)

        val scanResult: ModelScanResponse? = arguments?.getParcelable("scan_result")
        val imageUri: Uri? = arguments?.getParcelable("image_uri")

        scanResult?.let { result ->

            val diseaseInfo = result.diseaseInfo

            descriptionTextView.text = diseaseInfo?.description ?: "Description not available"
            symptomsTextView.text = diseaseInfo?.symptoms?.joinToString("\n")?.takeIf { it.isNotEmpty() }
                ?: "No symptoms available"
            treatmentTextView.text = diseaseInfo?.treatment?.joinToString("\n")?.takeIf { it.isNotEmpty() }
                ?: "No treatment available"
            predictedClassTextView.text = "Diagnosis: ${result.predictedClass ?: "Not available"}"
            noteTextView.text = diseaseInfo?.note ?: "No additional notes"
            sourceTextView.text = diseaseInfo?.source?.joinToString("\n")?.takeIf { it.isNotEmpty() }
                ?: "No sources available"

            imageUri?.let { uri ->
                Glide.with(this)
                    .load(uri)
                    .into(imageView)
            }
        }

        return view
    }
}
