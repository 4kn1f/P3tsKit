package com.application.p3tskit.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.application.p3tskit.MainActivity
import com.application.p3tskit.data.pref.AuthModel
import com.application.p3tskit.data.pref.AuthPreferences
import com.application.p3tskit.databinding.ActivityLoginBinding
import com.application.p3tskit.ui.ViewModelFactory
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import android.util.Patterns

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authPreferences: AuthPreferences
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authPreferences = AuthPreferences.getInstance(this)

        setupLogin()
        observeViewModel()
    }

    private fun observeViewModel() {
        loginViewModel.loginResponse.observe(this) { loginResult ->
            if (loginResult != null) {
                val userModel = AuthModel(
                    email = binding.emailEditText.text.toString(),
                    token = loginResult.token ?: "",
                    isLogin = true
                )

                lifecycleScope.launch {
                    authPreferences.saveSession(userModel)
                }

                showSuccess("Login successful!")
            }
        }

        loginViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        loginViewModel.errorMessage.observe(this) { errorMessage ->
            when {
                errorMessage.contains("Email") -> binding.emailEditTextLayout.error = errorMessage
                errorMessage.contains("Password") -> binding.passwordEditTextLayout.error = errorMessage
                errorMessage.isNotEmpty() -> showError("Login Failed")
            }
        }
    }

    private fun setupLogin() {
        binding.apply {
            loginButton.setOnClickListener {
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailEditTextLayout.error = "Please enter a valid email address"
                    } else {
                        loginViewModel.login(email, password)
                    }
                } else {
                    showError("Please fill in all fields")
                }
            }

            setFormFocus(emailEditText, emailEditTextLayout)
            setFormFocus(passwordEditText, passwordEditTextLayout)
        }
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setFormFocus(edt: EditText, layout: TextInputLayout) {
        edt.setOnFocusChangeListener { _, focus ->
            if (focus) {
                layout.error = null
            }
        }
    }
}