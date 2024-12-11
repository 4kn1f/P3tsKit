package com.application.p3tskit.ui.about

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.application.p3tskit.R
import com.application.p3tskit.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spinner: Spinner = binding.spinnerOptions

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.spinner_items,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
//                        binding.cardLearningPath1.visibility = View.VISIBLE
//                        binding.cardLearningPath2.visibility = View.VISIBLE
//                        binding.cardLearningPath3.visibility = View.VISIBLE
                    }
                    1 -> {
//                        binding.cardLearningPath1.visibility = View.VISIBLE
//                        binding.cardLearningPath2.visibility = View.GONE
//                        binding.cardLearningPath3.visibility = View.GONE
                    }
                    2 -> {
//                        binding.cardLearningPath1.visibility = View.GONE
//                        binding.cardLearningPath2.visibility = View.VISIBLE
//                        binding.cardLearningPath3.visibility = View.GONE
                    }
                    3 -> {
//                        binding.cardLearningPath1.visibility = View.GONE
//                        binding.cardLearningPath2.visibility = View.GONE
//                        binding.cardLearningPath3.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
