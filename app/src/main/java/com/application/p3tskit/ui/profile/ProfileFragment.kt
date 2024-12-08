package com.application.p3tskit.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.application.p3tskit.R
import com.application.p3tskit.data.pref.AuthPreferences
import com.application.p3tskit.data.pref.dataStore
import com.application.p3tskit.databinding.FragmentProfileBinding
import com.application.p3tskit.ui.ViewModelFactory
import com.application.p3tskit.ui.register.RegisterActivity
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        observeViewModel()
        setupLogoutButton()

        return binding.root
    }

    private fun setupLogoutButton() {
        binding.llLogout.setOnClickListener {
            lifecycleScope.launch {
                profileViewModel.logout()
                AuthPreferences.getInstance(requireContext()).logout()
            }
        }
    }

    private fun observeViewModel() {
        profileViewModel.getSession().observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvUserName.text = it.email
            }
        }

        profileViewModel.logout.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut) {
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }
}